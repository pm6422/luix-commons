package com.luixtech.springboot2framework.config.info;

import com.luixtech.springboot2framework.config.LuixProperties;
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
public class InfoContributorConfiguration {

    /**
     * <p>activeProfilesInfoContributor.</p>
     *
     * @param env            a {@link org.springframework.core.env.ConfigurableEnvironment} object.
     * @param luixProperties a {@link com.luixtech.springboot2framework.config.LuixProperties} object.
     * @return a {@link RibbonInfoContributor} object.
     */
    @Bean
    @ConditionalOnEnabledInfoContributor("active-profiles")
    public RibbonInfoContributor activeProfilesInfoContributor(ConfigurableEnvironment env, LuixProperties luixProperties) {
        return new RibbonInfoContributor(env, luixProperties);
    }
}
