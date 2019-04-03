package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
// 这里迫不得已用的Entity, 因为无法在ElementCollection中再使用ElementCollection, 按照DDD来说理论上这里要用ValueObject
public class Worker extends PPEntityTypeValidatableAbstract {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    @Setter
    private Project project;

    @NotEmpty
    @ElementCollection
    @Valid
    @OrderBy("startDate DESC")
    @Getter
    private List<HourCost> hourCosts;

    @NotEmpty
    @ElementCollection
    @Valid
    @Getter
    private List<HourCommission> hourCommissions;

//    public List<HourCost> getHourCosts() {
//        return hourCosts.stream().collect(Collectors.toList());
//    }
//
//    public List<HourCommission> getHourCommissions() {
//        return hourCommissions.stream().collect(Collectors.toList());
//    }

    public Double calCostPerMin(Date date) {
        // 找出小于等于date的最后一条hourCost
        HourCost hourCost = hourCosts.stream()
                .filter(item -> item.getStartDate().compareTo(date) <= 0)
                .findFirst()
                .get();

        return hourCost.getAmount() / 60;
    }

    public void addHourCost(Date start, Double amount) {
        // 同一天只能有一个HourCost
        hourCosts.removeIf(item -> item.getStartDate().compareTo(start) == 0);
        hourCosts.add(new HourCost(start, amount));
    }

    public void removeHourCost(Date start) {
        Boolean removed = hourCosts.removeIf(item -> item.getStartDate().compareTo(start) == 0);
        
        if (!removed) {
            throw new RuntimeException("没有找到需要删除的HourCost");
        }
    }

    public void addHourCommission(Date start, Double amount) {
        // 同一天只能有一个HourCommission
        hourCommissions.removeIf(item -> item.getStartDate().compareTo(start) == 0);
        hourCommissions.add(new HourCommission(start, amount));
    }

    public void removeHourCommission(Date start) {
        Boolean removed = hourCommissions.removeIf(item -> item.getStartDate().compareTo(start) == 0);

        if (!removed) {
            throw new RuntimeException("没有找到需要删除的HourCommission");
        }
    }

    @Override
    public String toString() {
        return "Worker(" + user.getUsername() + ", " + project.getName() + ")";
    }

    public boolean sameUser(Worker worker) {
        return this.user.getId().equals(worker.user.getId());
    }

    public boolean sameUser(Long userId) {
        return this.user.getId().equals(userId);
    }
}