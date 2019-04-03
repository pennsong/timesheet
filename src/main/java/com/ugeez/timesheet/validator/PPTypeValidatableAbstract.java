package com.ugeez.timesheet.validator;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.MappedSuperclass;
import javax.validation.GroupSequence;

@Slf4j
@GroupSequence({PPTypeValidatableAbstract.class, PPTypeGroup.class})
@PPTypeConstraint(groups = PPTypeGroup.class)
@MappedSuperclass
public abstract class PPTypeValidatableAbstract implements PPTypeValidatable {
    @Override
    public boolean validate() {
        log.info(this + "default type validate");
        return true;
    }
}
