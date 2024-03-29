package com.luixtech.springbootframework.config.apidoc;

import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@ConditionalOnProperty("springdoc.api-docs.enabled")
public class OpenApiEndpointConfiguration {

    @Value("${spring.application.name}")
    private String appName;

    /**
     * <p>OpenApiEndpoint</p>
     *
     * @param springDocConfigProperties a {@link SpringDocConfigProperties} object.
     * @return a {@link OpenApiEndpoint} object.
     */
    @Bean
    @ConditionalOnAvailableEndpoint
    public OpenApiEndpoint openApiEndpoint(Optional<SpringDocConfigProperties> springDocConfigProperties) {
        return springDocConfigProperties
                .map(docConfigProperties -> new OpenApiEndpoint(docConfigProperties, appName))
                .orElse(null);
    }
}