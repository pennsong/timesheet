package com.ugeez.timesheet.validator;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Set;

@MappedSuperclass
public abstract class PPEntityTypeValidatableAbstract extends PPTypeValidatableAbstract {
    @PrePersist
    @PreUpdate
    // 配合 spring.jpa.properties.javax.persistence.validation.mode=none 使用
    public void v() {
        Set<ConstraintViolation<Object>> constraintViolations = Validation.buildDefaultValidatorFactory().getValidator().validate(this);

        Errors errors = new BeanPropertyBindingResult(this, "root");

        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {

            String propertyPath = constraintViolation.getPropertyPath().toString();

            String message = constraintViolation.getMessage();

            errors.rejectValue(propertyPath, "", message);
        }

        // 如果单项校验没通过, 直接抛异常
        if (errors.hasErrors()) {
            throw new RuntimeException(errors.toString());
        }
    }
}
