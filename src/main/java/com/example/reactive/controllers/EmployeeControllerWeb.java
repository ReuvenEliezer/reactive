package com.example.reactive.controllers;

import com.example.reactive.dto.Employee;
import com.example.reactive.services.EmployeeService;
import com.example.reactive.services.cli.ReactiveCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/employee")
public class EmployeeControllerWeb {
    private static final Logger logger = LoggerFactory.getLogger(ReactiveCommand.class);
    private final EmployeeService employeeService;
    private final RSocketRequester rSocketRequester;


    public EmployeeControllerWeb(EmployeeService employeeService, RSocketRequester rSocketRequester) {
        this.employeeService = employeeService;
        this.rSocketRequester = rSocketRequester;
    }

    @PostMapping("/create")
    public Mono<Employee> createEmployee(@RequestBody Employee employee) {
//        return employeeService.createEmployee(employee);
        return rSocketRequester.route("create-employee")
                .data(employee)
                .retrieveMono(Employee.class)
                .doOnNext(entity -> logger.info("Creating new employee: {}", entity));

    }

    @GetMapping("/find-by-id/{id}")
    public Mono<Employee> findById(@PathVariable("id") Long id) {
        return rSocketRequester.route("find-by-id/{id}", id)
                .retrieveMono(Employee.class)
                .doOnNext(employee -> logger.info("find employee: {}", employee));

    }

    @GetMapping("/find-by-last-name/{last-name}")
    public Flux<Employee> findById(@PathVariable("last-name") String lastName) {
        return rSocketRequester.route("find-by-last-name/{last-name}", lastName)
                .retrieveFlux(Employee.class)
                .doOnNext(employee -> logger.info("find employees: {}", employee));

    }

    @GetMapping("/find-all")
    public Flux<Employee> findAll() {
        return rSocketRequester.route("find-all")
                .retrieveFlux(Employee.class)
                .doOnNext(employee -> logger.info("find employee: {}", employee));

    }
}
