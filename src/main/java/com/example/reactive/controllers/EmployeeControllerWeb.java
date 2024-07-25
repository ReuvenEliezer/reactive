package com.example.reactive.controllers;

import com.example.reactive.dto.Employee;
import com.example.reactive.services.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
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
    private static final Logger logger = LoggerFactory.getLogger(EmployeeControllerWeb.class);
    private final RSocketRequester rSocketRequester;
    private final EmployeeService employeeService;

    public EmployeeControllerWeb(RSocketRequester rSocketRequester, EmployeeService employeeService) {
        this.rSocketRequester = rSocketRequester;
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    public Mono<Employee> createEmployee(@Valid @RequestBody Employee employee) {
//        return employeeService.createEmployee(employee);
        return rSocketRequester.route("create-employee")
                .data(employee)
                .retrieveMono(Employee.class)
                .doOnNext(entity -> logger.info("Creating new employee: {}", entity));

    }

    @PostMapping("/create-many")
    public Flux<Employee> createEmployee(@RequestBody Flux<Employee> employees) {
        return employeeService.createEmployees(employees);
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

    @GetMapping("find-all-by-r2dbc-entity-template")
    public Flux<Employee> findAllByEntityTemplate() {
        return rSocketRequester.route("find-all-by-r2dbc-entity-template")
                .retrieveFlux(Employee.class);
    }

    @GetMapping("find-by-last-name-by-r2dbc-entity-template/{last-name}")
    public Flux<Employee> findByLastNameByEntityTemplate(@PathVariable("last-name") String lastName) {
        return rSocketRequester
                .route("find-by-last-name-by-r2dbc-entity-template/{last-name}", lastName)
                .retrieveFlux(Employee.class);
    }

}
