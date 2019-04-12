package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class WorkRecord extends PPEntityTypeValidatableAbstract {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Getter
    private LocalDate date;

    @NotNull
    @Getter
    private LocalDateTime start;

    @Getter
    @Setter
    private LocalDateTime end;

    @ManyToOne(optional = false)
    @Getter
    private Project project;

    @Getter
    @Setter
    private String note;

    @ManyToOne(optional = false)
    @Getter
    private User user;

    public Double calCost() {
        Optional<Worker> worker = project.gainWorkerByUserId(user.getId());

        if (!(worker.isPresent())) {
            throw new RuntimeException(this + "这条记录的用户还没有计费规则!");
        }

        HourCost hourCost = worker.get().gainHourCosts().stream().filter(item -> (item.getStartDate().isBefore(date) || item.getStartDate().isEqual(date))).findFirst().get();
        long seconds = Duration.between(start, end).getSeconds();
        double secondCost = hourCost.getAmount() / 3600;

        return seconds * secondCost;
    }

    public List<WorkRecord> splitWorkRecordToDay(LocalDateTime endDateTime) {
        List<WorkRecord> workRecords = new ArrayList<WorkRecord>();

        // 判断start到endDate中隔了几天
        long days = DAYS.between(start.toLocalDate(), endDateTime.toLocalDate());
        if (days == 0) {
            end = endDateTime;

        } else {
            // start那天23:59:59
            end = start.toLocalDate().atStartOfDay().plus(24 * 3600 - 1, SECONDS);
            for (int i = 0; i < days - 1; i++) {
                LocalDateTime curStart = start.toLocalDate().plusDays(1).atTime(0, 0);
                LocalDateTime curEnd = curStart.plusSeconds(24 * 3600 - 1);
                WorkRecord curWorkRecord = new WorkRecord(null, curStart.toLocalDate(), curStart, curEnd, project, note, user);
                workRecords.add(curWorkRecord);
            }
            WorkRecord last =  new WorkRecord(null, endDateTime.toLocalDate(), endDateTime.toLocalDate().atTime(0, 0), endDateTime, project, note, user);
            workRecords.add(last);
        }
        return workRecords;
    }

    @Override
    public String toString() {
        return "WorkRecord(" + id + ", " + date + ", " + start + ", " + end + ", " + project.getName() + ", " + user.getUsername() + ", " + note + ")";
    }

    @Override
    public boolean validate() {
        if (end != null && !(start.isBefore(end))) {
            throw new RuntimeException("开始时间要小于结束时间!");
        }

        return true;
    }
}
