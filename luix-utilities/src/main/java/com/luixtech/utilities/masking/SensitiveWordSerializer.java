package com.luixtech.utilities.masking;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import com.luixtech.utilities.masking.annotation.SensitiveField;
import com.luixtech.utilities.masking.strategy.Maskable;
import com.luixtech.utilities.serviceloader.ServiceLoader;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class SensitiveWordSerializer extends ValueSerializer<String> {
    private String sensitiveType;

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializationContext context) {
        Maskable maskingStrategy = ServiceLoader.forClass(Maskable.class).load(this.sensitiveType);
        jsonGenerator.writeString(maskingStrategy.mask(value));
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext context, BeanProperty beanProperty)
            throws DatabindException {
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
            return context.findValueSerializer(beanProperty.getType());
        }
        return context.findNullValueSerializer(null);
    }
}