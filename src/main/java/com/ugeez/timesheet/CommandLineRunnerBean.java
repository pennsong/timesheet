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

@Slf4j
@Component
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
        for (int i = 0; i < 2; i++) {
            factoryService.createUser("用户" + i, 500.0, 100.0);
        }

        // 新建公司
        for (int i = 0; i < 2; i++) {
            FactoryService.NewCompanyDto newCompanyDto = new FactoryService.NewCompanyDto("公司" + i, "c1", "p1");
            factoryService.createCompany(newCompanyDto);
        }

        // 新建项目
        for (Company item: companyRepository.findAll()) {
            for (int i = 0; i < 2; i++) {
                FactoryService.NewProjectDto  newProjectDto = new FactoryService.NewProjectDto(item.getName() + "_项目" + i, item.getId());
                factoryService.createProject(newProjectDto);
            }
        }
    }
}