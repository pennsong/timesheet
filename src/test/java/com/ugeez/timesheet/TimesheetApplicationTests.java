package com.ugeez.timesheet;

import com.ugeez.timesheet.model.Company;
import com.ugeez.timesheet.repository.CompanyRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.testng.annotations.BeforeTest;

import java.io.*;
import java.util.List;
import java.util.Map;
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
public class TimesheetApplicationTests {
    private static boolean init = false;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    CompanyRepository companyRepository;

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

            for (String item: sqlCommands) {
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
        log.info("pptest 公司_创建公司_成功");
        HttpEntity<FactoryService.NewCompanyDto> request = new HttpEntity<>(
                new FactoryService.NewCompanyDto(
                        "ct1",
                        null,
                        null
                )
        );

        ResponseEntity<String> response = restTemplate.exchange("/company", HttpMethod.POST, request, String.class);

        Company company = companyRepository.findOneByName("ct1");

        Assert.assertNotNull(company);
    }
    // ---

    // ---失败
    // ----创建重名公司
    @Test
    public void 公司_创建公司_失败_创建重名公司() throws Exception {
        log.info("pptest 公司_创建公司_失败_创建重名公司");

        Iterable<Company> companies = companyRepository.findAll();

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
    // ---

    // ---失败
    // ----删除已经有相关业务数据的公司
    // -----已有payment
    // -----
    // -----已有project
    // -----
    // ----
    // ---
    // --

    // --编辑公司
    // ---成功
    // ----修改名字, 联系人, 电话
    // ----
    // ---

    // ---失败
    // ----修改名字为和其他存在的公司重名
    // ---
    // --

    // --设置最后一次公司结算日
    // ---成功
    // ----修改截止日时非本公司有未结束的workRecord
    // ----
    // ---
    // ---失败
    // ----修改截止日时本公司有未结束的workRecord
    // ----
    // ---
    // --

    // --查看公司预付款余额
    // ---hourCost中途有调整过, 还包含没有开始的hourCost的例子
    // ---
    // --

    // --查看公司到指定日期预付款余额
    // ---hourCost中途有调整过, 还包含没有开始的hourCost的例子
    // ---
    // --

    // -

    // -项目

    // --创建项目
    // ---成功
    // ---
    // ---失败
    // ----创建项目名称为已存在项目同名的项目
    // ----
    // ---
    // --

    // --删除项目
    // ---成功
    // ----删除空项目
    // ----
    // ----删除有worker的项目
    // ----
    // ---
    // ---失败
    // ----删除已有workRecord的项目
    // ----
    // ---
    // --

    // --编辑项目
    // ---成功
    // ---
    // ---失败
    // ----项目名称为已有项目同名
    // ----
    // ---
    // --
    // -

    // --添加项目成员
    // ---成功
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
