package com.luixtech.springbootframework.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Refer to <a href="https://juejin.cn/post/7122014462181113887">WEB实时消息推送7种方案</a>
 */
@Slf4j
public class SseEmitterUtils {
    /**
     * Cached reused SseEmitters for many users
     */
    private static final Map<String, SseEmitter> USER_EMITTER_CACHE = new ConcurrentHashMap<>();

    /**
     * Create a new user connection and return SseEmitter
     *
     * @param userId user ID
     * @return SseEmitter object
     */
    public static SseEmitter connect(String userId) {
        if (USER_EMITTER_CACHE.containsKey(userId)) {
            return USER_EMITTER_CACHE.get(userId);
        }
        try {
            // Set timeout period, 0 indicates no expiry. Default is 30 seconds.
            SseEmitter sseEmitter = new SseEmitter(0L);
            sseEmitter.onCompletion(completionCallback(userId));
            sseEmitter.onError(errorCallback(userId));
            sseEmitter.onTimeout(timeoutCallback(userId));
            USER_EMITTER_CACHE.put(userId, sseEmitter);
            return sseEmitter;
        } catch (Exception e) {
            log.error("Failed to create SseEmitter connection for user ID: " + userId, e);
        }
        return null;
    }

    /**
     * Send message to the specified user
     *
     * @param userId  user ID
     * @param message message
     */
    public static void sendMessage(String userId, String message) {
        if (!USER_EMITTER_CACHE.containsKey(userId)) {
            return;
        }
        try {
            USER_EMITTER_CACHE.get(userId).send(message);
        } catch (IOException e) {
            log.error("Failed to send message to user ID: " + userId, e);
            removeUser(userId);
        }
    }

    /**
     * Send message to the specified users
     *
     * @param userIds users IDs
     * @param message message
     */
    public static void batchSendMessages(Set<String> userIds, String message) {
        userIds.forEach(userId -> sendMessage(userId, message));
    }

    /**
     * Send message to the connected users under the same group
     *
     * @param groupId group ID
     * @param message message
     */
    public static void sendGroupMessages(String groupId, String message) {
        if (MapUtils.isEmpty(USER_EMITTER_CACHE)) {
            return;
        }
        USER_EMITTER_CACHE.forEach((userId, emitter) -> {
            try {
                if (userId.startsWith(groupId)) {
                    emitter.send(message, MediaType.APPLICATION_JSON);
                }
            } catch (IOException e) {
                log.error("Failed to send message to user ID: " + userId, e);
                removeUser(userId);
            }
        });
    }

    /**
     * Send message to all connected users
     */
    public static void sendMessagesToALl(String message) {
        USER_EMITTER_CACHE.forEach((userId, emitter) -> {
            try {
                emitter.send(message, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                log.error("Failed to send message to user ID: " + userId, e);
                removeUser(userId);
            }
        });
    }

    /**
     * Get all user IDs
     */
    public static Set<String> getUserIds() {
        return USER_EMITTER_CACHE.keySet();
    }

    /**
     * Process completion callback
     *
     * @param userId user ID
     * @return an Runnable
     */
    private static Runnable completionCallback(String userId) {
        return () -> {
            log.info("Completed with user ID: {}", userId);
            removeUser(userId);
        };
    }

    /**
     * Process timeout callback
     *
     * @param userId user ID
     * @return an Runnable
     */
    private static Runnable timeoutCallback(String userId) {
        return () -> {
            log.info("Process timeout with user ID: {}", userId);
            removeUser(userId);
        };
    }

    /**
     * Process error callback
     *
     * @param userId user ID
     * @return an Runnable
     */
    private static Consumer<Throwable> errorCallback(String userId) {
        return throwable -> {
            log.info("Process error with user ID: {}", userId);
            removeUser(userId);
        };
    }

    /**
     * Remove user connection
     *
     * @param userId user ID
     */
    public static void removeUser(String userId) {
        USER_EMITTER_CACHE.remove(userId);
        log.info("Removed user ID: {}", userId);
    }
}