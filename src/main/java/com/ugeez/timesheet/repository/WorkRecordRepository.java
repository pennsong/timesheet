package com.ugeez.timesheet.repository;

import com.ugeez.timesheet.model.User;
import com.ugeez.timesheet.model.WorkRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface WorkRecordRepository extends CrudRepository<WorkRecord, Long> {
    List<WorkRecord> findByProjectCompanyId(Long id);

    List<WorkRecord> findByProjectCompanyIdAndDateIsLessThanEqual(Long id, LocalDate end);

    List<WorkRecord> findByProjectCompanyIdAndDateIsGreaterThanEqualAndDateIsLessThanEqual(Long id, LocalDate start, LocalDate end);

    @Query("select count(w) from " +
            "WorkRecord w " +
            "join w.project p " +
            "join p.company c " +
            "join w.user u " +
            "where c.id = :companyId " +
            "and u.id = :userId " +
            "and (" +
            "(w.start >= :start and w.start <= :end) " +
            "or (w.end >= :start and w.end <= :end) " +
            "or (w.start <= :start and w.end >= :end)" +
            ")")
    Long findByOverlapWorkRecords(@Param("companyId") Long companyId, @Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select w from " +
            "WorkRecord w " +
            "join w.project p " +
            "join p.company c " +
            "where c.id= :companyId " +
            "and w.end = null " +
            "and w.start <= :dateTime")
    List<WorkRecord>  findCompanyUnfinishedWorkRecordsByDate(@Param("companyId") Long companyId, @Param("dateTime") LocalDateTime dateTime);

    Optional<WorkRecord> findOneByUserIdAndEndIsNull(Long id);
}
