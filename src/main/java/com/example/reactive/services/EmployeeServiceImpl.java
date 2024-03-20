package com.example.reactive.services;

import com.example.reactive.controllers.EmployeeController;
import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Mono<Employee> createEmployee(Employee employee) {
        return employeeRepository
                .save(employee)
                .doOnNext(employee1 -> logger.info("Created new employee: {}", employee1));
    }
}
