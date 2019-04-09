package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Company extends PPEntityTypeValidatableAbstract {
    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @NotEmpty
    @Column(unique = true)
    @Getter
    @Setter
    private String name;

    @Setter
    private LocalDate workRecordFixedDate;
    public LocalDate getWorkRecordFixedDate() {
        if (workRecordFixedDate == null) {
            return LocalDate.of(1900, 1, 1);
        } else {
            return workRecordFixedDate;
        }
    }

    @Getter
    @Setter
    private String contactPerson;

    @Getter
    @Setter
    private String phone;

    @Override
    public String toString() {
        return "Company(" + id + ", " + name + ")";
    }
}
