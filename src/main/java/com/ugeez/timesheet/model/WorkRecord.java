package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class WorkRecord extends PPEntityTypeValidatableAbstract {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date date;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Temporal(TemporalType.TIMESTAMP)
    @Setter
    private Date end;

    @ManyToOne(optional = false)
    private Project project;

    @Setter
    private String note;

    @ManyToOne(optional = false)
    private User user;

    public Double calCost() {
        Optional<Worker> worker = project.gainWorkerByUserId(user.getId());

        if (!(worker.isPresent())) {
            throw new RuntimeException(this + "这条记录的用户还没有计费规则!");
        }

        HourCost hourCost = worker.get().getHourCosts().stream().filter(item -> item.getStartDate().compareTo(date) <= 0).findFirst().get();
        long miniutes = (end.getTime() - start.getTime()) / (1000 * 60);
        double minutesCost = hourCost.getAmount() / 60;

        return minutesCost * miniutes;
    }

    @Override
    public String toString() {
        return "WorkRecord(" + id + ", " + date + ", " + start + ", " + end + ", " + project.getName() + ", " + user.getUsername() + ", " + note + ")";
    }
}
