package com.ugeez.timesheet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TimesheetApplication {
    public static void main(String[] args) {
        SpringApplication.run(TimesheetApplication.class, args);
    }
}
