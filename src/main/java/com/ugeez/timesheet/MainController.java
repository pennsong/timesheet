package com.ugeez.timesheet;

import com.ugeez.timesheet.model.Project;
import com.ugeez.timesheet.model.User;
import com.ugeez.timesheet.model.WorkRecord;
import com.ugeez.timesheet.repository.ProjectRepository;
import com.ugeez.timesheet.repository.UserRepository;
import com.ugeez.timesheet.repository.WorkRecordRepository;
import com.ugeez.timesheet.service.FactoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Date;

@Slf4j
@RequestMapping("/")
@RestController
@Transactional
public class MainController {

    @Autowired
    private FactoryService factoryService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkRecordRepository workRecordRepository;

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String test() {
        // 添加worker
        for (Project project : projectRepository.findAll()) {
            for (User user : userRepository.findAll()) {
                FactoryService.NewWorkerDto newWorkerDto = new FactoryService.NewWorkerDto(user.getId(), project.getId(), null, null);
                factoryService.addWorkerToProject(newWorkerDto);
            }
        }

        return "ok";

        // end 添加workRecord

    }

    @RequestMapping(value = "/test2", method = RequestMethod.POST)
    public String test2() {
        Date date = new Date(0);
        // 添加workRecord
        for (Project project : projectRepository.findAll()) {
            for (User user : userRepository.findAll()) {
                for (int k = 0; k < 6; k++) {
                    Date s = new Date(date.getTime() + (3600 * 1000 * 8) * k);
                    Date e = new Date(date.getTime() + (3600 * 1000 * 8) * (k + 1));

                    FactoryService.StartWorkDto startWorkDto = new FactoryService.StartWorkDto(user.getId(), project.getId(), s);
                    factoryService.startWork(startWorkDto);

                    FactoryService.StopWorkDto stopWorkDto = new FactoryService.StopWorkDto(user.getId(), project.getId(), e, "项目事项");
                    factoryService.stopWork(stopWorkDto);
                }
            }
        }

        return "ok";

        // end 添加workRecord

    }

    // 公司
    // 创建公司
    @RequestMapping(value = "/company", method = RequestMethod.POST)
    public String createCompany(@Valid @RequestBody FactoryService.NewCompanyDto dto) {
        factoryService.createCompany(dto);

        return "ok";
    }

    // 删除公司
    @RequestMapping(value = "/company/{id}", method = RequestMethod.DELETE)
    public String deleteCompany(@PathVariable Long id) {
        factoryService.deleteCompany(id);

        return "ok";
    }

    // 编辑公司
    @RequestMapping(value = "/company/{id}", method = RequestMethod.PATCH)
    public String editCompany(@PathVariable Long id, @Valid @RequestBody FactoryService.EditCompanyDto dto) {
        factoryService.editCompany(id, dto);

        return "ok";
    }

    // 设置公司最后一次报表相关工作记录截止日
    @RequestMapping(value = "/company/{id}/setWorkRecordFixedDate/{date}", method = RequestMethod.POST)
    public String setCompanyWorkRecordFixedDate(@PathVariable Long id, @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date date) {
        factoryService.setCompanyWorkRecordFixedDate(id, date);

        return "ok";
    }

    // 查看公司预付款余额
    @RequestMapping(value = "/company/{id}/checkBalance", method = RequestMethod.GET)
    public String gainBalance(@PathVariable Long id) {
        return "ok" + factoryService.gainCompanyBalance(id);
    }

    // end 公司

    // 项目
    // 创建项目
    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public String createProject(@Valid @RequestBody FactoryService.NewProjectDto dto) {
        factoryService.createProject(dto);

        return "ok";
    }

    // 删除项目
    @RequestMapping(value = "/project/{id}", method = RequestMethod.DELETE)
    public String deleteProject(@PathVariable Long id) {
        factoryService.deleteProject(id);

        return "ok";
    }

    // 编辑项目
    @RequestMapping(value = "/project/{id}", method = RequestMethod.PATCH)
    public String editProject(@PathVariable Long id, @Valid @RequestBody FactoryService.EditProjectDto dto) {
        factoryService.editProject(id, dto);

        return "ok";
    }

    // 添加项目成员
    @RequestMapping(value = "/project/addWorkerToProject", method = RequestMethod.POST)
    public String addWorkerToProject(@Valid @RequestBody FactoryService.NewWorkerDto dto) {
        factoryService.addWorkerToProject(dto);

        return "ok";
    }

    // 删除项目成员
    @RequestMapping(value = "/project/{id}/removeWorkerFromProject/{userId}", method = RequestMethod.POST)
    public String removeWorkerFromProject(@PathVariable Long id, @PathVariable Long userId) {
        factoryService.removeWorkerFromProject(id, userId);

        return "ok";
    }

    // 添加项目成员小时计费
    @RequestMapping(value = "/project/{projectId}/addHourCostToProject/{userId}", method = RequestMethod.POST)
    public String addHourCostToProject(@PathVariable Long projectId, @PathVariable Long userId, @Valid @RequestBody FactoryService.NewHourCostDto dto) {
        factoryService.addHourCost(userId, projectId, dto);

        return "ok";
    }

    // 删除项目成员小时计费
    @RequestMapping(value = "/project/{id}/removeHourCostFromProject/{userId}", method = RequestMethod.POST)
    public String removeHourCostFromProject(@PathVariable Long id, @PathVariable Long userId, @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date date) {
        factoryService.removeHourCost(id, userId, date);

        return "ok";
    }

    // 添加项目成员佣金计费
    @RequestMapping(value = "/project/{id}/addHourCommissionToProject/{userId}", method = RequestMethod.POST)
    public String addHourCommissionToProject(@PathVariable Long id, @PathVariable Long userId, @Valid @RequestBody FactoryService.NewHourCommissionDto dto) {
        factoryService.addHourCommission(id, userId, dto);

        return "ok";
    }

    // 删除项目成员佣金计费
    @RequestMapping(value = "/project/{id}/removeHourCommissionFromProject/{userId}", method = RequestMethod.POST)
    public String removeHourCommissionFromProject(@PathVariable Long id, @PathVariable Long userId, @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date date) {
        factoryService.removeHourCommission(id, userId, date);

        return "ok";
    }
    // end 项目

    // 工作记录

    // 开始工作记录

    // 结束工作记录

    // 删除工作记录

    // end 工作记录

    // 支付记录

    // 添加支付记录

    // 删除支付记录

    // end 支付记录

    // 工作报告

    // 生成指定日期范围工作报告

    // end 工作报告

    // 用户

    // 添加用户

    // 删除用户

    // 禁用用户

    // 启用用户

    // 设置最后一次佣金结算相关工作记录截止日

    // end 用户
}
