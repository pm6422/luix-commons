package com.luixtech.uidgenerator.springboot.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({UidAutoConfiguration.class})
public @interface EnableUidGenerator {
}
