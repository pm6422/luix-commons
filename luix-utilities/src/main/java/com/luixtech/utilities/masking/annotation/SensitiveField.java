package com.luixtech.utilities.masking.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.luixtech.utilities.masking.SensitiveWordSerializer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveWordSerializer.class)
public @interface SensitiveField {
    /**
     * Sensitive word type {@link SensitiveType}
     *
     * @return the sensitive word type
     */
    String value();
}
