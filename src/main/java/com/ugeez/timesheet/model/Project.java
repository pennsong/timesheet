package com.ugeez.timesheet.model;

import com.ugeez.timesheet.validator.PPEntityTypeValidatableAbstract;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@Slf4j
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Project extends PPEntityTypeValidatableAbstract {
    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @NotEmpty
    @Setter
    @Getter
    private String name;

    @ManyToOne(optional = false)
    private Company company;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Worker> workers;

    public void addWorker(Worker worker) {
        if (workers == null) {
            workers = new ArrayList<>();
        } else {
            // 检查如果有相同User已经在worker中, 则不允许添加
            Long count = workers.stream().filter(item -> item.sameUser(worker)).count();
            if (count > 0) {
                throw new RuntimeException("这个用户已存在于workers中!");
            }
        }

        workers.add(worker);
    }

    public void removeWorker(Long userId) {
        if (workers == null) {
            throw new RuntimeException("这个用户不存在于workers中!");
        } else {
            Boolean removed = workers.removeIf(item -> item.sameUser(userId));
            if (!removed) {
                throw new RuntimeException("这个用户不存在于workers中!");
            }
        }
    }

    public void addHourCost(Long userId, Date start, Double amount) {
        Optional<Worker> worker = workers.stream().filter(item -> item.sameUser(userId)).findFirst();
        if (!(worker.isPresent())) {
            throw new RuntimeException("这个用户不存在于workers中!");
        }

        worker.get().addHourCost(start, amount);
    }

    public void removeHourCost(Long userId, Date start) {
        Optional<Worker> worker = workers.stream().filter(item -> item.sameUser(userId)).findFirst();
        if (!(worker.isPresent())) {
            throw new RuntimeException("这个用户不存在于workers中!");
        }

        worker.get().removeHourCost(start);
    }

    public void addHourCommission(Long userId, Date start, Double amount) {
        Optional<Worker> worker = workers.stream().filter(item -> item.sameUser(userId)).findFirst();
        if (!(worker.isPresent())) {
            throw new RuntimeException("这个用户不存在于workers中!");
        }

        worker.get().addHourCommission(start, amount);
    }

    public void removeHourCommission(Long userId, Date start) {
        Optional<Worker> worker = workers.stream().filter(item -> item.sameUser(userId)).findFirst();
        if (!(worker.isPresent())) {
            throw new RuntimeException("这个用户不存在于workers中!");
        }

        worker.get().removeHourCommission(start);
    }

    public Optional<Worker> gainWorkerByUserId(Long id) {
        return workers.stream().filter(item -> item.sameUser(id)).findFirst();
    }

    public Date gainCompanyWorkRecordFixedDate() {
        return company.getWorkRecordFixedDate() == null ? new Date(0) : company.getWorkRecordFixedDate();
    }

    @Override
    public String toString() {
        return "Project(" + id + ", " + name + ", " + company.getName() + ")";
    }
}
