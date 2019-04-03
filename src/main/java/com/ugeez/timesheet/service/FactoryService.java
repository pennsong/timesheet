package com.ugeez.timesheet.service;

import com.ugeez.timesheet.model.*;
import com.ugeez.timesheet.repository.*;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public void createCompany(NewCompanyDto dto) {
        Company company = genCompany(dto);
        companyRepository.save(company);
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

    public void setCompanyWorkRecordFixedDate(Long id, Date date) {
        Company company = gainEntityWithExistsChecking(Company.class, id);
        company.setWorkRecordFixedDate(date);
    }

    public Double calCompanyWorkCost(Long companyId) {
        List<WorkRecord> workRecords = workRecordRepository.findByProjectCompanyId(companyId);
        return workRecords.stream().mapToDouble(item -> item.calCost()).sum();
    }

    public Double gainCompanyBalance(Long id) {
        Company company = gainEntityWithExistsChecking(Company.class, id);

        // 遍历此公司的所有项目, 获得总花费
        Double totalCost = calCompanyWorkCost(id);

        // 遍历此公司的所有支付, 获得总收入
        List<Payment> paymentList = paymentRepository.findByCompanyId(id);
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

    public void createProject(NewProjectDto dto) {
        Project project = genProject(dto);
        projectRepository.save(project);
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
        hourCosts.add(new HourCost(new Date(0, 0, 0), dto.hourCostAmount));

        List<HourCommission> hourCommissions = new ArrayList<>();
        hourCommissions.add(new HourCommission(new Date(0, 0, 0), user.getHourCommissionAmount()));

        Worker worker = new Worker(null, user, project, hourCosts, hourCommissions);

        project.addWorker(worker);

        // todo 为什么注释掉下面一句后验证通不过worker的hourCosts, hourCommissions为空?
//        projectRepository.save(project);
    }

    public void removeWorkerFromProject(Long id, Long userId) {
        Project project = gainEntityWithExistsChecking(Project.class, id);
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
            dto.setDate(new Date());
        }

        //如果date <= 项目所属company的workRecordFixedDate, 则不允许
        if (dto.date.compareTo(project.gainCompanyWorkRecordFixedDate()) <= 0) {
            throw new RuntimeException("开始时间不能早于或等于项目所属公司的最后结算日期!");
        }

        project.addHourCost(userId, dto.date, dto.hourCostAmount);
    }

    public void removeHourCost(Long userId, Long projectId, Date start) {
        Project project = gainEntityWithExistsChecking(Project.class, projectId);

        //如果date <= 项目所属company的workRecordFixedDate, 则不允许
        if (start.compareTo(project.gainCompanyWorkRecordFixedDate()) <= 0) {
            throw new RuntimeException("删除记录开始时间不能早于或等于项目所属公司的最后结算日期!");
        }

        project.removeHourCost(userId, start);
    }

    @Data
    @AllArgsConstructor
    public static class NewHourCostDto {
        @Positive
        private Double hourCostAmount;

        @Temporal(TemporalType.DATE)
        private Date date;
    }
    // end HourCost

    // HourCommission
    public void addHourCommission(Long userId, Long projectId, NewHourCommissionDto dto) {
        Project project = gainEntityWithExistsChecking(Project.class, projectId);
        User user = gainEntityWithExistsChecking(User.class, userId);

        if (dto.date == null) {
            dto.setDate(new Date());
        }

        //如果date <= user.getLastSettlementDate(), 则不允许
        if (dto.date.compareTo(user.getLastSettlementDate()) <= 0) {
            throw new RuntimeException("开始时间不能早于或等于用户的最后结算日期!");
        }

        project.addHourCommission(userId, dto.date, dto.hourCommissionAmount);
    }

    public void removeHourCommission(Long userId, Long projectId, Date start) {
        Project project = gainEntityWithExistsChecking(Project.class, projectId);
        User user = gainEntityWithExistsChecking(User.class, userId);

        //如果date <= user.getLastSettlementDate(), 则不允许
        if (start.compareTo(user.getLastSettlementDate()) <= 0) {
            throw new RuntimeException("开始时间不能早于或等于用户的最后结算日期!");
        }

        project.removeHourCommission(userId, start);
    }

    @Data
    @AllArgsConstructor
    public static class NewHourCommissionDto {
        @Positive
        private Double hourCommissionAmount;

        @Temporal(TemporalType.DATE)
        @DateTimeFormat(pattern = "yyyyMMdd")
        private Date date;
    }
    // end HourCommission
    // end 项目

    // WorkRecord
    public void startWork(StartWorkDto dto) {
        if (dto.start == null) {
            dto.start = new Date();
        }

        User user = gainEntityWithExistsChecking(User.class, dto.userId);
        Project project = gainEntityWithExistsChecking(Project.class, dto.projectId);

        // 如果此用户当前有未完成work, 则不允许再开始work
        Optional<WorkRecord> workRecord = workRecordRepository.findOneByUserIdAndEndIsNull(user.getId());

        if (workRecord.isPresent()) {
            throw new RuntimeException("请先结束当前工作!");
        }

        WorkRecord newWorkRecord = new WorkRecord(null, dto.start, dto.start, null, project, null, user);

        workRecordRepository.save(newWorkRecord);
    }

    public void stopWork(StopWorkDto dto) {
        if (dto.end == null) {
            dto.end = new Date();
        }

        User user = gainEntityWithExistsChecking(User.class, dto.userId);
        Project project = gainEntityWithExistsChecking(Project.class, dto.projectId);

        // 如果此用户当前没未完成work, 则不允许结束work
        Optional<WorkRecord> workRecord = workRecordRepository.findOneByUserIdAndEndIsNull(user.getId());

        if (!(workRecord.isPresent())) {
            throw new RuntimeException("请先开始工作!");
        }

        WorkRecord endWorkRecord = workRecord.get();

        // 如果工作记录start的24:00则以天为单位分开

        endWorkRecord.setEnd(dto.end);
        endWorkRecord.setNote(dto.note);
    }

    @Data
    @AllArgsConstructor
    public static class StartWorkDto {
        @NotNull
        private Long userId;

        @NotNull
        private Long projectId;

        @Temporal(TemporalType.TIMESTAMP)
        private Date start;
    }

    @Data
    @AllArgsConstructor
    public static class StopWorkDto {
        @NotNull
        private Long userId;

        @NotNull
        private Long projectId;

        @Temporal(TemporalType.TIMESTAMP)
        private Date end;

        @NotEmpty
        private String note;
    }
    // end WorkRecord

    // 支付
    public Payment genPayment(NewPaymentDto dto) {
        Company company = gainEntityWithExistsChecking(Company.class, dto.companyId);
        return new Payment(null, dto.date, dto.amount, company);
    }

    public void createPayment(NewPaymentDto dto) {
        Payment payment = genPayment(dto);
        paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        Payment payment = gainEntityWithExistsChecking(Payment.class, id);
        paymentRepository.delete(payment);
    }

    @Data
    @AllArgsConstructor
    public static class NewPaymentDto {
        @NotNull
        @Positive
        private Double amount;

        @NotNull
        @Temporal(TemporalType.DATE)
        private Date date;

        @NotNull
        @Positive
        private Long companyId;
    }
    // end 支付

    // 用户
    public User genUser(String username, Double hourCostAmount, Double hourCommissionAmount) {
        return new User(null, username, "1", hourCostAmount, hourCommissionAmount, null);
    }

    public void createUser(String username, Double hourCostAmount, Double hourCommissionAmount) {
        User user = genUser(username, hourCostAmount, hourCommissionAmount);
        userRepository.save(user);
    }
    // end 用户
}
