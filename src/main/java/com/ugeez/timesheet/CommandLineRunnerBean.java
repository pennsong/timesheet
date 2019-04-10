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
import java.time.LocalDate;
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
        User penn = factoryService.createUser("penn", 500.0, 0.0);
        User jimi = factoryService.createUser("jimi", 500.0, 0.0);
        User jay = factoryService.createUser("jay", 500.0, 0.0);
        User jan = factoryService.createUser("jan", 500.0, 0.0);
        User tommy = factoryService.createUser("tommy", 500.0, 0.0);
        User fan = factoryService.createUser("fan", 500.0, 100.0);
        User jin = factoryService.createUser("jin", 500.0, 100.0);
        User bear = factoryService.createUser("bear", 500.0, 100.0);
        User gary = factoryService.createUser("gary", 500.0, 200.0);
        User villa = factoryService.createUser("villa", 500.0, 250.0);
        User rainbow = factoryService.createUser("rainbow", 500.0, 200.0);
        User moda = factoryService.createUser("moda", 500.0, 200.0);
        User ken = factoryService.createUser("ken", 500.0, 200.0);

        // 新建公司
        FactoryService.NewCompanyDto newCompanyDto = new FactoryService.NewCompanyDto("Harvey公司", "Harvey", null);
        Company harveyCompany = factoryService.createCompany(newCompanyDto);

        newCompanyDto = new FactoryService.NewCompanyDto("节能公司", "吕进", null);
        Company bearCompany = factoryService.createCompany(newCompanyDto);

        newCompanyDto = new FactoryService.NewCompanyDto("量子QTC", "心程", null);
        Company xinCompany = factoryService.createCompany(newCompanyDto);

        newCompanyDto = new FactoryService.NewCompanyDto("米茶", "心程", null);
        Company miCompany = factoryService.createCompany(newCompanyDto);

        newCompanyDto = new FactoryService.NewCompanyDto("磐哲", "冰的", null);
        Company panCompany = factoryService.createCompany(newCompanyDto);

        newCompanyDto = new FactoryService.NewCompanyDto("辰帆", "宁总", null);
        Company chenCompany = factoryService.createCompany(newCompanyDto);

        newCompanyDto = new FactoryService.NewCompanyDto("职行力", "张晶总", null);
        Company zhiCompany = factoryService.createCompany(newCompanyDto);

        // 新建项目
        FactoryService.NewProjectDto  newProjectDto = new FactoryService.NewProjectDto("小红书爬虫", harveyCompany.getId());
        Project red = factoryService.createProject(newProjectDto);

        newProjectDto = new FactoryService.NewProjectDto("节能项目", bearCompany.getId());
        Project power = factoryService.createProject(newProjectDto);

        newProjectDto = new FactoryService.NewProjectDto("赠药项目", xinCompany.getId());
        Project gift = factoryService.createProject(newProjectDto);

        newProjectDto = new FactoryService.NewProjectDto("减肥小程序", miCompany.getId());
        Project weight = factoryService.createProject(newProjectDto);

        newProjectDto = new FactoryService.NewProjectDto("人事系统前端", panCompany.getId());
        Project front = factoryService.createProject(newProjectDto);

        newProjectDto = new FactoryService.NewProjectDto("云集系统", chenCompany.getId());
        Project cloud = factoryService.createProject(newProjectDto);

        newProjectDto = new FactoryService.NewProjectDto("培训平台", zhiCompany.getId());
        Project bank = factoryService.createProject(newProjectDto);

        // 添加worker
        // red
        FactoryService.NewWorkerDto newWorkerDto = new FactoryService.NewWorkerDto(penn.getId(), red.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), red.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        // power
        newWorkerDto = new FactoryService.NewWorkerDto(penn.getId(), power.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), power.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        // gift
        newWorkerDto = new FactoryService.NewWorkerDto(penn.getId(), gift.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), gift.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        newWorkerDto = new FactoryService.NewWorkerDto(jan.getId(), gift.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        newWorkerDto = new FactoryService.NewWorkerDto(fan.getId(), gift.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        // weight
        newWorkerDto = new FactoryService.NewWorkerDto(penn.getId(), weight.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), weight.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        // front
        newWorkerDto = new FactoryService.NewWorkerDto(jimi.getId(), front.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), front.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        newWorkerDto = new FactoryService.NewWorkerDto(fan.getId(), front.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        // cloud
        newWorkerDto = new FactoryService.NewWorkerDto(jimi.getId(), cloud.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), cloud.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        // bank
        newWorkerDto = new FactoryService.NewWorkerDto(jimi.getId(), bank.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), bank.getId(), null, null);
        factoryService.addWorkerToProject(newWorkerDto);

        // 添加payment
        // harveyCompany
        FactoryService.NewPaymentDto newPaymentDto = new FactoryService.NewPaymentDto(8000.0, LocalDate.of(2019, 4, 7), harveyCompany.getId(), null);
        factoryService.createPayment(newPaymentDto);

        // bearCompany
        newPaymentDto = new FactoryService.NewPaymentDto(387375.0, LocalDate.of(2019, 2, 27), bearCompany.getId(), "系统启用时初始余额");
        factoryService.createPayment(newPaymentDto);

        // xinCompany
        newPaymentDto = new FactoryService.NewPaymentDto(20250.0, LocalDate.of(2019, 1, 1), xinCompany.getId(), "系统启用时初始余额");
        factoryService.createPayment(newPaymentDto);

        newPaymentDto = new FactoryService.NewPaymentDto(19750.0, LocalDate.of(2019, 3, 17), xinCompany.getId(), null);
        factoryService.createPayment(newPaymentDto);

        // miCompany
        newPaymentDto = new FactoryService.NewPaymentDto(2250.0, LocalDate.of(2019, 3, 3), miCompany.getId(), "系统启用时初始余额");
        factoryService.createPayment(newPaymentDto);

        // panCompany
        newPaymentDto = new FactoryService.NewPaymentDto(95000.0, LocalDate.of(2019, 2, 28), panCompany.getId(), null);
        factoryService.createPayment(newPaymentDto);
        // end 添加payment
    }
}