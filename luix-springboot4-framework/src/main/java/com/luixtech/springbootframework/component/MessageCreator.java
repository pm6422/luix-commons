package com.luixtech.springbootframework.component;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Use AcceptHeaderLocaleResolver as default
 * {@link org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver}
 */
@Component
@AllArgsConstructor
public class MessageCreator {
    private final MessageSource messageSource;

    public String getMessage(String code, Object... arguments) {
        return messageSource.getMessage(code, arguments, LocaleContextHolder.getLocale());
    }
}