package com.luixtech.springbootframework.config.apidoc;

import com.luixtech.springbootframework.config.LuixProperties;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;

/**
 * OpenAPI configuration.
 * <p>
 * Warning! When having a lot of REST endpoints, OpenApi can become a performance issue.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(OpenAPI.class)
@AutoConfigureBefore(SpringDocConfiguration.class)
@AutoConfigureAfter(LuixProperties.class)
@Import(SpringDocGroupsConfiguration.class)
@ConditionalOnProperty(SPRINGDOC_ENABLED)
public class SpringDocAutoConfiguration {
}
