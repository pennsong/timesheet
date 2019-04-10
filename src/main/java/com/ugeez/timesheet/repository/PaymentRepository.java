package com.ugeez.timesheet.repository;

import com.ugeez.timesheet.model.Payment;
import com.ugeez.timesheet.model.Project;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    List<Payment> findByCompanyId(Long id);

    List<Payment> findByCompanyIdAndDateIsLessThanEqual(Long id, LocalDate end);

    List<Payment> findByCompanyIdAndDateIsGreaterThanEqualAndDateIsLessThanEqual(Long id, LocalDate start, LocalDate end);
}
