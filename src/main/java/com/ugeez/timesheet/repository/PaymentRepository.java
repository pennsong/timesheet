package com.ugeez.timesheet.repository;

import com.ugeez.timesheet.model.Payment;
import com.ugeez.timesheet.model.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    List<Payment> findByCompanyId(Long id);
}
