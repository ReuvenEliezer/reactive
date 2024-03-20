package com.example.reactive.controllers;

import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import com.example.reactive.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
public class EmployeeController {
    private final EmployeeService employeeService;

    private final EmployeeRepository employeeRepository;


    public EmployeeController(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
    }

    @MessageMapping("create-employee")
    public Mono<Employee> createEmployee(@Payload Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @MessageMapping("find-by-id/{id}")
    public Mono<Employee> createEmployee(@DestinationVariable("id") Long id) {
        return employeeRepository.findById(id);
    }

    @MessageMapping("find-all")
    public Flux<Employee> findAll() {
        return employeeRepository.findAll();
    }
}
