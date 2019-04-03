package com.ugeez.timesheet.repository;

import com.ugeez.timesheet.model.User;
import com.ugeez.timesheet.model.WorkRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface WorkRecordRepository extends CrudRepository<WorkRecord, Long> {
    List<WorkRecord> findByProjectCompanyId(Long id);

    Optional<WorkRecord> findOneByUserIdAndEndIsNull(Long id);
}
