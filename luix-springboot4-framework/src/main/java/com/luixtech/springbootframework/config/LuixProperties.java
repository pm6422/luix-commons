package com.luixtech.springbootframework.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Properties specific to Application.
 *
 * <p>
 * Properties are configured in the application.yml file.
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "luix")
@Validated
@Getter
public class LuixProperties {
    public static final String             SPRING_PROFILE_DEV  = "dev";
    public static final String             SPRING_PROFILE_TEST = "test";
    public static final String             SPRING_PROFILE_DEMO = "demo";
    public static final String             SPRING_PROFILE_PROD = "prod";
    private final       Http               http                = new Http();
    private final       ApiDocs            apiDocs             = new ApiDocs();
    private final       Metrics            metrics             = new Metrics();
    private final       AopLogging         aopLogging          = new AopLogging();
    private final       ElapsedTimeLogging elapsedTimeLogging  = new ElapsedTimeLogging();
    private final       Ribbon             ribbon              = new Ribbon();
    private final       AppInfo            appInfo             = new AppInfo();

    /**
     * HTTP properties.
     */
    @Data
    public static class Http {
        private final Cache cache = new Cache();

        /**
         * Create a new instance.
         */
        public Http() {
        }

        /**
         * HTTP cache properties.
         */
        @Data
        public static class Cache {
            /**
             * Expired days
             */
            private Long expiredAfter = 31L;

            /**
             * Create a new instance.
             */
            public Cache() {
            }
        }
    }

    /**
     * API documentation properties.
     */
    @Data
    public static class ApiDocs {
        private String   apiIncludePattern        = "/api/**";
        private String   openApiIncludePattern    = "/open-api/**";
        private String   managementIncludePattern = "/management/**";
        private String   apiTitle;
        private String   apiDescription           = "API documentation";
        private String   openApiTitle;
        private String   openApiDescription       = "OpenAPI documentation";
        private String   managementTitle;
        private String   managementDescription    = "Management documentation";
        private String   version;
        private String   termsOfServiceUrl;
        private String   contactName;
        private String   contactUrl;
        private String   contactEmail;
        private String   contactMobile;
        private String   license;
        private String   licenseUrl;
        private Server[] servers                  = new Server[0];

        /**
         * Create a new instance.
         */
        public ApiDocs() {
        }

        /**
         * OpenAPI server properties.
         */
        @Data
        public static class Server {
            private String url;
            private String description;

            /**
             * Create a new instance.
             */
            public Server() {
            }
        }
    }

    /**
     * Metrics properties.
     */
    @Data
    public static class Metrics {
        private final Logs     logs     = new Logs();
        private final Graphite graphite = new Graphite();

        /**
         * Create a new instance.
         */
        public Metrics() {
        }

        /**
         * Spark metrics properties.
         */
        @Data
        public static class Spark {
            private boolean enabled = false;
            private String  host    = "localhost";
            private int     port    = 9999;

            /**
             * Create a new instance.
             */
            public Spark() {
            }
        }

        /**
         * Graphite metrics properties.
         */
        @Data
        public static class Graphite {
            private boolean enabled = false;
            private String  host    = "localhost";
            private int     port    = 2003;
            private String  prefix  = "";

            /**
             * Create a new instance.
             */
            public Graphite() {
            }
        }

        /**
         * Log reporting metrics properties.
         */
        @Data
        public static class Logs {
            private boolean enabled         = false;
            private int     reportFrequency = 60;

            /**
             * Create a new instance.
             */
            public Logs() {
            }
        }
    }

    /**
     * AOP logging properties.
     */
    @Data
    public static class AopLogging {
        private boolean      enabled;
        private boolean      methodBlacklistMode;
        private List<String> methodBlacklist;

        /**
         * Create a new instance.
         */
        public AopLogging() {
        }
    }

    /**
     * Elapsed time logging properties.
     */
    @Data
    public static class ElapsedTimeLogging {
        private boolean enabled;
        private int     slowExecutionThreshold;

        /**
         * Create a new instance.
         */
        public ElapsedTimeLogging() {
        }
    }

    /**
     * Ribbon properties.
     */
    @Data
    public static class Ribbon {
        private List<String> displayOnActiveProfiles;

        /**
         * Create a new instance.
         */
        public Ribbon() {
        }
    }

    /**
     * Application info properties.
     */
    @Data
    public static class AppInfo {
        private boolean printEnabled = true;

        /**
         * Create a new instance.
         */
        public AppInfo() {
        }
    }
}
