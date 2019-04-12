package com.ugeez.timesheet.repository;

import com.ugeez.timesheet.model.Project;
import com.ugeez.timesheet.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    User findOneByUsernameEqualsIgnoreCase(String username);
}
