package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class User extends PPEntityTypeValidatableAbstract {
    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @NotEmpty
    @Getter
    private String username;

    @NotEmpty
    @Getter
    @Setter
    private String password;

    @NotNull
    @PositiveOrZero
    @Getter
    @Setter
    private Double hourCostAmount;

    @NotNull
    @PositiveOrZero
    @Getter
    @Setter
    private Double hourCommissionAmount;

    @Setter
    private LocalDate lastSettlementDate;
    public LocalDate getLastSettlementDate(){
        if (lastSettlementDate == null) {
            return LocalDate.of(1900, 1, 1);
        } else {
            return lastSettlementDate;
        }
    }

    @Override
    public String toString() {
        return "User( + " + id + ", " + username + ")";
    }
}
