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
import java.util.function.Consumer;

/**
 * A server-sent event is when a web page automatically gets updates from a server.
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
     * Push message to the specified connected user
     *
     * @param userId  user ID
     * @param message message
     * @return failed user ID
     */
    public static String pushUserMessage(String userId, String message) {
        if (!USER_EMITTER_CACHE.containsKey(userId)) {
            return null;
        }
        try {
            USER_EMITTER_CACHE.get(userId).send(message);
        } catch (IOException e) {
            log.error("Failed to push message to user: " + userId, e);
            removeUser(userId);
            return userId;
        }
        return null;
    }

    /**
     * Push message to the specified connected users
     *
     * @param userIds users IDs
     * @param message message
     * @return failed user IDs
     */
    public static Set<String> pushUsersMessages(Set<String> userIds, String message) {
        Set<String> failedUserIds = new HashSet<>();
        userIds.forEach(userId -> {
            String failedUserId = pushUserMessage(userId, message);
            if (failedUserId != null) {
                failedUserIds.add(failedUserId);
            }
        });
        return failedUserIds;
    }

    /**
     * Push message to the connected users under the same group
     *
     * @param groupId group ID
     * @param message message
     * @return failed user IDs
     */
    public static Set<String> pushGroupMessages(String groupId, String message) {
        if (MapUtils.isEmpty(USER_EMITTER_CACHE)) {
            return null;
        }
        Set<String> failedUserIds = new HashSet<>();
        USER_EMITTER_CACHE.forEach((userId, emitter) -> {
            try {
                if (userId.startsWith(groupId)) {
                    emitter.send(message, MediaType.APPLICATION_JSON);
                }
            } catch (IOException e) {
                log.error("Failed to Push message to user: " + userId, e);
                removeUser(userId);
                failedUserIds.add(userId);
            }
        });
        return failedUserIds;
    }

    /**
     * Push message to the connected users for specified groups
     *
     * @param groupIds group IDs
     * @param message  message
     * @return failed user IDs
     */
    public static Set<String> pushGroupsMessages(Set<String> groupIds, String message) {
        Set<String> failedUserIds = new HashSet<>();
        groupIds.forEach(groupId -> failedUserIds.addAll(pushGroupMessages(groupId, message)));
        return failedUserIds;
    }

    /**
     * Push message to all online connected users
     *
     * @return failed user IDs
     */
    public static Set<String> pushMessagesToALlConnectedUsers(String message) {
        Set<String> failedUserIds = new HashSet<>();
        USER_EMITTER_CACHE.forEach((userId, emitter) -> {
            try {
                emitter.send(message, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                log.error("Failed to Push message to user: " + userId, e);
                removeUser(userId);
                failedUserIds.add(userId);
            }
        });
        return failedUserIds;
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