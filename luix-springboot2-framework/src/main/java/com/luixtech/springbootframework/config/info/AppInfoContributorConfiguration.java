package com.luixtech.springbootframework.config.info;

import com.luixtech.springbootframework.config.LuixProperties;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor;
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorAutoConfiguration;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Auto-configuration for custom {@link org.springframework.boot.actuate.info.InfoContributor}s.
 */
@Configuration
@AutoConfigureAfter(InfoContributorAutoConfiguration.class)
@ConditionalOnClass(InfoContributor.class)
public class AppInfoContributorConfiguration {

    /**
     * <p>activeProfilesInfoContributor.</p>
     *
     * @param env            a {@link org.springframework.core.env.ConfigurableEnvironment} object.
     * @param luixProperties a {@link com.luixtech.springbootframework.config.LuixProperties} object.
     * @return a {@link AppInfoContributor} object.
     */
    @Bean
    @ConditionalOnEnabledInfoContributor("active-profiles")
    public AppInfoContributor activeProfilesInfoContributor(ConfigurableEnvironment env,
                                                            SpringDocConfigProperties springDocConfigProperties,
                                                            LuixProperties luixProperties) {
        return new AppInfoContributor(env, springDocConfigProperties, luixProperties);
    }
}
