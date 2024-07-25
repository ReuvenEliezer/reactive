package com.example.reactive.services;

import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final EmployeeRepository employeeRepository;

    private final TransactionalOperator transactionalOperator;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               TransactionalOperator transactionalOperator) {
        this.employeeRepository = employeeRepository;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Employee> createEmployee(Employee employee) {
        return employeeRepository
                .save(employee)
                .doOnNext(employee1 -> logger.info("Created new employee: {}", employee1));
    }

    @Override
    public Flux<Employee> createEmployees(Flux<Employee> employee) {
        return transactionalOperator.execute(status -> employeeRepository.saveAll(employee)
                .onErrorResume(throwable -> {
                    logger.error("Error: {}", throwable.getMessage());
                    status.setRollbackOnly();
                    return Flux.empty();
                }));
    }
}
