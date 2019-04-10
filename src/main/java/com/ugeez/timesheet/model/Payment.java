package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
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
    @Getter
    private LocalDate date;

    @NotNull
    @Positive
    @Getter
    private Double amount;

    @ManyToOne(optional = false)
    @Getter
    private Company company;

    @Getter
    private String note;

    @Override
    public String toString() {
        return "Payment(" + company.getName() + ", " + amount + ", " + date + ", " + note + ")";
    }
}
