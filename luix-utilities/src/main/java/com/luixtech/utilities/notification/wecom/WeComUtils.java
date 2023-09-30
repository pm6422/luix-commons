package com.luixtech.utilities.notification.wecom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;

/**
 * Refer to
 * https://itwake.blog.csdn.net/article/details/122043499
 * https://blog.csdn.net/whzhaochao/article/details/130512864
 */
@Slf4j
public abstract class WeComUtils {
    private static final String ROBOT_WEBHOOK_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key={0}";

    @SneakyThrows
    public static HttpResponse<String> sendMessage(String webhookKey, Duration timeout, WeComRobotMsgRequest requestBody) {
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MessageFormat.format(ROBOT_WEBHOOK_URL, webhookKey)))
                .timeout(timeout)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        log.info("Sending to WeCom robot with request: {}", requestJson);
        HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();
        log.info("Sent to WeCom robot with response code: {} and body: {}", response.statusCode(), response.body());
        return response;
    }
}
