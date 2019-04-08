package com.ugeez.timesheet.repository;

import com.ugeez.timesheet.model.User;
import com.ugeez.timesheet.model.WorkRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface WorkRecordRepository extends CrudRepository<WorkRecord, Long> {
    List<WorkRecord> findByProjectCompanyId(Long id);

    List<WorkRecord> findByProjectCompanyIdAndDateIsLessThanEqual(Long id, Date end);

    List<WorkRecord> findByProjectCompanyIdAndDateIsGreaterThanEqualAndDateIsLessThanEqual(Long id, Date start, Date end);

    Optional<WorkRecord> findOneByUserIdAndEndIsNull(Long id);
}
