package com.ugeez.timesheet.validator;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class PPTypeValidator implements
        ConstraintValidator<PPTypeConstraint, PPTypeValidatable> {

    @Override
    public void initialize(PPTypeConstraint ppTypeConstraint) {
    }

    @Override
    public boolean isValid(PPTypeValidatable ppTypeValidatable, ConstraintValidatorContext cxt) {
//        log.info(ppTypeValidatable + " PPTypeValidator");
        return ppTypeValidatable.validate();
    }

}
