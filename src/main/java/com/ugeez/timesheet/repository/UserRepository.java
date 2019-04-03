package com.ugeez.timesheet.repository;

import com.ugeez.timesheet.model.Project;
import com.ugeez.timesheet.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
}
