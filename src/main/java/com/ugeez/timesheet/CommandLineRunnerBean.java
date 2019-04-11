package com.ugeez.timesheet;

import com.ugeez.timesheet.model.Company;
import com.ugeez.timesheet.model.Project;
import com.ugeez.timesheet.model.User;
import com.ugeez.timesheet.model.Worker;
import com.ugeez.timesheet.repository.CompanyRepository;
import com.ugeez.timesheet.repository.ProjectRepository;
import com.ugeez.timesheet.repository.UserRepository;
import com.ugeez.timesheet.service.FactoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.FileReader;
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
    private JdbcTemplate jdbcTemplate;

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
        /*user
        u1 2 0
        u2 2 1
        u3 2 1
        */
        User u1 = factoryService.createUser("u1", 2.0, 0.0);
        User u2 = factoryService.createUser("u2", 2.0, 1.0);
        User u3 = factoryService.createUser("u3", 2.0, 1.0);


       /* company
       c1
       c2
       c3
       */
        FactoryService.NewCompanyDto newCompanyDto = new FactoryService.NewCompanyDto("c1", null, null);
        Company c1 = factoryService.createCompany(newCompanyDto);

        newCompanyDto = new FactoryService.NewCompanyDto("c2", null, null);
        Company c2 = factoryService.createCompany(newCompanyDto);

        newCompanyDto = new FactoryService.NewCompanyDto("c3", null, null);
        Company c3 = factoryService.createCompany(newCompanyDto);


        /*
        project
        c1p1 c1 worker(u1, u2)
        [
            {
                u1,
                hourCost: [
                    {
                        1999/12/31,
                        2
                    },
                    {
                        2000/1/5,
                        4
                    },
                    {
                        2000/1/20,
                        6
                    },
                ],
                hourCommission: [
                    {
                        1999/12/31,
                        1
                    },
                    {
                        2000/1/5,
                        2
                    },
                    {
                        2000/1/20,
                        3
                    },
                ],
            },
            {
                u2,
                [
                    {
                        1999/12/31,
                        2
                    }
                ]
            }
        ]
        c1p2 c1
        c2p1 c2
        */
        FactoryService.NewProjectDto newProjectDto = new FactoryService.NewProjectDto("c1p1", c1.getId());
        Project c1p1 = factoryService.createProject(newProjectDto);

        newProjectDto = new FactoryService.NewProjectDto("c1p2", c1.getId());
        Project c1p2 = factoryService.createProject(newProjectDto);

        newProjectDto = new FactoryService.NewProjectDto("c2p1", c2.getId());
        Project c2p1 = factoryService.createProject(newProjectDto);

        factoryService.addWorkerToProject(
                new FactoryService.NewWorkerDto(u1.getId(), c1p1.getId(), null ,null)
        );

        factoryService.addWorkerToProject(
                new FactoryService.NewWorkerDto(u2.getId(), c1p1.getId(), null ,null)
        );

        factoryService.addHourCost(
                u1.getId(),
                c1p1.getId(),
                new FactoryService.NewHourCostDto(2.0,
                        LocalDate.of(1999, 12, 31)
                )
        );

        factoryService.addHourCost(
                u1.getId(),
                c1p1.getId(),
                new FactoryService.NewHourCostDto(4.0,
                        LocalDate.of(2000, 1, 5)
                )
        );

        factoryService.addHourCost(
                u1.getId(),
                c1p1.getId(),
                new FactoryService.NewHourCostDto(6.0,
                        LocalDate.of(2000, 1, 20)
                )
        );

        factoryService.addHourCommission(
                u1.getId(),
                c1p1.getId(),
                new FactoryService.NewHourCommissionDto(1.0,
                        LocalDate.of(1999, 12, 31)
                )
        );

        factoryService.addHourCommission(
                u1.getId(),
                c1p1.getId(),
                new FactoryService.NewHourCommissionDto(2.0,
                        LocalDate.of(2000, 1, 5)
                )
        );

        factoryService.addHourCommission(
                u1.getId(),
                c1p1.getId(),
                new FactoryService.NewHourCommissionDto(3.0,
                        LocalDate.of(2000, 1, 20)
                )
        );

        factoryService.addHourCost(
                u2.getId(),
                c1p1.getId(),
                new FactoryService.NewHourCostDto(2.0,
                        LocalDate.of(1999, 12, 31)
                )
        );

        /*
        payment
        1999/12/1 c1 100.0 testNote
        2000/1/5 c1 100.0 testNote
        2000/1/15 c1 100.0 testNote
        */
        FactoryService.NewPaymentDto newPaymentDto = new FactoryService.NewPaymentDto(100.0, LocalDate.of(1999, 12, 1), c1.getId(), "testNote");
        factoryService.createPayment(newPaymentDto);

        newPaymentDto = new FactoryService.NewPaymentDto(100.0, LocalDate.of(2000, 1, 5), c1.getId(), "testNote");
        factoryService.createPayment(newPaymentDto);

        newPaymentDto = new FactoryService.NewPaymentDto(100.0, LocalDate.of(2000, 1, 15), c1.getId(), "testNote");
        factoryService.createPayment(newPaymentDto);

        /*
        workRecord
        c1p1 u1 2000/1/1 10:01 11:01 testWorkNote
        c1p1 u1 2000/1/5 10:01 11:01 testWorkNote
        c1p1 u1 2000/1/6 10:01 11:01 testWorkNote
        */
        factoryService.createWorkRecord(
                new FactoryService.NewWorkRecordDto(
                        u1.getId(),
                        c1p1.getId(),
                        LocalDateTime.of(2000, 1, 1, 10, 1),
                        LocalDateTime.of(2000, 1, 1, 11, 1),
                        "testWorkNote"
                )
        );

        factoryService.createWorkRecord(
                new FactoryService.NewWorkRecordDto(
                        u1.getId(),
                        c1p1.getId(),
                        LocalDateTime.of(2000, 1, 5, 10, 1),
                        LocalDateTime.of(2000, 1, 5, 11, 1),
                        "testWorkNote"
                )
        );

        factoryService.createWorkRecord(
                new FactoryService.NewWorkRecordDto(
                        u1.getId(),
                        c1p1.getId(),
                        LocalDateTime.of(2000, 1, 6, 10, 1),
                        LocalDateTime.of(2000, 1, 6, 11, 1),
                        "testWorkNote"
                )
        );

//===========================================================================

//        // 新建用户
//        User penn = factoryService.createUser("penn", 500.0, 0.0);
//        User jimi = factoryService.createUser("jimi", 500.0, 0.0);
//        User jay = factoryService.createUser("jay", 500.0, 0.0);
//        User jan = factoryService.createUser("jan", 500.0, 0.0);
//        User tommy = factoryService.createUser("tommy", 500.0, 0.0);
//        User fan = factoryService.createUser("fan", 500.0, 100.0);
//        User jin = factoryService.createUser("jin", 500.0, 100.0);
//        User bear = factoryService.createUser("bear", 500.0, 100.0);
//        User gary = factoryService.createUser("gary", 500.0, 200.0);
//        User villa = factoryService.createUser("villa", 500.0, 250.0);
//        User rainbow = factoryService.createUser("rainbow", 500.0, 200.0);
//        User moda = factoryService.createUser("moda", 500.0, 200.0);
//        User ken = factoryService.createUser("ken", 500.0, 200.0);
//
//        // 新建公司
//        FactoryService.NewCompanyDto newCompanyDto = new FactoryService.NewCompanyDto("Harvey公司", "Harvey", null);
//        Company harveyCompany = factoryService.createCompany(newCompanyDto);
//
//        newCompanyDto = new FactoryService.NewCompanyDto("节能公司", "吕进", null);
//        Company bearCompany = factoryService.createCompany(newCompanyDto);
//
//        newCompanyDto = new FactoryService.NewCompanyDto("量子QTC", "心程", null);
//        Company xinCompany = factoryService.createCompany(newCompanyDto);
//
//        newCompanyDto = new FactoryService.NewCompanyDto("米茶", "心程", null);
//        Company miCompany = factoryService.createCompany(newCompanyDto);
//
//        newCompanyDto = new FactoryService.NewCompanyDto("磐哲", "冰的", null);
//        Company panCompany = factoryService.createCompany(newCompanyDto);
//
//        newCompanyDto = new FactoryService.NewCompanyDto("辰帆", "宁总", null);
//        Company chenCompany = factoryService.createCompany(newCompanyDto);
//
//        newCompanyDto = new FactoryService.NewCompanyDto("职行力", "张晶总", null);
//        Company zhiCompany = factoryService.createCompany(newCompanyDto);
//
//        // 新建项目
//        FactoryService.NewProjectDto  newProjectDto = new FactoryService.NewProjectDto("小红书爬虫", harveyCompany.getId());
//        Project red = factoryService.createProject(newProjectDto);
//
//        newProjectDto = new FactoryService.NewProjectDto("节能项目", bearCompany.getId());
//        Project power = factoryService.createProject(newProjectDto);
//
//        newProjectDto = new FactoryService.NewProjectDto("赠药项目", xinCompany.getId());
//        Project gift = factoryService.createProject(newProjectDto);
//
//        newProjectDto = new FactoryService.NewProjectDto("减肥小程序", miCompany.getId());
//        Project weight = factoryService.createProject(newProjectDto);
//
//        newProjectDto = new FactoryService.NewProjectDto("人事系统前端", panCompany.getId());
//        Project front = factoryService.createProject(newProjectDto);
//
//        newProjectDto = new FactoryService.NewProjectDto("云集系统", chenCompany.getId());
//        Project cloud = factoryService.createProject(newProjectDto);
//
//        newProjectDto = new FactoryService.NewProjectDto("培训平台", zhiCompany.getId());
//        Project bank = factoryService.createProject(newProjectDto);
//
//        // 添加worker
//        // red
//        FactoryService.NewWorkerDto newWorkerDto = new FactoryService.NewWorkerDto(penn.getId(), red.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), red.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        // power
//        newWorkerDto = new FactoryService.NewWorkerDto(penn.getId(), power.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), power.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        // gift
//        newWorkerDto = new FactoryService.NewWorkerDto(penn.getId(), gift.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), gift.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        newWorkerDto = new FactoryService.NewWorkerDto(jan.getId(), gift.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        newWorkerDto = new FactoryService.NewWorkerDto(fan.getId(), gift.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        // weight
//        newWorkerDto = new FactoryService.NewWorkerDto(penn.getId(), weight.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), weight.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        // front
//        newWorkerDto = new FactoryService.NewWorkerDto(jimi.getId(), front.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), front.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        newWorkerDto = new FactoryService.NewWorkerDto(fan.getId(), front.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        // cloud
//        newWorkerDto = new FactoryService.NewWorkerDto(jimi.getId(), cloud.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), cloud.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        // bank
//        newWorkerDto = new FactoryService.NewWorkerDto(jimi.getId(), bank.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        newWorkerDto = new FactoryService.NewWorkerDto(jay.getId(), bank.getId(), null, null);
//        factoryService.addWorkerToProject(newWorkerDto);
//
//        // 添加payment
//        // harveyCompany
//        FactoryService.NewPaymentDto newPaymentDto = new FactoryService.NewPaymentDto(8000.0, LocalDate.of(2019, 4, 7), harveyCompany.getId(), null);
//        factoryService.createPayment(newPaymentDto);
//
//        // bearCompany
//        newPaymentDto = new FactoryService.NewPaymentDto(387375.0, LocalDate.of(2019, 2, 27), bearCompany.getId(), "系统启用时初始余额");
//        factoryService.createPayment(newPaymentDto);
//
//        // xinCompany
//        newPaymentDto = new FactoryService.NewPaymentDto(20250.0, LocalDate.of(2019, 1, 1), xinCompany.getId(), "系统启用时初始余额");
//        factoryService.createPayment(newPaymentDto);
//
//        newPaymentDto = new FactoryService.NewPaymentDto(19750.0, LocalDate.of(2019, 3, 17), xinCompany.getId(), null);
//        factoryService.createPayment(newPaymentDto);
//
//        // miCompany
//        newPaymentDto = new FactoryService.NewPaymentDto(2250.0, LocalDate.of(2019, 3, 3), miCompany.getId(), "系统启用时初始余额");
//        factoryService.createPayment(newPaymentDto);
//
//        // panCompany
//        newPaymentDto = new FactoryService.NewPaymentDto(95000.0, LocalDate.of(2019, 2, 28), panCompany.getId(), null);
//        factoryService.createPayment(newPaymentDto);
//        // end 添加payment
    }
}