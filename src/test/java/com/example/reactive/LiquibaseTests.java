package com.example.reactive;

import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.*;

@DataR2dbcTest
@Disabled
class LiquibaseTests {

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
                .thenMany(employeeRepository.save(new Employee("Eliezer", "Reuven")))
                .thenMany(employeeRepository.findAll());

        StepVerifier.create(actual).expectNextMatches(result -> result.firstName().equals("Eliezer")).verifyComplete();
    }

}
