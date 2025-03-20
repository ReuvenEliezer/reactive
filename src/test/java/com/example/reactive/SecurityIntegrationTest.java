package com.example.reactive;

import com.example.reactive.dto.Employee;
import com.example.reactive.dto.User;
import com.example.reactive.dto.login.LoginRequest;
import com.example.reactive.dto.login.TokenResponse;
import com.example.reactive.repositories.EmployeeRepository;
import com.example.reactive.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
class SecurityIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(SecurityIntegrationTest.class);
    private static final int MAX_RETRY = 3;
    private static final Duration FIXED_DELAY_ON_RETRY = Duration.ofSeconds(1);
    private static final String AUTH_PREFIX = "/api/v1/auth/";

    @Value("${server.port}")
    private int appPort;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebClient webClient;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll().block();
        employeeRepository.deleteAll().block();
    }

    @Test
    void createNewEmployeeTest() throws JsonProcessingException {
        User user = new User("email@gmail.com", "Eliezer-Reuven", "password");
        ResponseEntity<String> signupResponse = signup(user).block();
        assertThat(signupResponse).isNotNull();
        assertThat(signupResponse.getStatusCode().is2xxSuccessful()).isTrue();

        ResponseEntity<String> loginResponse = login(new LoginRequest(user.username(), user.password())).block();
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getStatusCode().is2xxSuccessful()).isTrue();
        String responseBody = loginResponse.getBody();
        assertThat(responseBody).isNotNull();

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String token = jsonNode.get("token").asText();
        String refreshToken = jsonNode.get("refreshToken").asText();
        TokenResponse tokenResponse = new TokenResponse(token, refreshToken);
        assertThat(tokenResponse).isNotNull();

        Employee employee = createEmployee(new Employee("E", "R"), tokenResponse.token()).block();
        assertThat(employee).isNotNull();
        assertThat(employee.empNo()).isNotNull();
    }

    private Mono<ResponseEntity<String>> signup(User user) {
        return webClient.post()
                .uri("http://localhost:" + appPort + AUTH_PREFIX + "signup")
                .bodyValue(user)
                .retrieve()
                .toEntity(String.class)
                .retryWhen(Retry.fixedDelay(MAX_RETRY, FIXED_DELAY_ON_RETRY))
                .doOnNext(user1 -> logger.info("Created new user: {}", user1))
                .onErrorResume(e -> {
                    logger.error("Failed to create user", e);
                    return Mono.empty();
                });
    }

    private Mono<ResponseEntity<String>> login(LoginRequest loginRequest) {
        return webClient.post()
                .uri("http://localhost:" + appPort + AUTH_PREFIX + "login")
                .bodyValue(loginRequest)
                .retrieve()
                .toEntity(String.class)
                .retryWhen(Retry.fixedDelay(MAX_RETRY, FIXED_DELAY_ON_RETRY))
                .doOnNext(res -> logger.info("login successfully for user: {}", loginRequest.username()))
                .onErrorResume(e -> {
                    logger.error("Failed to login with username '{}'", loginRequest.username(), e);
                    return Mono.empty();
                });
    }

    private Mono<Employee> createEmployee(Employee employee, String token) {
        return webClient.post()
                .uri("http://localhost:" + appPort + "/employee/create")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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

}
