package com.ugeez.timesheet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugeez.timesheet.model.Project;
import com.ugeez.timesheet.model.User;
import com.ugeez.timesheet.model.WorkRecord;
import com.ugeez.timesheet.repository.ProjectRepository;
import com.ugeez.timesheet.repository.UserRepository;
import com.ugeez.timesheet.repository.WorkRecordRepository;
import com.ugeez.timesheet.service.FactoryService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
    public String setCompanyWorkRecordFixedDate(@PathVariable Long id, @PathVariable LocalDate date) {
        factoryService.setCompanyWorkRecordFixedDate(id, date);

        return "ok";
    }

    // 查看公司预付款余额
    @RequestMapping(value = "/company/{id}/checkBalance", method = RequestMethod.GET)
    public String gainBalance(@PathVariable Long id) {
        return "ok" + factoryService.gainCompanyBalance(id);
    }

    @RequestMapping(value = "/company/{id}/checkBalance/{end}", method = RequestMethod.GET)
    public String gainBalanceByDate(@PathVariable Long id, @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date end) {
        return "ok" + factoryService.gainCompanyBalanceByDate(id, end);
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
    public String removeHourCostFromProject(@PathVariable Long id, @PathVariable Long userId, @PathVariable LocalDate date) {
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
    public String removeHourCommissionFromProject(@PathVariable Long id, @PathVariable Long userId, @PathVariable LocalDate date) {
        factoryService.removeHourCommission(id, userId, date);

        return "ok";
    }
    // end 项目

    // 工作记录

    // 开始工作记录
    @RequestMapping(value = "/workRecord/startWork", method = RequestMethod.POST)
    public String startWork(@Valid @RequestBody FactoryService.StartWorkDto dto) {
        factoryService.startWork(dto);

        return "ok";
    }

    // 结束工作记录
    @RequestMapping(value = "/workRecord/stopWork", method = RequestMethod.POST)
    public String stopWork(@Valid @RequestBody FactoryService.StopWorkDto dto) {
        factoryService.stopWork(dto);

        return "ok";
    }

    // 删除工作记录
    @RequestMapping(value = "/workRecord/{id}", method = RequestMethod.DELETE)
    public String deleteWorkRecord(@PathVariable Long id) {
        factoryService.deleteWorkRecord(id);

        return "ok";
    }

    // end 工作记录

    // 支付记录

    // 添加支付记录
    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    public String createPayment(@Valid @RequestBody FactoryService.NewPaymentDto dto) {
        factoryService.createPayment(dto);

        return "ok";
    }

    // 删除支付记录
    @RequestMapping(value = "/payment/{id}", method = RequestMethod.DELETE)
    public String deletePayment(@PathVariable Long id) {
        factoryService.deletePayment(id);

        return "ok";
    }

    // end 支付记录

    // 工作报告

    // 生成指定日期范围工作报告
    @RequestMapping(value = "/report/{companyId}/{start}/{end}", method = RequestMethod.GET)
    public String report(@PathVariable Long companyId, @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date start, @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date end) throws JSONException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JSONObject jsonObject = factoryService.genReport(companyId, start, end);

        return jsonObject.toString();
    }

    // end 工作报告

    // 用户

    // 添加用户

    // 删除用户

    // 禁用用户

    // 启用用户

    // 设置最后一次佣金结算相关工作记录截止日

    // end 用户
}
