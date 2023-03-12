package com.luixtech.framework.component;

import com.luixtech.framework.config.LuixProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MessageCreator {

    private final LuixProperties luixProperties;
    private final MessageSource  messageSource;

    public String getMessage(String code, Object... arguments) {
        return messageSource.getMessage(code, arguments, luixProperties.getLang().getDefaultLocale());
    }
}
