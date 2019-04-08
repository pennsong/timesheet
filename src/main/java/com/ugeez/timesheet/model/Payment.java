package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Payment extends PPEntityTypeValidatableAbstract {
    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date date;
    public Date gainDate() {
        return new Date(date.getTime());
    }

    @NotNull
    @Positive
    @Getter
    private Double amount;

    @ManyToOne(optional = false)
    @Getter
    private Company company;

    @Override
    public String toString() {
        return "Payment(" + company.getName() + ", " + amount + ", " + date + ")";
    }
}
