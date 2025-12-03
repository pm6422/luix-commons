package com.luixtech.springbootframework.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SseNotifier implements InitializingBean, DisposableBean {

  private final Map<String, SseEmitter> userEmitterCache = new ConcurrentHashMap<>();
  private final ScheduledExecutorService cleanupExecutor = new ScheduledThreadPoolExecutor(1);

  @Autowired(required = false)
  private SseDistributor sseDistributor;

  @Override
  public void afterPropertiesSet() {
    if (sseDistributor == null) {
      // Default to local distribution if no distributed implementation is provided
      sseDistributor = new SseDistributor() {
        @Override
        public void distribute(String userId, String content) {
          sendLocal(userId, content);
        }

        @Override
        public void broadcast(String content) {
          broadcastLocal(content);
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
     * Distribute a message to a specific user across the cluster.
     *
     * @param userId  target user ID
     * @param content message content
     */
    void distribute(String userId, String content);

    /**
     * Broadcast a message to all users across the cluster.
     *
     * @param content message content
     */
    void broadcast(String content);
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
   * Delegates to the configured SseDistributor (defaults to local if none
   * configured).
   *
   * @param userId  target user ID
   * @param content message content
   */
  public void send(String userId, String content) {
    sseDistributor.distribute(userId, content);
  }

  /**
   * Broadcasts a message to all users.
   * Delegates to the configured SseDistributor (defaults to local if none
   * configured).
   *
   * @param content message content
   */
  public void broadcast(String content) {
    sseDistributor.broadcast(content);
  }

  /**
   * Sends a message to a user connected to this local instance.
   * This method should be called by the SseDistributor implementation when a
   * message is received.
   *
   * @param userId  target user ID
   * @param content message content
   */
  public void sendLocal(String userId, String content) {
    SseEmitter emitter = userEmitterCache.get(userId);
    if (emitter != null) {
      try {
        emitter.send(content);
      } catch (IOException e) {
        log.error("Failed to send message to user: {}", userId, e);
        removeUser(userId);
      }
    }
  }

  /**
   * Broadcasts a message to all users connected to this local instance.
   * This method should be called by the SseDistributor implementation when a
   * broadcast message is received.
   *
   * @param content message content
   */
  public void broadcastLocal(String content) {
    userEmitterCache.forEach((userId, emitter) -> {
      try {
        emitter.send(content);
      } catch (IOException e) {
        log.error("Failed to broadcast message to user: {}", userId, e);
        removeUser(userId);
      }
    });
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
}
