package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import com.ugeez.timesheet.validator.PPTypeValidatableAbstract;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class HourCost extends PPTypeValidatableAbstract {
    @NotNull
    @Temporal(TemporalType.DATE)
    @Getter
    private Date startDate;

    @NotNull
    @Positive
    @Getter
    private Double amount;

    @Override
    public String toString() {
        return "HourCost(" + startDate + ", " + amount + ")";
    }
}
