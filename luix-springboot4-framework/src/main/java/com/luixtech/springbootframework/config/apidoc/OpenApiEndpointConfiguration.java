package com.luixtech.springbootframework.config.apidoc;

import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * <p>OpenApiEndpointConfiguration class.</p>
 */
@Configuration
@ConditionalOnClass(SpringDocConfigProperties.class)
@AutoConfigureAfter(SpringDocAutoConfiguration.class)
public class OpenApiEndpointConfiguration {

    /**
     * <p>openApiEndpoint.</p>
     *
     * @param springDocConfigProperties a {@link org.springdoc.core.properties.SpringDocConfigProperties} object.
     * @return a {@link OpenApiEndpoint} object.
     */
    @Bean
    @ConditionalOnBean({SpringDocConfigProperties.class})
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public OpenApiEndpoint openApiEndpoint(Optional<SpringDocConfigProperties> springDocConfigProperties,
                                           @Value("${spring.application.name:application}") String appName) {
        return springDocConfigProperties
                .map(docConfigProperties -> new OpenApiEndpoint(docConfigProperties, appName))
                .orElse(null);
    }
}
