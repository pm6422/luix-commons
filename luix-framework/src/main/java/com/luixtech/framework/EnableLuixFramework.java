package com.luixtech.framework;

import com.luixtech.framework.aspect.AopLoggingAspect;
import com.luixtech.framework.aspect.ElapsedTimeLoggingAspect;
import com.luixtech.framework.aspect.ExceptionTranslatorAdviceAspect;
import com.luixtech.framework.component.HttpHeaderCreator;
import com.luixtech.framework.component.MessageCreator;
import com.luixtech.framework.component.PrintAppInfoApplicationRunner;
import com.luixtech.framework.config.AsyncConfiguration;
import com.luixtech.framework.config.LocaleConfiguration;
import com.luixtech.framework.config.LuixProperties;
import com.luixtech.framework.config.api.OpenApiEndpointConfiguration;
import com.luixtech.framework.config.api.SpringDocConfiguration;
import com.luixtech.framework.config.info.InfoContributorConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AopLoggingAspect.class, ElapsedTimeLoggingAspect.class, ExceptionTranslatorAdviceAspect.class,
        HttpHeaderCreator.class, MessageCreator.class, PrintAppInfoApplicationRunner.class,
        AsyncConfiguration.class, LocaleConfiguration.class, LuixProperties.class, OpenApiEndpointConfiguration.class,
        SpringDocConfiguration.class, InfoContributorConfiguration.class})
public @interface EnableLuixFramework {

}
