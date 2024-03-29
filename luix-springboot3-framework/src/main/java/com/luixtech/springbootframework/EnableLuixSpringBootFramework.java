package com.luixtech.springbootframework;

import com.luixtech.springbootframework.aspect.AopLoggingAspect;
import com.luixtech.springbootframework.aspect.ElapsedTimeLoggingAspect;
import com.luixtech.springbootframework.aspect.ExceptionTranslatorAdviceAspect;
import com.luixtech.springbootframework.component.HttpHeaderCreator;
import com.luixtech.springbootframework.component.MessageCreator;
import com.luixtech.springbootframework.component.PrintAppInfoApplicationRunner;
import com.luixtech.springbootframework.config.AsyncConfiguration;
import com.luixtech.springbootframework.config.LocaleConfiguration;
import com.luixtech.springbootframework.config.LuixProperties;
import com.luixtech.springbootframework.config.apidoc.OpenApiEndpointConfiguration;
import com.luixtech.springbootframework.config.apidoc.SpringDocAutoConfiguration;
import com.luixtech.springbootframework.config.info.AppInfoContributorConfiguration;
import com.luixtech.springbootframework.config.metrics.LuixMetricsEndpointConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AopLoggingAspect.class, ElapsedTimeLoggingAspect.class, ExceptionTranslatorAdviceAspect.class,
        HttpHeaderCreator.class, MessageCreator.class, PrintAppInfoApplicationRunner.class,
        AsyncConfiguration.class, LocaleConfiguration.class, LuixProperties.class, OpenApiEndpointConfiguration.class,
        SpringDocAutoConfiguration.class, AppInfoContributorConfiguration.class, LuixMetricsEndpointConfiguration.class})
public @interface EnableLuixSpringBootFramework {

}
