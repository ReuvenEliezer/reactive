package com.example.reactive.repositories;

import com.example.reactive.dto.Employee;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface EmployeeRepository extends R2dbcRepository<Employee, Long> {
    Flux<Employee> findByLastName(String lastName);
}
