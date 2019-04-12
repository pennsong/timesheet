package com.ugeez.timesheet.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ugeez.timesheet.model.*;
import com.ugeez.timesheet.repository.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class FactoryService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private WorkRecordRepository workRecordRepository;

    @Autowired
    private UserRepository userRepository;

    private Repositories repositories;

    public FactoryService(ListableBeanFactory listableBeanFactory) {
        this.repositories = new Repositories(listableBeanFactory);
    }

    // 检查Entity是否存在
    public <T> T gainEntityWithExistsChecking(Class<T> tClass, Object id) {
        Optional repositoryObject = repositories.getRepositoryFor(tClass);

        if (!(repositoryObject.isPresent())) {
            throw new RuntimeException("操作的记录所属的Repository不存在!");
        }

        CrudRepository crudRepository = (CrudRepository) repositoryObject.get();

        Optional<T> t = crudRepository.findById(id);
        if (!(t.isPresent())) {
            throw new RuntimeException("操作的记录不存在!");
        } else {
            return t.get();
        }
    }

    // 公司
    public Company genCompany(NewCompanyDto dto) {
        return new Company(null, dto.name, null, dto.contactPerson, dto.phone);
    }

    public Company createCompany(NewCompanyDto dto) {
        Company company = genCompany(dto);
        return companyRepository.save(company);
    }

    public void deleteCompany(Long id) {
        Company company = gainEntityWithExistsChecking(Company.class, id);
        companyRepository.delete(company);
    }

    public void editCompany(Long id, EditCompanyDto dto) {
        Company company = gainEntityWithExistsChecking(Company.class, id);
        company.setName(dto.name);
        company.setContactPerson(dto.contactPerson);
        company.setPhone(dto.phone);
    }

    public void checkUnfinishedWorkRecordsByDate(Long companyId, LocalDate date) {
        List<WorkRecord> workRecords = workRecordRepository.findCompanyUnfinishedWorkRecordsByDate(companyId, date.plusDays(1).atStartOfDay());
        if (workRecords.size() > 0) {
            throw new RuntimeException("请先结束以下工作记录:" + workRecords.toString());
        }
    }

    public void setCompanyWorkRecordFixedDate(Long id, LocalDate date) {
        Company company = gainEntityWithExistsChecking(Company.class, id);

        // 修改截止日时截止日前(包括)本公司有未结束的workRecord, 则不允许修改
        checkUnfinishedWorkRecordsByDate(id, date);

        company.setWorkRecordFixedDate(date);
    }

    public Double calCompanyWorkCost(Long companyId) {
        List<WorkRecord> workRecords = workRecordRepository.findByProjectCompanyId(companyId);
        return workRecords.stream().mapToDouble(item -> item.calCost()).sum();
    }

    public Double calCompanyWorkCostByDate(Long companyId, LocalDate end) {
        List<WorkRecord> workRecords = workRecordRepository.findByProjectCompanyIdAndDateIsLessThanEqual(companyId, end);
        return workRecords.stream().mapToDouble(item -> item.calCost()).sum();
    }

    public Double gainCompanyBalance(Long id) {
        Company company = gainEntityWithExistsChecking(Company.class, id);

        // 修改截止日时截止日前(包括)本公司有未结束的workRecord, 则不允许修改
        checkUnfinishedWorkRecordsByDate(id, LocalDate.now());

        // 遍历此公司的所有项目, 获得总花费
        Double totalCost = calCompanyWorkCost(id);

        // 遍历此公司的所有支付, 获得总收入
        List<Payment> paymentList = paymentRepository.findByCompanyId(id);
        Double totalIncome = paymentList.stream().mapToDouble(item -> item.getAmount()).sum();

        // 总收入 - 总花费 = balance
        return totalIncome - totalCost;
    }

    public Double gainCompanyBalanceByDate(Long id, LocalDate end) {
        Company company = gainEntityWithExistsChecking(Company.class, id);

        // 修改截止日时截止日前(包括)本公司有未结束的workRecord, 则不允许修改
        checkUnfinishedWorkRecordsByDate(id, end);

        // 遍历此公司的所有项目, 获得总花费
        Double totalCost = calCompanyWorkCostByDate(id, end);

        // 遍历此公司的所有支付, 获得总收入
        List<Payment> paymentList = paymentRepository.findByCompanyIdAndDateIsLessThanEqual(id, end);
        Double totalIncome = paymentList.stream().mapToDouble(item -> item.getAmount()).sum();

        // 总收入 - 总花费 = balance
        return totalIncome - totalCost;
    }

    @Data
    @AllArgsConstructor
    public static class NewCompanyDto {
        @NotEmpty
        private String name;

        private String contactPerson;

        private String phone;
    }

    @Data
    @AllArgsConstructor
    public static class EditCompanyDto {
        @NotEmpty
        private String name;

        private String contactPerson;

        private String phone;
    }
    // end 公司

    // 项目
    public Project genProject(NewProjectDto dto) {
        Company company = gainEntityWithExistsChecking(Company.class, dto.companyId);
        return new Project(null, dto.name, company, null);
    }

    public Project createProject(NewProjectDto dto) {
        Project project = genProject(dto);
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        Project project = gainEntityWithExistsChecking(Project.class, id);
        projectRepository.delete(project);
    }

    public void editProject(Long id, EditProjectDto dto) {
        Project project = gainEntityWithExistsChecking(Project.class, id);
        project.setName(dto.name);
    }

    @Data
    @AllArgsConstructor
    public static class NewProjectDto {
        @NotEmpty
        private String name;

        @NotNull
        @Positive
        private Long companyId;
    }

    @Data
    @AllArgsConstructor
    public static class EditProjectDto {
        @NotEmpty
        private String name;

        // todo 找到原因: 去掉下面一个field, 这个class只留1个field, 会报错误: JSON parse error
        private String foo;

        public EditProjectDto(String name) {
            this.name = name;
        }
    }

    // Worker
    public void addWorkerToProject(NewWorkerDto dto) {
        User user = gainEntityWithExistsChecking(User.class, dto.userId);
        Project project = gainEntityWithExistsChecking(Project.class, dto.projectId);

        if (dto.hourCostAmount == null) {
            dto.setHourCostAmount(user.getHourCostAmount());
        }

        if (dto.hourCommissionAmount == null) {
            dto.setHourCommissionAmount(user.getHourCommissionAmount());
        }

        List<HourCost> hourCosts = new ArrayList<>();
        hourCosts.add(new HourCost(LocalDate.of(1900, 1, 1), dto.hourCostAmount));

        List<HourCommission> hourCommissions = new ArrayList<>();
        hourCommissions.add(new HourCommission(LocalDate.of(1900, Month.JANUARY, 1), dto.getHourCommissionAmount()));

        Worker worker = new Worker(null, user, project, hourCosts, hourCommissions);

        project.addWorker(worker);
    }

    public void removeWorkerFromProject(Long id, Long userId) {
        Project project = gainEntityWithExistsChecking(Project.class, id);

        // 如果这个user在这个项目已有工作记录则不允许删除
        Long count = workRecordRepository.workRecordExist(id, userId);
        if (count > 0) {
            throw new RuntimeException("此用户已存在workRecord, 不能删除!");
        }

        project.removeWorker(userId);
    }

    @Data
    @AllArgsConstructor
    public static class NewWorkerDto {
        @NotNull
        @Positive
        private Long userId;

        @NotNull
        @Positive
        private Long projectId;

        @Positive
        private Double hourCostAmount;

        @Positive
        private Double hourCommissionAmount;
    }
    // end Worker

    // HourCost
    public void addHourCost(Long userId, Long projectId, NewHourCostDto dto) {
        Project project = gainEntityWithExistsChecking(Project.class, projectId);

        if (dto.date == null) {
            dto.setDate(LocalDate.now());
        }

        //如果date <= 项目所属company的workRecordFixedDate, 则不允许
        if (dto.date.compareTo(project.getCompany().getWorkRecordFixedDate()) <= 0) {
            throw new RuntimeException("开始时间不能早于或等于项目所属公司的最后结算日期!");
        }

        project.addHourCost(userId, dto.date, dto.hourCostAmount);
    }

    public void removeHourCost(Long userId, Long projectId, LocalDate start) {
        Project project = gainEntityWithExistsChecking(Project.class, projectId);

        //如果date <= 项目所属company的workRecordFixedDate, 则不允许
        if (start.isBefore(project.getCompany().getWorkRecordFixedDate())) {
            throw new RuntimeException("删除记录开始时间不能早于或等于项目所属公司的最后结算日期!");
        }

        project.removeHourCost(userId, start);
    }

    @Data
    @AllArgsConstructor
    public static class NewHourCostDto {
        @Positive
        private Double hourCostAmount;

        private LocalDate date;
    }
    // end HourCost

    // HourCommission
    public void addHourCommission(Long userId, Long projectId, NewHourCommissionDto dto) {
        Project project = gainEntityWithExistsChecking(Project.class, projectId);
        User user = gainEntityWithExistsChecking(User.class, userId);

        if (dto.date == null) {
            dto.setDate(LocalDate.now());
        }

        //如果date <= user.getLastSettlementDate(), 则不允许
        if (dto.date.isBefore(user.getLastSettlementDate())) {
            throw new RuntimeException("开始时间不能早于或等于用户的最后结算日期!");
        }

        project.addHourCommission(userId, dto.date, dto.hourCommissionAmount);
    }

    public void removeHourCommission(Long userId, Long projectId, LocalDate start) {
        Project project = gainEntityWithExistsChecking(Project.class, projectId);
        User user = gainEntityWithExistsChecking(User.class, userId);

        //如果start <= user.getLastSettlementDate(), 则不允许
        if (start.isBefore(user.getLastSettlementDate())) {
            throw new RuntimeException("开始时间不能早于或等于用户的最后结算日期!");
        }

        project.removeHourCommission(userId, start);
    }

    @Data
    @AllArgsConstructor
    public static class NewHourCommissionDto {
        @Positive
        private Double hourCommissionAmount;

        private LocalDate date;
    }
    // end HourCommission
    // end 项目

    // WorkRecord
    public void startWork(StartWorkDto dto) {
        if (dto.start == null) {
            dto.start = LocalDateTime.now();
        }

        User user = gainEntityWithExistsChecking(User.class, dto.userId);
        Project project = gainEntityWithExistsChecking(Project.class, dto.projectId);

        // 如果此用户当前有未完成work, 则不允许再开始work
        Optional<WorkRecord> workRecord = workRecordRepository.findOneByUserIdAndEndIsNull(user.getId());

        if (workRecord.isPresent()) {
            throw new RuntimeException("请先结束当前工作!");
        }

        WorkRecord newWorkRecord = new WorkRecord(null, dto.start.toLocalDate(), dto.start, null, project, null, user);

        workRecordRepository.save(newWorkRecord);
    }

    public void stopWork(StopWorkDto dto) {
        if (dto.end == null) {
            dto.end = LocalDateTime.now();
        }

        User user = gainEntityWithExistsChecking(User.class, dto.userId);
        Project project = gainEntityWithExistsChecking(Project.class, dto.projectId);

        // 如果此用户当前没未完成work, 则不允许结束work
        Optional<WorkRecord> workRecord = workRecordRepository.findOneByUserIdAndEndIsNull(user.getId());

        if (!(workRecord.isPresent())) {
            throw new RuntimeException("请先开始工作!");
        }

        WorkRecord endWorkRecord = workRecord.get();
        endWorkRecord.setNote(dto.note);

        // 如果工作记录start的24:00则以天为单位分开
        List<WorkRecord> workRecords;

        workRecords = endWorkRecord.splitWorkRecordToDay(dto.end);

        for (WorkRecord item : workRecords) {
            workRecordRepository.save(item);
        }
    }

    public void createWorkRecord(NewWorkRecordDto dto) {
        if (!(dto.start.isBefore(dto.end))) {
            throw new RuntimeException("开始时间要小于结束时间!");
        }

        User user = gainEntityWithExistsChecking(User.class, dto.userId);
        Project project = gainEntityWithExistsChecking(Project.class, dto.projectId);

        // 判断同一个公司的项目上如有重合时间段的workRecord, 则不允许添加
        Long count = workRecordRepository.findByOverlapWorkRecords(project.getCompany().getId(), dto.userId, dto.getStart(), dto.getEnd());

        if (count > 0) {
            throw new RuntimeException("本用户在本公司的项目中有重合的工作记录, 不能添加!");
        }

        WorkRecord workRecord = new WorkRecord(null, dto.start.toLocalDate(), dto.start, dto.end, project, dto.note, user);

        // 如果工作记录start的24:00则以天为单位分开
        List<WorkRecord> workRecords;

        workRecords = workRecord.splitWorkRecordToDay(dto.end);

        workRecordRepository.save(workRecord);

        for (WorkRecord item : workRecords) {
            workRecordRepository.save(item);
        }
    }

    public void deleteWorkRecord(Long id) {
        WorkRecord workRecord = gainEntityWithExistsChecking(WorkRecord.class, id);

        // 判断是否可以删除
        // 相关Company workRecordFixedDate
        Company company = workRecord.getProject().getCompany();
        if (workRecord.getDate().isBefore(company.getWorkRecordFixedDate())) {
            throw new RuntimeException("工作记录的时间早于所属公司的结算时间, 不能修改!");
        }

        // 相关User lastSettlementDate
        User user = workRecord.getUser();
        if (workRecord.getDate().isBefore(user.getLastSettlementDate())) {
            throw new RuntimeException("工作记录的时间早于所属用户的结算时间, 不能修改!");
        }
        // end 判断是否可以删除

        workRecordRepository.delete(workRecord);
    }

    @Data
    @AllArgsConstructor
    public static class StartWorkDto {
        @NotNull
        private Long userId;

        @NotNull
        private Long projectId;

        private LocalDateTime start;
    }

    @Data
    @AllArgsConstructor
    public static class StopWorkDto {
        @NotNull
        private Long userId;

        @NotNull
        private Long projectId;

        private LocalDateTime end;

        @NotEmpty
        private String note;
    }

    @Data
    @AllArgsConstructor
    public static class NewWorkRecordDto {
        @NotNull
        private Long userId;

        @NotNull
        private Long projectId;

        @NotNull
        private LocalDateTime start;

        @NotNull
        private LocalDateTime end;

        @NotEmpty
        private String note;
    }
    // end WorkRecord

    // 支付
    public Payment genPayment(NewPaymentDto dto) {
        Company company = gainEntityWithExistsChecking(Company.class, dto.companyId);

        // 只允许添加结算日期之后的payment
        if (company.getWorkRecordFixedDate().isAfter(dto.getDate())) {
            throw new RuntimeException("支付时间小于等于公司结算日期, 不允许添加!");
        }

        return new Payment(null, dto.date, dto.amount, company, dto.note);
    }

    public void createPayment(NewPaymentDto dto) {
        Payment payment = genPayment(dto);
        paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        Payment payment = gainEntityWithExistsChecking(Payment.class, id);
        Company company = payment.getCompany();

        // 只允许删除结算日期之后的payment
        if (company.getWorkRecordFixedDate().isAfter(payment.getDate())) {
            throw new RuntimeException("支付时间小于等于公司结算日期, 不允许删除!");
        }

        paymentRepository.delete(payment);
    }

    @Data
    @AllArgsConstructor
    public static class NewPaymentDto {
        @NotNull
        @Positive
        private Double amount;

        @NotNull
        private LocalDate date;

        @NotNull
        @Positive
        private Long companyId;

        private String note;
    }
    // end 支付

    // 用户
    public User genUser(String username, Double hourCostAmount, Double hourCommissionAmount) {
        return new User(null, username, "1", hourCostAmount, hourCommissionAmount, null);
    }

    public User createUser(String username, Double hourCostAmount, Double hourCommissionAmount) {
        User user = genUser(username, hourCostAmount, hourCommissionAmount);
        return userRepository.save(user);
    }
    // end 用户

    // report
    public JSONObject genReport(Long companyId, LocalDate start, LocalDate end) throws JSONException {
        // 取得start到end之间的工作记录
        List<WorkRecord> workRecords = workRecordRepository.findByProjectCompanyIdAndDateIsGreaterThanEqualAndDateIsLessThanEqual(companyId, start, end);
        List<String> wordRecordsReport = workRecords.stream().map(item -> "" + item + ", " + item.calCost()).collect(Collectors.toList());

        // 取得start的balance
        Double startBalance = gainCompanyBalanceByDate(companyId, start.minus(1, DAYS));

        // 取得end的balance
        Double endBalance = gainCompanyBalanceByDate(companyId, end);

        // 取得start到end中的支付
        List<Payment> paymentReport = paymentRepository.findByCompanyIdAndDateIsGreaterThanEqualAndDateIsLessThanEqual(companyId, start, end);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("日期范围", start + "到" + end);

        jsonObject.put("期初Balance", startBalance);

        jsonObject.put("充值", paymentReport);

        jsonObject.put("花费", wordRecordsReport);

        jsonObject.put("期末Balance", endBalance);

        return jsonObject;
    }
    // end report
}
