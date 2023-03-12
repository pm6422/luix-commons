package com.luixtech.utilities.annotation;

import java.lang.annotation.*;

/**
 * A marker used to identify there is an event
 */
@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.CLASS)
public @interface EventMarker {
}
