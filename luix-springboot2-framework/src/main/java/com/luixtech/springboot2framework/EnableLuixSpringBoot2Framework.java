package com.luixtech.springboot2framework;

import com.luixtech.springboot2framework.aspect.AopLoggingAspect;
import com.luixtech.springboot2framework.aspect.ElapsedTimeLoggingAspect;
import com.luixtech.springboot2framework.aspect.ExceptionTranslatorAdviceAspect;
import com.luixtech.springboot2framework.component.HttpHeaderCreator;
import com.luixtech.springboot2framework.component.MessageCreator;
import com.luixtech.springboot2framework.component.PrintAppInfoApplicationRunner;
import com.luixtech.springboot2framework.config.AsyncConfiguration;
import com.luixtech.springboot2framework.config.LocaleConfiguration;
import com.luixtech.springboot2framework.config.LuixProperties;
import com.luixtech.springboot2framework.config.api.OpenApiEndpointConfiguration;
import com.luixtech.springboot2framework.config.api.SpringDocConfiguration;
import com.luixtech.springboot2framework.config.info.InfoContributorConfiguration;
import com.luixtech.springboot2framework.config.metrics.LuixMetricsEndpointConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AopLoggingAspect.class, ElapsedTimeLoggingAspect.class, ExceptionTranslatorAdviceAspect.class,
        HttpHeaderCreator.class, MessageCreator.class, PrintAppInfoApplicationRunner.class,
        AsyncConfiguration.class, LocaleConfiguration.class, LuixProperties.class, OpenApiEndpointConfiguration.class,
        SpringDocConfiguration.class, InfoContributorConfiguration.class, LuixMetricsEndpointConfiguration.class})
public @interface EnableLuixSpringBoot2Framework {

}
