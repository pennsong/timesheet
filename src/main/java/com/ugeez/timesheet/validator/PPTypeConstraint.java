package com.ugeez.timesheet.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PPTypeValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PPTypeConstraint {
    String message() default "类型验证错误";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

