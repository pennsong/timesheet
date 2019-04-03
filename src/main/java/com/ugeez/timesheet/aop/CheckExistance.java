package com.ugeez.timesheet.aop;


import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ElementType.METHOD})
@Retention(RUNTIME)
public @interface CheckExistance {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> value() default void.class;
}
