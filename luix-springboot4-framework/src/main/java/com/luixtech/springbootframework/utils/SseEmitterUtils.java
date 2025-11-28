package com.luixtech.springbootframework.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Utility class for managing Server-Sent Events (SSE) connections.
 * Provides methods to create, maintain, and broadcast messages to SSE connections.
 *
 * <p>Server-Sent Events enable automatic updates from server to web clients.
 *
 * @see <a href="https://juejin.cn/post/7122014462181113887">7 Solutions for WEB Real-time Message Push</a>
 */
@Slf4j
public class SseEmitterUtils {
    /**
     * Cache for storing active user SSE connections
     * Key: user ID, Value: SseEmitter instance
     */
    private static final Map<String, SseEmitter> USER_EMITTER_CACHE = new ConcurrentHashMap<>();

    /**
     * Executor service for periodic cleanup of expired connections
     */
    private static final ScheduledExecutorService CLEANUP_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    static {
        // Schedule periodic cleanup every 5 minutes
        CLEANUP_EXECUTOR.scheduleAtFixedRate(SseEmitterUtils::cleanUpExpiredConnections, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * Creates a new SSE connection for the specified user
     *
     * @param userId unique identifier of the user
     * @return SseEmitter instance for the user, or null if creation failed
     */
    public static SseEmitter connect(String userId) {
        // Return existing emitter if present
        if (USER_EMITTER_CACHE.containsKey(userId)) {
            return USER_EMITTER_CACHE.get(userId);
        }

        try {
            // Create new emitter with no timeout (0L means no timeout)
            SseEmitter sseEmitter = new SseEmitter(0L);

            // Register callbacks
            sseEmitter.onCompletion(completionCallback(userId));
            sseEmitter.onError(errorCallback(userId));
            sseEmitter.onTimeout(timeoutCallback(userId));

            // Cache the new emitter
            USER_EMITTER_CACHE.put(userId, sseEmitter);
            log.debug("Created new SSE connection for user: {}", userId);
            return sseEmitter;
        } catch (Exception e) {
            log.error("Failed to create SseEmitter for user: {}", userId, e);
            return null;
        }
    }

    /**
     * Sends a message to a specific user
     *
     * @param userId  target user ID
     * @param message message to send
     * @return failed user ID if sending failed, null otherwise
     */
    public static String pushToUser(String userId, String message) {
        if (!USER_EMITTER_CACHE.containsKey(userId)) {
            return userId;
        }

        try {
            USER_EMITTER_CACHE.get(userId).send(message);
            return null;
        } catch (IOException e) {
            log.error("Message delivery failed for user: {}", userId, e);
            removeUser(userId);
            return userId;
        }
    }

    /**
     * Sends a message to multiple users
     *
     * @param userIds set of target user IDs
     * @param message message to send
     * @return set of user IDs for which delivery failed
     */
    public static Set<String> pushToUsers(Set<String> userIds, String message) {
        Set<String> failedUserIds = new HashSet<>();
        userIds.forEach(userId -> {
            String failedUserId = pushToUser(userId, message);
            if (failedUserId != null) {
                failedUserIds.add(failedUserId);
            }
        });
        return failedUserIds;
    }

    /**
     * Broadcasts a message to all users in a group
     * Group is identified by user IDs starting with the group ID prefix
     *
     * @param groupId group identifier prefix
     * @param message message to broadcast
     * @return set of user IDs for which delivery failed
     */
    public static Set<String> pushToGroup(String groupId, String message) {
        if (MapUtils.isEmpty(USER_EMITTER_CACHE)) {
            return Set.of();
        }

        Set<String> failedUserIds = new HashSet<>();
        USER_EMITTER_CACHE.forEach((userId, emitter) -> {
            if (userId.startsWith(groupId)) {
                try {
                    emitter.send(message, MediaType.APPLICATION_JSON);
                } catch (IOException e) {
                    log.error("Group message delivery failed for user: {}", userId, e);
                    removeUser(userId);
                    failedUserIds.add(userId);
                }
            }
        });
        return failedUserIds;
    }

    /**
     * Broadcasts a message to multiple groups
     *
     * @param groupIds set of group identifier prefixes
     * @param message  message to broadcast
     * @return set of user IDs for which delivery failed
     */
    public static Set<String> pushToGroups(Set<String> groupIds, String message) {
        Set<String> failedUserIds = new HashSet<>();
        groupIds.forEach(groupId ->
                failedUserIds.addAll(pushToGroup(groupId, message)));
        return failedUserIds;
    }

    /**
     * Broadcasts a message to all connected users
     *
     * @param message message to broadcast
     * @return set of user IDs for which delivery failed
     */
    public static Set<String> pushToAll(String message) {
        Set<String> failedUserIds = new HashSet<>();
        USER_EMITTER_CACHE.forEach((userId, emitter) -> {
            try {
                emitter.send(message, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                log.error("Broadcast message delivery failed for user: {}", userId, e);
                removeUser(userId);
                failedUserIds.add(userId);
            }
        });
        return failedUserIds;
    }

    /**
     * Gets the number of active connections
     *
     * @return count of active connections
     */
    public static int getConnectionCount() {
        return USER_EMITTER_CACHE.size();
    }

    /**
     * Gets all connected user IDs
     *
     * @return set of active user IDs
     */
    public static Set<String> getConnectedUsers() {
        return USER_EMITTER_CACHE.keySet();
    }

    /**
     * Checks if a specific user is connected
     *
     * @param userId user ID to check
     * @return true if user is connected, false otherwise
     */
    public static boolean isUserConnected(String userId) {
        return USER_EMITTER_CACHE.containsKey(userId);
    }

    /**
     * Creates a completion callback for SSE emitter
     *
     * @param userId user ID associated with the emitter
     * @return completion callback Runnable
     */
    private static Runnable completionCallback(String userId) {
        return () -> {
            log.debug("SSE connection completed for user: {}", userId);
            removeUser(userId);
        };
    }

    /**
     * Creates a timeout callback for SSE emitter
     *
     * @param userId user ID associated with the emitter
     * @return timeout callback Runnable
     */
    private static Runnable timeoutCallback(String userId) {
        return () -> {
            log.debug("SSE connection timeout for user: {}", userId);
            removeUser(userId);
        };
    }

    /**
     * Creates an error callback for SSE emitter
     *
     * @param userId user ID associated with the emitter
     * @return error callback Consumer
     */
    private static Consumer<Throwable> errorCallback(String userId) {
        return throwable -> {
            log.debug("SSE connection error for user: {}", userId, throwable);
            removeUser(userId);
        };
    }

    /**
     * Removes a user's SSE connection from cache
     *
     * @param userId user ID to remove
     */
    public static void removeUser(String userId) {
        if (USER_EMITTER_CACHE.remove(userId) != null) {
            log.debug("Removed SSE connection for user: {}", userId);
        }
    }

    /**
     * Cleans up expired or dead connections
     */
    public static void cleanUpExpiredConnections() {
        int initialSize = USER_EMITTER_CACHE.size();
        USER_EMITTER_CACHE.entrySet().removeIf(entry -> {
            SseEmitter emitter = entry.getValue();
            return emitter == null || isEmitterDead(emitter);
        });
        int removed = initialSize - USER_EMITTER_CACHE.size();
        if (removed > 0) {
            log.debug("Cleaned up {} expired SSE connections", removed);
        }
    }

    /**
     * Checks if an emitter is no longer active
     *
     * @param emitter SSE emitter to check
     * @return true if emitter is dead, false otherwise
     */
    private static boolean isEmitterDead(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().comment("ping"));
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Shuts down the cleanup executor service
     * Should be called when application is shutting down
     */
    public static void shutdown() {
        CLEANUP_EXECUTOR.shutdown();
        try {
            if (!CLEANUP_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                CLEANUP_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            CLEANUP_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}