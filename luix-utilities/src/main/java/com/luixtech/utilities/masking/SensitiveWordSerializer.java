package com.luixtech.utilities.masking;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.luixtech.utilities.masking.annotation.SensitiveField;
import com.luixtech.utilities.masking.strategy.Maskable;
import com.luixtech.utilities.serviceloader.ServiceLoader;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class SensitiveWordSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private String sensitiveType;

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Maskable maskingStrategy = ServiceLoader.forClass(Maskable.class).load(this.sensitiveType);
        jsonGenerator.writeString(maskingStrategy.mask(value));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        // Skip null property
        if (beanProperty != null) {
            // Skip non-string type
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                SensitiveField field = beanProperty.getAnnotation(SensitiveField.class);
                if (field == null) {
                    field = beanProperty.getContextAnnotation(SensitiveField.class);
                }
                if (field != null && Boolean.TRUE.equals(DataMaskingThreadContextHolder.getMaskEnabled())) {
                    // Execute data masking
                    return new SensitiveWordSerializer(field.value());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }
}