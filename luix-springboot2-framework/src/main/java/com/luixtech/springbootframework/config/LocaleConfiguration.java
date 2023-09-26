package com.luixtech.springbootframework.config;

import com.luixtech.springbootframework.config.locale.FrontendCookieLocaleResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class LocaleConfiguration implements WebMvcConfigurer {

    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        FrontendCookieLocaleResolver cookieLocaleResolver = new FrontendCookieLocaleResolver();
        cookieLocaleResolver.setCookieName("TRANSLATE_LANG_KEY");
        return cookieLocaleResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        registry.addInterceptor(localeChangeInterceptor);
    }
}
