package com.ugeez.timesheet;

import com.ugeez.timesheet.model.Company;
import com.ugeez.timesheet.model.Project;
import com.ugeez.timesheet.model.User;
import com.ugeez.timesheet.repository.CompanyRepository;
import com.ugeez.timesheet.repository.ProjectRepository;
import com.ugeez.timesheet.repository.UserRepository;
import com.ugeez.timesheet.service.FactoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;

@Slf4j
@Component
@Transactional
public class CommandLineRunnerBean implements CommandLineRunner {
    @Autowired
    private FactoryService factoryService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 新建用户
        for (int i = 0; i < 10; i++) {
            factoryService.createUser("用户" + i, 500.0, 100.0);
        }

        Iterable<User> users = userRepository.findAll();

        // 新建公司
        for (int i = 0; i < 2; i++) {
            FactoryService.NewCompanyDto newCompanyDto = new FactoryService.NewCompanyDto("公司" + i, "c1", "p1");
            factoryService.createCompany(newCompanyDto);
        }

        Iterable<Company> companies = companyRepository.findAll();

        // 新建项目
        for (Company item: companies) {
            for (int i = 0; i < 2; i++) {
                FactoryService.NewProjectDto  newProjectDto = new FactoryService.NewProjectDto(item.getName() + "_项目" + i, item.getId());
                factoryService.createProject(newProjectDto);
            }
        }

        Iterable<Project> projects = projectRepository.findAll();

        // 添加worker
        for (Project project : projects) {
            for (User user : users) {
                FactoryService.NewWorkerDto newWorkerDto = new FactoryService.NewWorkerDto(user.getId(), project.getId(), null, null);
                factoryService.addWorkerToProject(newWorkerDto);
            }
        }

        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        long start = (new Date()).getTime();
        // 添加workRecord

        for (Project project : projects) {
            for (User user : users) {
                for (int k = 0; k < 2; k++) {
                    LocalDateTime s = dateTime.plus(8 * k, HOURS);
                    LocalDateTime e = dateTime.plus(8 * k + 1, HOURS);

                    FactoryService.StartWorkDto startWorkDto = new FactoryService.StartWorkDto(user.getId(), project.getId(), s);
                    factoryService.startWork(startWorkDto);

                    FactoryService.StopWorkDto stopWorkDto = new FactoryService.StopWorkDto(user.getId(), project.getId(), e, "项目事项");
                    factoryService.stopWork(stopWorkDto);
                }
            }
        }

        long end = (new Date()).getTime();
        log.info("" + (end - start));
    }
}