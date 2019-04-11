package com.ugeez.timesheet.repository;

import com.ugeez.timesheet.model.Company;
import com.ugeez.timesheet.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface CompanyRepository extends CrudRepository<Company, Long> {
    Company findOneByName(String name);
}
