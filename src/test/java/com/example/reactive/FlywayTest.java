package com.example.reactive;

import com.example.reactive.config.FlywayDatabaseConfig;
import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {FlywayDatabaseConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FlywayTest {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void databaseClientExistTest() {
        assertThat(databaseClient).isNotNull();
    }

    @Test
    void employeeRepositoryExistTest() {
        assertThat(employeeRepository).isNotNull();
    }

    @Test
    void employeeRepositoryQueryTest() {
        Flux<Employee> actual = employeeRepository
                .deleteAll()
                .thenMany(employeeRepository.save(new Employee(null, "Eliezer", "Reuven")))
                .thenMany(employeeRepository.findAll());

        StepVerifier.create(actual).expectNextMatches(result -> result.firstName().equals("Eliezer")).verifyComplete();
    }

}