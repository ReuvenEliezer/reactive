package com.example.reactive.services;

import com.example.reactive.dto.Employee;
import reactor.core.publisher.Mono;

public interface EmployeeService {

    Mono<Employee> createEmployee(Employee employee);

}
