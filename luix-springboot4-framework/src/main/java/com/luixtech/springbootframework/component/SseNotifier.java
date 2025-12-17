package com.luixtech.springbootframework.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SseNotifier implements InitializingBean, DisposableBean {

    private final Map<String, SseEmitter>  userEmitterCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor  = new ScheduledThreadPoolExecutor(1);

    @Autowired(required = false)
    private SseDistributor sseDistributor;

    @Override
    public void afterPropertiesSet() {
        if (sseDistributor == null) {
            // Default to local distribution if no distributed implementation is provided
            sseDistributor = new SseDistributor() {
                @Override
                public void distribute(SseMessage message) {
                    sendLocal(message);
                }

                @Override
                public void broadcast(SseMessage message) {
                    broadcastLocal(message);
                }
            };
            log.info("No SseDistributor bean found, using default local distribution.");
        }
        // Schedule periodic cleanup every 5 minutes
        cleanupExecutor.scheduleAtFixedRate(this::cleanUpExpiredConnections, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * Interface for handling distributed SSE message distribution.
     * Implement this interface to support distributed environments (e.g., using
     * Kafka or Redis).
     *
     * <p>
     * Example implementation using Kafka:
     * </p>
     *
     * <pre>
     * {
     *   &#64;code
     *   &#64;Component
     *   public class KafkaSseDistributor implements SseNotifier.SseDistributor {
     *     &#64;Autowired
     *     private KafkaTemplate<String, String> kafkaTemplate;
     *
     *     &#64;Override
     *     public void distribute(String userId, String content) {
     *       Map<String, Object> message = Map.of("type", "UNICAST", "userId", userId, "content", content);
     *       kafkaTemplate.send("sse-topic", JSON.toJSONString(message));
     *     }
     *
     *     @Override
     *     public void broadcast(String content) {
     *       Map<String, Object> message = Map.of("type", "BROADCAST", "content", content);
     *       kafkaTemplate.send("sse-topic", JSON.toJSONString(message));
     *     }
     *   }
     * }
     * </pre>
     */
    public interface SseDistributor {
        /**
         * Distribute a message to specific users across the cluster.
         *
         * @param message message object containing target user IDs
         */
        void distribute(SseMessage message);

        /**
         * Broadcast a message to all users across the cluster.
         *
         * @param message message object
         */
        void broadcast(SseMessage message);
    }

    /**
     * Creates a new SSE connection for the specified user
     *
     * @param userId unique identifier of the user
     * @return SseEmitter instance
     */
    public SseEmitter connect(String userId) {
        if (userEmitterCache.containsKey(userId)) {
            return userEmitterCache.get(userId);
        }
        // 0L means no timeout
        SseEmitter sseEmitter = new SseEmitter(0L);
        sseEmitter.onCompletion(() -> removeUser(userId));
        sseEmitter.onError((e) -> removeUser(userId));
        sseEmitter.onTimeout(() -> removeUser(userId));
        userEmitterCache.put(userId, sseEmitter);
        log.debug("Created new SSE connection for user: {}", userId);
        return sseEmitter;
    }

    /**
     * Sends a message to a specific user.
     *
     * @param userId target user ID
     * @param data   message data
     */
    public void send(String userId, Object data) {
        send(new SseMessage(userId, data));
    }

    /**
     * Sends a message to multiple users.
     *
     * @param userIds target user IDs
     * @param data    message data
     */
    public void send(Collection<String> userIds, Object data) {
        SseMessage message = new SseMessage(data);
        message.setUserIds(new HashSet<>(userIds));
        send(message);
    }

    /**
     * Sends a rich SSE message (with id, name, etc.) to specified users.
     *
     * @param message SSE message including target user IDs
     */
    public void send(SseMessage message) {
        if (CollectionUtils.isEmpty(message.getUserIds())) {
            log.warn("No target users specified for SSE message");
            return;
        }
        sseDistributor.distribute(message);
    }

    /**
     * Broadcasts a message to all users.
     * Delegates to the configured SseDistributor (defaults to local if none
     * configured).
     *
     * @param data message data
     */
    public void broadcast(Object data) {
        sseDistributor.broadcast(new SseMessage(data));
    }

    /**
     * Broadcasts a richer SSE message to all users.
     *
     * @param message SSE message
     */
    public void broadcast(SseMessage message) {
        sseDistributor.broadcast(message);
    }

    /**
     * Sends a message to users connected to this local instance.
     *
     * @param message message object containing target user IDs
     */
    private void sendLocal(SseMessage message) {
        if (CollectionUtils.isEmpty(message.getUserIds())) {
            return;
        }
        for (String userId : message.getUserIds()) {
            SseEmitter emitter = userEmitterCache.get(userId);
            if (emitter != null) {
                try {
                    emitter.send(buildSseEvent(message));
                } catch (IOException e) {
                    log.error("Failed to send message to user: {}", userId, e);
                    removeUser(userId);
                }
            }
        }
    }

    /**
     * Broadcasts a message to all users connected to this local instance.
     * This method should be called by the SseDistributor implementation when a
     * broadcast message is received.
     *
     * @param message message object
     */
    private void broadcastLocal(SseMessage message) {
        userEmitterCache.forEach((userId, emitter) -> {
            try {
                emitter.send(buildSseEvent(message));
            } catch (IOException e) {
                log.error("Failed to broadcast message to user: {}", userId, e);
                removeUser(userId);
            }
        });
    }

    private SseEmitter.SseEventBuilder buildSseEvent(SseMessage message) {
        SseEmitter.SseEventBuilder builder = SseEmitter.event().data(message.getData());
        if (message.getId() != null) {
            builder.id(message.getId());
        }
        if (message.getName() != null) {
            builder.name(message.getName());
        }
        if (message.getReconnectTime() != null) {
            builder.reconnectTime(message.getReconnectTime());
        }
        return builder;
    }

    public void removeUser(String userId) {
        if (userEmitterCache.remove(userId) != null) {
            log.debug("Removed SSE connection for user: {}", userId);
        }
    }

    private void cleanUpExpiredConnections() {
        int initialSize = userEmitterCache.size();
        userEmitterCache.entrySet().removeIf(entry -> {
            SseEmitter emitter = entry.getValue();
            try {
                emitter.send(SseEmitter.event().comment("ping"));
                return false;
            } catch (IOException e) {
                return true;
            }
        });
        int removed = initialSize - userEmitterCache.size();
        if (removed > 0) {
            log.debug("Cleaned up {} expired SSE connections", removed);
        }
    }

    @Override
    public void destroy() throws Exception {
        cleanupExecutor.shutdown();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SseMessage {
        private Set<String> userIds = new HashSet<>();
        private String      id;
        private String      name;
        private Object      data;
        private Long        reconnectTime;

        public SseMessage(Object data) {
            this.data = data;
        }

        public SseMessage(String userId, Object data) {
            this.userIds.add(userId);
            this.data = data;
        }

        public SseMessage addUserId(String userId) {
            this.userIds.add(userId);
            return this;
        }
    }
}
