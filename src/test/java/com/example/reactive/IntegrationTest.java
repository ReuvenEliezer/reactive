package com.example.reactive;

import com.example.reactive.dto.Employee;
import com.example.reactive.dto.Gender;
import com.example.reactive.dto.Salaries;
import com.example.reactive.repositories.EmployeeRepository;
import com.example.reactive.repositories.SalariesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ReactiveApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
    private static final int MAX_RETRY = 3;
    private static final Duration FIXED_DELAY_ON_RETRY = Duration.ofSeconds(1);

    @Value("${server.port}")
    private int appPort;

    @Value("${spring.r2dbc.url}")
    private String dbType;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SalariesRepository salariesRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private WebClient webClient;

    @Autowired
    private RSocketRequester rSocketRequester;


    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll().block();
        salariesRepository.deleteAll().block();
    }

    @Test
    void bufferQueryTest() {
        int employeeCount = 5;
        List<Employee> employees = Flux.range(0, employeeCount)
                .index()
                .flatMap(iteration ->
                        createEmployee(new Employee("E " + iteration.getT2(), "R"))
                )
                .doOnComplete(() -> logger.info("Complete to create employees"))
                .collectList()
                .block();
        assertThat(employees).hasSize(employeeCount);
        Long totalSaved = employeeRepository.findAll().count().block();
        assertThat(totalSaved).isEqualTo(employeeCount);
    }

    public Mono<Employee> createEmployee(Employee employee) {
        return webClient.post()
                .uri("http://localhost:" + appPort + "/employee/create")
                .bodyValue(employee)
                .retrieve()
                .bodyToMono(Employee.class)
                .retryWhen(Retry.fixedDelay(MAX_RETRY, FIXED_DELAY_ON_RETRY))
                .doOnNext(employee1 -> logger.info("Created new employee: {}", employee1))
                .onErrorResume(e -> {
                    logger.error("Failed to create employee", e);
                    return Mono.empty();
                });
    }

    @Test
    void createNewEmployeeTest() {
        Employee employee = createEmployee(new Employee("E", "R")).block();
        assertThat(employee).isNotNull();
        assertThat(employee.empNo()).isNotNull();
    }

    @Test
    void createNewEmployeeTest1() {
        Employee employee = rSocketRequester.route("create-employee")
                .data(new Employee("E", "R"))
                .retrieveMono(Employee.class)
                .doOnNext(employee1 -> logger.info("Created new employee: {}", employee1))
                .block();
        assertThat(employee).isNotNull();
        assertThat(employee.empNo()).isNotNull();
    }

    @Test
    void createNewEmployeeFullDataTest() {
        Employee employee = createEmployee(
                new Employee(
                        "E",
                        "R",
                        Gender.MALE,
                        LocalDate.now().minusYears(22),
                        LocalDate.now()
                ))
                .block();
        assertThat(employee).isNotNull();
        assertThat(employee.empNo()).isNotNull();
        assertThat(employee.birthDate()).isNotNull();
    }

    @Test
    void employeeWithSalaryTest() {
        Employee employee = createEmployee(
                new Employee(
                        "E",
                        "R",
                        Gender.MALE,
                        LocalDate.now().minusYears(22),
                        LocalDate.now()
                ))
                .block();
        assertThat(employee).isNotNull();

        Salaries salaries = salariesRepository.save(new Salaries(employee.empNo(), 1000, LocalDate.now().minusYears(2), LocalDate.now())).block();
        assertThat(salaries).isNotNull();

        List<Salaries> salaryList = salariesRepository.findAllByEmpNo(employee.empNo()).collectList().block();
        assertThat(salaryList).hasSize(1);
        assertThat(salaryList).containsExactly(salaries);

        Tuple2<Employee, List<Salaries>> employeeSalaries = Mono.zip(employeeRepository.findById(employee.empNo()),
                        salariesRepository.findAllByEmpNo(employee.empNo()).collectList())
                .doOnNext(tuple -> logger.info("Employee: {}, Salaries: {}", tuple.getT1(), tuple.getT2()))
                .block();
        assertThat(employeeSalaries).isNotNull();
        assertThat(employeeSalaries.getT1()).isEqualTo(employee);
        assertThat(employeeSalaries.getT2()).hasSize(1);
        assertThat(employeeSalaries.getT2()).containsExactly(salaries);


        Flux<Salaries> sage = databaseClient.sql("SELECT * FROM salaries INNER JOIN employee ON employee.emp_no = salaries.emp_no")
                .map((row, rowMetadata) ->
                        new Salaries(
                                dbType.contains("h2") ? Long.valueOf(row.get("emp_no", Integer.class)) : row.get("emp_no", Long.class) , // H2 not support Long, for the real mysql use Long.Class
                                row.get("salary", Integer.class),
                                row.get("from_date", LocalDate.class),
                                row.get("to_date", LocalDate.class))
                )
                .all();

        List<Salaries> salariesList = sage.collectList().block();
        assertThat(salariesList).hasSize(1);
        assertThat(salariesList).containsExactly(salaries);

        List<Salaries> salariesList1 = employeeRepository.findById(employee.empNo())
                .flatMap(employee1 -> salariesRepository.findAllByEmpNo(employee1.empNo()).collectList())
                .block();
        assertThat(salariesList1).hasSize(1);
        assertThat(salariesList1).containsExactly(salaries);

        List<Salaries> salariesList1 = employeeRepository.findById(employee.empNo())
                .flatMap(employee1 -> salariesRepository.findAllByEmpNo(employee1.empNo()).collectList())
                .block();
        assertThat(salariesList1).hasSize(1);
        assertThat(salariesList1).containsExactly(salaries);
    }

    @Test
    void delayElementsTest() {
        Flux.range(1, 100)
                .index()
                .delayElements(Duration.ofMillis(100))
                .buffer(10)
                .doOnNext(value -> logger.info("value: {}", value))
                .blockLast();
    }

    @Test
    void removeEmployeeWithSalaryTest() {
        Employee employee = createEmployee(
                new Employee(
                        "E",
                        "R",
                        Gender.MALE,
                        LocalDate.now().minusYears(22),
                        LocalDate.now()
                ))
                .block();
        assertThat(employee).isNotNull();

        Salaries salaries = salariesRepository.save(new Salaries(employee.empNo(), 1000, LocalDate.now().minusYears(2), LocalDate.now())).block();
        assertThat(salaries).isNotNull();

        employeeRepository.deleteById(employee.empNo()).block();

        assertThat(employeeRepository.findById(employee.empNo()).block()).isNull();
        assertThat(salariesRepository.findAllByEmpNo(employee.empNo()).collectList().block()).isEmpty();
    }

    @Test
    void removeSalaryTest() {
        Employee employee = createEmployee(
                new Employee(
                        "E",
                        "R",
                        Gender.MALE,
                        LocalDate.now().minusYears(22),
                        LocalDate.now()
                ))
                .block();
        assertThat(employee).isNotNull();

        Salaries salaries = salariesRepository.save(new Salaries(employee.empNo(), 1000, LocalDate.now().minusYears(2), LocalDate.now())).block();
        assertThat(salaries).isNotNull();

        salariesRepository.deleteAll().block();

        assertThat(employeeRepository.findById(employee.empNo()).block()).isNotNull();
        assertThat(salariesRepository.findAllByEmpNo(employee.empNo()).collectList().block()).isEmpty();
    }


}
