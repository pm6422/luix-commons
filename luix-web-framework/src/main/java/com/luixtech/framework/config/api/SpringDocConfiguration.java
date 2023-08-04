package com.luixtech.framework.config.api;

import com.luixtech.framework.config.LuixProperties;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.customizers.ActuatorOpenApiCustomizer;
import org.springdoc.core.customizers.ActuatorOperationCustomizer;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.ByteBuffer;
import java.util.List;

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
    public static final String                 AUTH                    = "basicAuth";
    public static final String                 MANAGEMENT_GROUP_NAME   = "management";
    static final        String                 MANAGEMENT_TITLE_SUFFIX = "Management API";
    static final        String                 MANAGEMENT_DESCRIPTION  = "Management endpoints documentation";
    private final       LuixProperties.ApiDocs apiDocsProperties;
    private final       BuildProperties        buildProperties;
    @Value("${spring.application.name}")
    private             String                 appName;

    static {
        SpringDocUtils.getConfig().replaceWithClass(ByteBuffer.class, String.class);
    }

    public SpringDocConfiguration(LuixProperties applicationProperties,
                                  @Autowired(required = false) BuildProperties buildProperties) {
        this.apiDocsProperties = applicationProperties.getApiDocs();
        this.buildProperties = buildProperties;
    }

    /**
     * OpenApi Customizer
     *
     * @return the Customizer
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        OpenApiCustomizer openApiCustomizer = new OpenApiCustomizer(this.apiDocsProperties, this.buildProperties);
        log.debug("Initialized OpenApi customizer");
        return openApiCustomizer;
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
                .group("api")
                .pathsToMatch(apiDocsProperties.getApiIncludePattern());
        openApiCustomizers.stream()
                .filter(customizer -> !(customizer instanceof ActuatorOpenApiCustomizer))
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
                .group("open-api")
                .pathsToMatch(apiDocsProperties.getOpenApiIncludePattern());
        openApiCustomizers.stream()
                .filter(customizer -> !(customizer instanceof ActuatorOpenApiCustomizer))
                .forEach(builder::addOpenApiCustomiser);
        operationCustomizers.stream()
                .filter(customizer -> !(customizer instanceof ActuatorOperationCustomizer))
                .forEach(builder::addOperationCustomizer);
        log.debug("Initialized open-api group");
        return builder.build();
    }

    /**
     * OpenApi management group configuration for the management endpoints (actuator) OpenAPI docs.
     *
     * @return the GroupedOpenApi configuration
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties")
    @ConditionalOnProperty(SPRINGDOC_SHOW_ACTUATOR)
    public GroupedOpenApi managementGroupedOpenApi(ActuatorOpenApiCustomizer actuatorOpenApiCustomizer,
                                                   ActuatorOperationCustomizer actuatorCustomizer) {
        String version = buildProperties == null
                ? "Unknown" :
                defaultIfEmpty(buildProperties.getVersion(), apiDocsProperties.getVersion());

        GroupedOpenApi groupedOpenApi = GroupedOpenApi.builder()
                .group(MANAGEMENT_GROUP_NAME)
                .addOpenApiCustomiser(openApi -> openApi.info(new Info()
                        .title(StringUtils.capitalize(appName) + " " + MANAGEMENT_TITLE_SUFFIX)
                        .description(MANAGEMENT_DESCRIPTION)
                        .version(version)
                ))
                .addOpenApiCustomiser(actuatorOpenApiCustomizer)
                .addOperationCustomizer(actuatorCustomizer)
                .pathsToMatch(apiDocsProperties.getManagementIncludePattern())
                .build();
        log.debug("Initialized management group");
        return groupedOpenApi;
    }
}