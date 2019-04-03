package com.ugeez.timesheet.repository;

import com.ugeez.timesheet.model.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    List<Project> findByCompanyId(Long id);
}
