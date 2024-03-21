package com.example.reactive;

import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private WebClient webClient;
    @Autowired
    private RSocketRequester rSocketRequester;


    @BeforeEach
    void setUp() {
        logger.info("setUp");
        employeeRepository.deleteAll().block();
    }

    @Test
    void bufferQueryTest() {
        logger.info("start test");
        int employeeCount = 5;
        List<Employee> employees = Flux.range(0, employeeCount)
                .index()
                .flatMap(iteration ->
                        createEmployee(new Employee(null, "E " + iteration.getT2(), "R"))
                )
                .doOnComplete(() -> logger.info("Complete to create employees"))
                .collectList()
                .block();
        assertThat(employees).hasSize(employeeCount);
        Long totalSaved = employeeRepository.findAll().count().block();
        logger.info("end test");
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
        Employee employee = createEmployee(new Employee(null, "E", "R"))
                .block();
        assertThat(employee).isNotNull();
        assertThat(employee.id()).isNotNull();
    }

    @Test
    void createNewEmployeeTest1() {
        Employee employee = rSocketRequester.route("create-employee")
                .data(new Employee(null, "E", "R"))
                .retrieveMono(Employee.class)
                .doOnNext(employee1 -> logger.info("Created new employee: {}", employee1))
                .block();
        assertThat(employee).isNotNull();
        assertThat(employee.id()).isNotNull();
    }

}
