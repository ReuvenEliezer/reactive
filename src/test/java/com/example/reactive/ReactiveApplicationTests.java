package com.example.reactive;

import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

@DataR2dbcTest
class ReactiveApplicationTests {

    @Autowired
    private DatabaseClient client;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testDatabaseClientExisted() {
        assertThat(client).isNotNull();
        ;
    }

    @Test
    void testPostRepositoryExisted() {
        assertThat(employeeRepository).isNotNull();
    }

    @Test
    void testInsertAndQuery() {
        Flux<Employee> actual = employeeRepository
                .deleteAll()
                .thenMany(employeeRepository.save(new Employee(null, "Eliezer", "Reuven")))
                .thenMany(employeeRepository.findAll());

        StepVerifier.create(actual).expectNextMatches(result -> result.firstName().equals("Eliezer")).verifyComplete();
    }

}
