package com.example.reactive;

import com.example.reactive.config.db.FlywayDatabaseConfig;
import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

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

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll().block();
    }

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
                .thenMany(employeeRepository.save(new Employee("Eliezer", "Reuven")))
                .thenMany(employeeRepository.findAll());

        StepVerifier.create(actual).expectNextMatches(result -> result.firstName().equals("Eliezer")).verifyComplete();
    }

    @Test
    void employeeRepositoryPageableQueryTest() {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            employees.add(new Employee("Eliezer", "Reuven"));
        }
        employeeRepository.saveAll(employees).blockLast();
        assertThat(employeeRepository.findAll().count().block()).isEqualTo(10);

        int totalElementForEachPage = 2;
        for (int page = 0; page < 5; page++) {
            Pageable pageable = Pageable.ofSize(totalElementForEachPage).withPage(page);
            List<Employee> employeesPage1 = employeeRepository.findByLastName("Reuven", pageable)
                    .collectList()
                    .block();
            assertThat(employeesPage1).hasSize(totalElementForEachPage);
        }

        Pageable pageable = Pageable.ofSize(totalElementForEachPage).withPage(5);
        assertThat(employeeRepository.findByLastName("Reuven", pageable)
                .collectList()
                .block()).isEmpty();
    }

}
