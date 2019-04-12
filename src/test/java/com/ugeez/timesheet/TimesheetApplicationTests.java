package com.ugeez.timesheet;

import com.ugeez.timesheet.model.*;
import com.ugeez.timesheet.repository.CompanyRepository;
import com.ugeez.timesheet.repository.ProjectRepository;
import com.ugeez.timesheet.repository.UserRepository;
import com.ugeez.timesheet.repository.WorkRecordRepository;
import com.ugeez.timesheet.service.FactoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.testng.annotations.BeforeTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class TimesheetApplicationTests {
    private static boolean init = false;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    WorkRecordRepository workRecordRepository;

    @Before
    public void before() throws IOException {
        if (!init) {
            init = true;
            jdbcTemplate.execute("script to 'src/test/resources/dump.sql'");
            log.info("pptest create dump.sql");
        } else {
            jdbcTemplate.execute("DROP SEQUENCE HIBERNATE_SEQUENCE;");

            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES");
            tables.stream().forEach(item -> jdbcTemplate.execute("DROP TABLE " + item.get("TABLE_NAME")));

            FileReader fr = new FileReader(new File("src/test/resources/dump.sql"));
            BufferedReader br = new BufferedReader(fr);
            String lineStr;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineStr = br.readLine()) != null) {
                stringBuilder.append(lineStr);
            }
            br.close();

            String[] sqlCommands = stringBuilder.toString().split(";");

            for (String item : sqlCommands) {
                jdbcTemplate.execute(item);
            }

            log.info("pptest drop and restored");
        }
    }

    // -公司

    // --创建公司
    // ---成功
    @Test
    public void 公司_创建公司_成功() {
        HttpEntity<FactoryService.NewCompanyDto> request = new HttpEntity<>(
                new FactoryService.NewCompanyDto(
                        "ct1",
                        null,
                        null
                )
        );

        ResponseEntity<String> response = restTemplate.exchange("/company", HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        Company company = companyRepository.findOneByName("ct1");
        Assert.assertNotNull(company);
    }
    // ---

    // ---失败
    // ----创建重名公司
    @Test
    public void 公司_创建公司_失败_创建重名公司() {

        HttpEntity<FactoryService.NewCompanyDto> request = new HttpEntity<>(
                new FactoryService.NewCompanyDto(
                        "c1",
                        null,
                        null
                )
        );

        ResponseEntity<String> response = restTemplate.exchange("/company", HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    // ----

    // ---

    // --

    // --删除公司
    // ---成功
    @Test
    public void 公司_删除公司_成功() {
        Company company = companyRepository.findOneByName("c3");
        Assert.assertNotNull(company);

        ResponseEntity<String> response = restTemplate.exchange(
                "/company/" + company.getId(),
                HttpMethod.DELETE,
                new HttpEntity<String>(""),
                String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        company = companyRepository.findOneByName("c3");
        Assert.assertNull(company);
    }
    // ---

    // ---失败
    // ----删除已经有相关业务数据的公司
    // -----已有payment
    @Test
    public void 公司_删除公司_失败_删除已经有相关业务数据的公司_已有payment() {
        Company company = companyRepository.findOneByName("c4");
        Assert.assertNotNull(company);

        ResponseEntity<String> response = restTemplate.exchange(
                "/company/" + company.getId(),
                HttpMethod.DELETE,
                new HttpEntity<String>(""),
                String.class);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        company = companyRepository.findOneByName("c4");
        Assert.assertNotNull(company);
    }

    // -----
    // -----已有project
    @Test
    public void 公司_删除公司_失败_删除已经有相关业务数据的公司_已有project() {
        Company company = companyRepository.findOneByName("c5");
        Assert.assertNotNull(company);

        ResponseEntity<String> response = restTemplate.exchange(
                "/company/" + company.getId(),
                HttpMethod.DELETE,
                new HttpEntity<String>(""),
                String.class);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        company = companyRepository.findOneByName("c5");
        Assert.assertNotNull(company);
    }
    // -----
    // ----
    // ---
    // --

    // --编辑公司
    // ---成功
    // ----修改名字$联系人$电话
    @Test
    public void 公司_编辑公司_成功_修改名字修改名字$联系人$电话() {
        Company company = companyRepository.findOneByName("c1");

        HttpEntity<FactoryService.EditCompanyDto> request = new HttpEntity<>(
                new FactoryService.EditCompanyDto(
                        "c1c",
                        "cpc",
                        "pc")
        );

        ResponseEntity<String> response = restTemplate.exchange("/company/" + company.getId(), HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        Optional<Company> companyOptional = companyRepository.findById(company.getId());
        Company result = companyOptional.get();

        Assert.assertEquals("c1c", result.getName());
        Assert.assertEquals("cpc", result.getContactPerson());
        Assert.assertEquals("pc", result.getPhone());
    }
    // ----
    // ---

    // ---失败
    // ----修改名字为和其他存在的公司重名
    @Test
    public void 公司_编辑公司_失败_修改名字为和其他存在的公司重名() {
        Company company = companyRepository.findOneByName("c1");

        HttpEntity<FactoryService.EditCompanyDto> request = new HttpEntity<>(
                new FactoryService.EditCompanyDto(
                        "c2",
                        "cpc",
                        "pc")
        );

        ResponseEntity<String> response = restTemplate.exchange("/company/" + company.getId(), HttpMethod.PUT, request, String.class);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        Optional<Company> companyOptional = companyRepository.findById(company.getId());
        Company result = companyOptional.get();
        Assert.assertEquals("c1", result.getName());
    }
    // ---
    // --

    // --设置最后一次公司结算日
    // ---成功
    // ----修改截止日时非本公司有未结束的workRecord
    @Test
    public void 公司_设置最后一次公司结算日_成功_修改截止日时非本公司有未结束的workRecord() {
        Company company = companyRepository.findOneByName("c1");

        HttpEntity<String> request = new HttpEntity<>("");

        ResponseEntity<String> response = restTemplate.exchange(
                "/company/" + company.getId() + "/setCompanyWorkRecordFixedDate/2000-02-01",
                HttpMethod.POST,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        Optional<Company> companyOptional = companyRepository.findById(company.getId());
        Company result = companyOptional.get();
        Assert.assertEquals(LocalDate.of(2000, 2, 1), result.getWorkRecordFixedDate());
    }

    // ----
    // ---
    // ---失败
    // ----修改截止日时截止日前本公司有未结束的workRecord
    @Test
    public void 公司_设置最后一次公司结算日_失败_修改截止日时截止日前本公司有未结束的workRecord() {
        Company company = companyRepository.findOneByName("c6");

        HttpEntity<String> request = new HttpEntity<>("");

        ResponseEntity<String> response = restTemplate.exchange(
                "/company/" + company.getId() + "/setCompanyWorkRecordFixedDate/2000-02-01",
                HttpMethod.POST,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        Optional<Company> companyOptional = companyRepository.findById(company.getId());
        Company result = companyOptional.get();
        Assert.assertEquals(LocalDate.of(1900, 1, 1), result.getWorkRecordFixedDate());
    }
    // ----
    // ---
    // --

    // --查看公司预付款余额
    // ---成功
    // ----hourCost中途有调整过还包含没有开始的hourCost的例子
    @Test
    public void 公司_查看公司预付款余额_成功_hourCost中途有调整过还包含没有开始的hourCost的例子() {
        Company company = companyRepository.findOneByName("c1");

        HttpEntity<String> request = new HttpEntity<>("");

        ResponseEntity<String> response = restTemplate.exchange(
                "/company/" + company.getId() + "/checkBalance",
                HttpMethod.GET,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("290.0", response.getBody());
    }

    // ----
    // ---
    // ---失败
    // ----今天以前本公司有相关未结束的workRecord
    @Test
    public void 公司_查看公司预付款余额_失败_今天以前本公司有相关未结束的workRecord() {
        Company company = companyRepository.findOneByName("c6");

        HttpEntity<String> request = new HttpEntity<>("");

        ResponseEntity<String> response = restTemplate.exchange(
                "/company/" + company.getId() + "/checkBalance",
                HttpMethod.GET,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    // ----
    // ---


    // --

    // --查看公司到指定日期预付款余额
    // ---成功
    // ----hourCost中途有调整过还包含没有开始的hourCost的例子
    @Test
    public void 公司_查看公司到指定日期预付款余额_成功_hourCost中途有调整过还包含没有开始的hourCost的例子() {
        Company company = companyRepository.findOneByName("c1");

        HttpEntity<String> request = new HttpEntity<>("");

        ResponseEntity<String> response = restTemplate.exchange(
                "/company/" + company.getId() + "/checkBalance/2000-01-05",
                HttpMethod.GET,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("194.0", response.getBody());
    }

    // ----
    // ---
    // ---失败
    // ----指定日期以前本公司有相关未结束的workRecord
    @Test
    public void 公司_查看公司到指定日期预付款余额_失败_指定日期以前本公司有相关未结束的workRecord() {
        Company company = companyRepository.findOneByName("c6");

        HttpEntity<String> request = new HttpEntity<>("");

        ResponseEntity<String> response = restTemplate.exchange(
                "/company/" + company.getId() + "/checkBalance/2099-01-01",
                HttpMethod.GET,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    // ----
    // ---
    // --

    // -

    // -项目

    // --创建项目
    // ---成功
    @Test
    public void 项目_创建项目_成功() {
        Company company = companyRepository.findOneByName("c1");

        HttpEntity<FactoryService.NewProjectDto> request = new HttpEntity<>(new FactoryService.NewProjectDto(
                "c1pt1",
                company.getId()
        ));

        ResponseEntity<String> response = restTemplate.exchange(
                "/project/",
                HttpMethod.POST,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        Project project = projectRepository.findOneByName("c1pt1");
        Assert.assertNotNull(project);
    }

    // ---
    // ---失败
    // ----创建项目名称为已存在项目同名的项目
    @Test
    public void 项目_创建项目_失败_创建项目名称为已存在项目同名的项目() {
        Company company = companyRepository.findOneByName("c1");

        HttpEntity<FactoryService.NewProjectDto> request = new HttpEntity<>(new FactoryService.NewProjectDto(
                "c1p1",
                company.getId()
        ));

        ResponseEntity<String> response = restTemplate.exchange(
                "/project/",
                HttpMethod.POST,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    // ----
    // ---
    // --

    // --删除项目
    // ---成功
    // ----删除有worker的项目
    @Test
    public void 项目_删除项目_成功_删除有worker的项目() {
        Project project = projectRepository.findOneByName("c5p1");

        HttpEntity<String> request = new HttpEntity<>("");

        ResponseEntity<String> response = restTemplate.exchange(
                "/project/" + project.getId(),
                HttpMethod.DELETE,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        project = projectRepository.findOneByName("c5p1");
        Assert.assertNull(project);
    }

    // ----
    // ---
    // ---失败
    // ----删除已有workRecord的项目
    @Test
    public void 项目_删除项目_失败_删除已有workRecord的项目() {
        Project project = projectRepository.findOneByName("c1p1");

        HttpEntity<String> request = new HttpEntity<>("");

        ResponseEntity<String> response = restTemplate.exchange(
                "/project/" + project.getId(),
                HttpMethod.DELETE,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    // ----
    // ---
    // --

    // --编辑项目
    // ---成功
    @Test
    public void 项目_编辑项目_成功() {
        Project project = projectRepository.findOneByName("c1p1");

        HttpEntity<FactoryService.EditProjectDto> request = new HttpEntity<>(new FactoryService.EditProjectDto(
                "c1p1c"
        ));

        ResponseEntity<String> response = restTemplate.exchange(
                "/project/" + project.getId(),
                HttpMethod.PUT,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        project = projectRepository.findOneByName("c1p1c");
        Assert.assertNotNull(project);
    }

    // ---
    // ---失败
    // ----项目名称为已有项目同名
    @Test
    public void 项目_编辑项目_失败_项目名称为已有项目同名() {
        Project project = projectRepository.findOneByName("c1p1");

        HttpEntity<FactoryService.EditProjectDto> request = new HttpEntity<>(new FactoryService.EditProjectDto(
                "c1p2"
        ));

        ResponseEntity<String> response = restTemplate.exchange(
                "/project/" + project.getId(),
                HttpMethod.PUT,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    // ----
    // ---
    // --
    // -

    // --添加项目成员
    // ---成功
    // ----不指定hourCost和hourCommission
    @Test
    public void 项目_添加项目成员_成功_不指定hourCost和hourCommission() {
        User user = userRepository.findOneByUsernameEqualsIgnoreCase("u3");
        Project project = projectRepository.findOneByName("c1p1");

        HttpEntity<FactoryService.NewWorkerDto> request = new HttpEntity<>(new FactoryService.NewWorkerDto(
                user.getId(),
                project.getId(),
                null,
                null
        ));

        ResponseEntity<String> response = restTemplate.exchange(
                "/project/addWorkerToProject",
                HttpMethod.POST,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        project = projectRepository.findOneByName("c1p1");
        Assert.assertEquals(true, project.gainWorkerByUserId(user.getId()).isPresent());

        HourCost hourCost = project.gainWorkerByUserId(user.getId()).get().gainHourCosts().get(0);
        HourCommission hourCommission = project.gainWorkerByUserId(user.getId()).get().gainHourCommissions().get(0);
        Assert.assertEquals(user.getHourCostAmount(), hourCost.getAmount());
        Assert.assertEquals(LocalDate.of(1900, 1, 1), hourCost.getStartDate());
        Assert.assertEquals(user.getHourCommissionAmount(), hourCommission.getAmount());
        Assert.assertEquals(LocalDate.of(1900, 1, 1), hourCommission.getStartDate());
    }

    // ----
    // ----指定hourCost和hourCommission
    @Test
    public void 项目_添加项目成员_成功_指定hourCost和hourCommission() {
        User user = userRepository.findOneByUsernameEqualsIgnoreCase("u3");
        Project project = projectRepository.findOneByName("c1p1");

        HttpEntity<FactoryService.NewWorkerDto> request = new HttpEntity<>(new FactoryService.NewWorkerDto(
                user.getId(),
                project.getId(),
                1000.0,
                500.0
        ));

        ResponseEntity<String> response = restTemplate.exchange(
                "/project/addWorkerToProject",
                HttpMethod.POST,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        // 清空当前repository以从数据库获取最新数据
        entityManager.clear();

        project = projectRepository.findOneByName("c1p1");
        Assert.assertEquals(true, project.gainWorkerByUserId(user.getId()).isPresent());

        HourCost hourCost = project.gainWorkerByUserId(user.getId()).get().gainHourCosts().get(0);
        HourCommission hourCommission = project.gainWorkerByUserId(user.getId()).get().gainHourCommissions().get(0);
        Assert.assertEquals(new Double(1000.0), hourCost.getAmount());
        Assert.assertEquals(LocalDate.of(1900, 1, 1), hourCost.getStartDate());
        Assert.assertEquals(new Double(500.0), hourCommission.getAmount());
        Assert.assertEquals(LocalDate.of(1900, 1, 1), hourCommission.getStartDate());
    }
    // ----

    // ---
    // ---失败
    // ----指定已存在的成员
    @Test
    public void 项目_添加项目成员_失败_指定已存在的成员() {
        User user = userRepository.findOneByUsernameEqualsIgnoreCase("u1");
        Project project = projectRepository.findOneByName("c1p1");

        HttpEntity<FactoryService.NewWorkerDto> request = new HttpEntity<>(new FactoryService.NewWorkerDto(
                user.getId(),
                project.getId(),
                1000.0,
                500.0
        ));

        ResponseEntity<String> response = restTemplate.exchange(
                "/project/addWorkerToProject",
                HttpMethod.POST,
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    // ----
    // ---
    // --

    // --删除项目成员
    // ---成功
    // ---
    // ---失败
    // ----删除这个项目已有workRecord的成员
    // ----
    // ---
    // --

    // --添加项目成员小时计费
    // ---成功
    // ----指定date
    // ----
    // ----不指定date
    // ----
    // ---
    // --

    // --删除项目成员小时计费
    // ---成功
    // ---
    // ---失败
    // ----指定日期的用户hourCost不存在
    // ----
    // ---
    // --

    // --添加项目成员佣金计费
    // ---成功
    // ----指定date
    // ----
    // ----不指定date
    // ----
    // ---
    // ---失败
    // ----添加非项目worker的hourCommission
    // ----
    // ---
    // --

    // --删除项目成员佣金计费
    // ---成功
    // ---
    // ---失败
    // ----指定日期的用户hourCommission不存在
    // ----
    // ---
    // --

    // -workRecord

    // --开始workRecord
    // ---成功
    // ----指定start
    // ----
    // ----不指定start
    // ----
    // ---
    // ---失败
    // ----当前还存在没有结束的工作
    // ----
    // ----当前时间小于公司结算日(这个结算日在未来)
    // ----
    // ----指定start和同个用户在同个公司的workRecord重合
    // ----
    // ---
    // --

    // --结束workRecord
    // ---成功
    // ----指定end
    // ----
    // ----不指定end
    // ----
    // ---
    // ---失败
    // ----当前没有开始的workRecord
    // ----
    // ----指定end和同个用户在同个公司的workRecord重合
    // ----
    // ---
    // --

    // --添加workRecord
    // ---成功
    // ----添加单天workRecord
    // ----
    // ----添加跨天的workRecord
    // ----
    // ---
    // ---失败
    // ----不是项目worker的工作记录
    // ----
    // ---
    // --

    // --删除workRecord
    // ---成功
    // ---
    // --

    // --批量添加工作记录
    // ---成功
    // ---
    // ---失败
    // ----添加的用户不在指定项目的worker中
    // ----
    // ----添加的用户在指定项目有未结束的workRecord
    // ----
    // ----添加的用户在指定项目有时间段重合的workRecord
    // ----
    // ---
    // --
    // -

    // -payment
    // --添加payment
    // ---成功
    // ---
    // ---失败
    // ---
    // --

    // --删除payment
    // ---成功
    // ---
    // ---失败
    // ---
    // --
    // -

    // -工作报告
    // --生成指定日期范围工作报告
    // --
    // --生成指定日期范围工作报告, 并设置公司结算日
    // ---成功
    // ---
    // ---失败
    // ----指定日期在公司结算日之前
    // ----
    // ---
    // --
    // -

    // -相关性测试
    // --只能添加公司结算日之后的workRecord
    // --
    // --只能删除公司结算日之后的workRecord
    // --
    // --只能添加公司结算日之后的worker的hourCost
    // --
    // --只能删除公司结算日之后的worker的hourCost
    // --
    // --只能添加公司结算日之后的worker的hourCommission
    // --
    // --只能删除公司结算日之后的worker的hourCommission
    // --
    // --只能添加公司结算日之后的payment
    // --
    // --只能删除公司结算日之后的payment
    // --
    // -

    // -复杂综合流程测试
    // -


}
