package com.luixtech.uidgenerator.usagedemo.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Properties specific to Application.
 *
 * <p>
 * Properties are configured in the application.yml file.
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Validated
@Getter
public class ApplicationProperties {
    private final ApiDocs apiDocs = new ApiDocs();

    @Data
    public static class ApiDocs {
        private String   apiIncludePattern        = "/api/**";
        private String   openApiIncludePattern    = "/open-api/**";
        private String   managementIncludePattern = "/management/**";
        private String   title;
        private String   description              = "API documentation";
        private String   version;
        private String   termsOfServiceUrl;
        private String   contactName;
        private String   contactUrl;
        private String   contactEmail;
        private String   license;
        private String   licenseUrl;
        private Server[] servers                  = new Server[0];

        @Data
        public static class Server {
            private String url;
            private String description;
        }
    }
}
