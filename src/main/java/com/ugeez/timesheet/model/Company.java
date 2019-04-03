package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
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

    @Temporal(TemporalType.DATE)
    @Setter
    @Getter
    private Date workRecordFixedDate;

    @Setter
    private String contactPerson;

    @Setter
    private String phone;

    @Override
    public String toString() {
        return "Company(" + id + ", " + name + ")";
    }
}
