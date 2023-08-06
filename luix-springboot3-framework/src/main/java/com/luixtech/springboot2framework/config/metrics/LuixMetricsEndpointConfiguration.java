package com.luixtech.springboot2framework.config.metrics;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Timed.class)
@AutoConfigureAfter(MetricsEndpointAutoConfiguration.class)
public class LuixMetricsEndpointConfiguration {
    /**
     * <p>metricsEndpoint</p>
     *
     * @param meterRegistry a {@link io.micrometer.core.instrument.MeterRegistry} object.
     * @return a {@link LuixMetricsEndpoint} object.
     */
    @Bean
//    @ConditionalOnBean({MeterRegistry.class})
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public LuixMetricsEndpoint luixMetricsEndpoint(MeterRegistry meterRegistry) {
        return new LuixMetricsEndpoint(meterRegistry);
    }
}
