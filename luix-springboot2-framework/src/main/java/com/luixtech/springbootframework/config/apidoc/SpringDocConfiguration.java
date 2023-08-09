package com.luixtech.springbootframework.config.apidoc;

import com.luixtech.springbootframework.config.LuixProperties;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.customizers.ActuatorOperationCustomizer;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.springdoc.core.Constants.SPRINGDOC_SHOW_ACTUATOR;

@Slf4j
@ConditionalOnProperty("springdoc.api-docs.enabled")
@Configuration
@SecurityScheme(
        name = SpringDocConfiguration.AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class SpringDocConfiguration {
    public static final String                    AUTH                  = "basicAuth";
    public static final String                    API_GROUP_NAME        = "api";
    public static final String                    OPEN_API_GROUP_NAME   = "open-api";
    public static final String                    MANAGEMENT_GROUP_NAME = "management";
    private final       LuixProperties.ApiDocs    apiDocsProperties;
    private final       Optional<BuildProperties> buildProperties;
    @Value("${spring.application.name}")
    private             String                    appName;

    static {
        SpringDocUtils.getConfig().replaceWithClass(ByteBuffer.class, String.class);
    }

    public SpringDocConfiguration(LuixProperties luixProperties, Optional<BuildProperties> buildProperties) {
        this.apiDocsProperties = luixProperties.getApiDocs();
        this.buildProperties = buildProperties;
    }

    /**
     * Api Customizer
     *
     * @return the Customizer
     */
    @Bean
    public NamedApiCustomizer apiCustomizer() {
        NamedApiCustomizer customizer = new NamedApiCustomizer(this.apiDocsProperties,
                "api-customizer", apiDocsProperties.getApiTitle(),
                apiDocsProperties.getApiDescription(), getVersion());
        log.debug("Initialized Api customizer");
        return customizer;
    }

    /**
     * OpenApi Customizer
     *
     * @return the Customizer
     */
    @Bean
    public NamedApiCustomizer openApiCustomizer() {
        NamedApiCustomizer customizer = new NamedApiCustomizer(this.apiDocsProperties,
                "open-api-customizer", apiDocsProperties.getOpenApiTitle(),
                apiDocsProperties.getOpenApiDescription(), getVersion());
        return customizer;
    }

    /**
     * Management Customizer
     *
     * @return the Customizer
     */
    @Bean
    public NamedApiCustomizer managementCustomizer() {
        NamedApiCustomizer customizer = new NamedApiCustomizer(this.apiDocsProperties,
                "management-customizer", apiDocsProperties.getManagementTitle(),
                apiDocsProperties.getManagementDescription(), getVersion());
        return customizer;
    }

    /**
     * api group configuration.
     *
     * @return the GroupedOpenApi configuration
     */
    @Bean
    public GroupedOpenApi apiGroup(List<OpenApiCustomiser> openApiCustomizers,
                                   List<OperationCustomizer> operationCustomizers) {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
                .group(API_GROUP_NAME)
                .pathsToMatch(apiDocsProperties.getApiIncludePattern());

        openApiCustomizers.stream()
                .filter(customizer -> customizer instanceof NamedApiCustomizer
                        && ((NamedApiCustomizer) customizer).getName().equals("api-customizer"))
                .forEach(builder::addOpenApiCustomiser);
        operationCustomizers.stream()
                .filter(customizer -> !(customizer instanceof ActuatorOperationCustomizer))
                .forEach(builder::addOperationCustomizer);
        log.debug("Initialized api group");
        return builder.build();
    }

    /**
     * open-api group configuration.
     *
     * @return the GroupedOpenApi configuration
     */
    @Bean
    public GroupedOpenApi openApiGroup(List<OpenApiCustomiser> openApiCustomizers,
                                       List<OperationCustomizer> operationCustomizers) {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
                .group(OPEN_API_GROUP_NAME)
                .pathsToMatch(apiDocsProperties.getOpenApiIncludePattern());

        openApiCustomizers.stream()
                .filter(customizer -> customizer instanceof NamedApiCustomizer
                        && ((NamedApiCustomizer) customizer).getName().equals("open-api-customizer"))
                .forEach(builder::addOpenApiCustomiser);
        operationCustomizers.stream()
                .filter(customizer -> !(customizer instanceof ActuatorOperationCustomizer))
                .forEach(builder::addOperationCustomizer);
        log.debug("Initialized open-api group");
        return builder.build();
    }

    /**
     * management group configuration.
     *
     * @return the GroupedOpenApi configuration
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties")
    @ConditionalOnProperty(SPRINGDOC_SHOW_ACTUATOR)
    public GroupedOpenApi managementGroupedOpenApi(List<OpenApiCustomiser> openApiCustomizers,
                                                   List<OperationCustomizer> operationCustomizers) {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
                .group(MANAGEMENT_GROUP_NAME)
                .pathsToMatch(apiDocsProperties.getManagementIncludePattern());

        openApiCustomizers.stream()
                .filter(customizer -> customizer instanceof NamedApiCustomizer
                        && ((NamedApiCustomizer) customizer).getName().equals("management-customizer"))
                .forEach(builder::addOpenApiCustomiser);
        operationCustomizers.stream()
                .filter(customizer -> !(customizer instanceof ActuatorOperationCustomizer))
                .forEach(builder::addOperationCustomizer);
        log.debug("Initialized management group");
        return builder.build();
    }

    private String getVersion() {
        return buildProperties.isPresent()
                ? defaultIfEmpty(buildProperties.get().getVersion(), apiDocsProperties.getVersion())
                : "Unknown";
    }
}