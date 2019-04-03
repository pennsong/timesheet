package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    private String password;

    @NotNull
    @PositiveOrZero
    @Getter
    private Double hourCostAmount;

    @NotNull
    @PositiveOrZero
    @Getter
    private Double hourCommissionAmount;

    @Getter
    private Date lastSettlementDate;

    @Override
    public String toString() {
        return "User( + " + id + ", " + username + ")";
    }
}
