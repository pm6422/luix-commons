package com.luixtech.springbootframework.config.apidoc;

import com.luixtech.springbootframework.config.LuixProperties;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.ActuatorOperationCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.springdoc.core.utils.Constants.SPRINGDOC_SHOW_ACTUATOR;

/**
 * OpenApi Groups configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, OpenApi can become a performance issue.
 */
@Configuration
@Slf4j
public class SpringDocGroupsConfiguration {
    static {
        SpringDocUtils.getConfig().replaceWithClass(ByteBuffer.class, String.class);
    }

    public static final String                    API_GROUP_NAME        = "api";
    public static final String                    OPEN_API_GROUP_NAME   = "open-api";
    public static final String                    MANAGEMENT_GROUP_NAME = "management";
    private final       LuixProperties.ApiDocs    apiDocsProperties;
    private final       Optional<BuildProperties> buildProperties;

    /**
     * <p>Constructor for OpenApiAutoConfiguration.</p>
     *
     * @param luixProperties a {@link LuixProperties} object.
     */
    public SpringDocGroupsConfiguration(LuixProperties luixProperties, Optional<BuildProperties> buildProperties) {
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
                apiDocsProperties.getApiDescription(), getVersion(), 0);
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
                apiDocsProperties.getOpenApiDescription(), getVersion(), 1);
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
                apiDocsProperties.getManagementDescription(), getVersion(), 2);
        return customizer;
    }

    /**
     * api group configuration.
     *
     * @return the GroupedOpenApi configuration
     */
    @Bean
    public GroupedOpenApi apiGroup(List<OpenApiCustomizer> openApiCustomizers,
                                   List<OperationCustomizer> operationCustomizers) {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
                .group(API_GROUP_NAME)
                .pathsToMatch(apiDocsProperties.getApiIncludePattern());

        openApiCustomizers.stream()
                .filter(customizer -> customizer instanceof NamedApiCustomizer
                        && ((NamedApiCustomizer) customizer).getName().equals("api-customizer"))
                .forEach(builder::addOpenApiCustomizer);
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
    public GroupedOpenApi openApiGroup(List<OpenApiCustomizer> openApiCustomizers,
                                       List<OperationCustomizer> operationCustomizers) {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
                .group(OPEN_API_GROUP_NAME)
                .pathsToMatch(apiDocsProperties.getOpenApiIncludePattern());

        openApiCustomizers.stream()
                .filter(customizer -> customizer instanceof NamedApiCustomizer
                        && ((NamedApiCustomizer) customizer).getName().equals("open-api-customizer"))
                .forEach(builder::addOpenApiCustomizer);
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
    public GroupedOpenApi managementGroupedOpenApi(List<OpenApiCustomizer> openApiCustomizers,
                                                   List<OperationCustomizer> operationCustomizers) {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
                .group(MANAGEMENT_GROUP_NAME)
                .pathsToMatch(apiDocsProperties.getManagementIncludePattern());

        openApiCustomizers.stream()
                .filter(customizer -> customizer instanceof NamedApiCustomizer
                        && ((NamedApiCustomizer) customizer).getName().equals("management-customizer"))
                .forEach(builder::addOpenApiCustomizer);
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
