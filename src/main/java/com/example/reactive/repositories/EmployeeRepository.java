package com.example.reactive.repositories;

import com.example.reactive.dto.Employee;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface EmployeeRepository extends R2dbcRepository<Employee, Long> {

    /**
     * https://docs.spring.io/spring-data/r2dbc/docs/current-SNAPSHOT/reference/html/#r2dbc.repositories.queries:~:text=12.2.-,Query%20Methods,-Most%20of%20the
     * @param lastName
     * @return
     */
    Flux<Employee> findByLastName(String lastName);
}
