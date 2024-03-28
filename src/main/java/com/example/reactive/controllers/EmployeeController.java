package com.example.reactive.controllers;

import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import com.example.reactive.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
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
    private final R2dbcEntityTemplate r2dbcentitytemplate;


    public EmployeeController(EmployeeService employeeService,
                              EmployeeRepository employeeRepository,
                              R2dbcEntityTemplate r2dbcentitytemplate) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.r2dbcentitytemplate = r2dbcentitytemplate;
    }

    @MessageMapping("create-employee")
    public Mono<Employee> createEmployee(@Payload Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @MessageMapping("find-by-id/{id}")
    public Mono<Employee> findById(@DestinationVariable("id") Long id) {
        return employeeRepository.findById(id);
    }

    @MessageMapping("find-all")
    public Flux<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @MessageMapping("find-all-by-r2dbc-entity-template")
    public Flux<Employee> findAllByEntityTemplate() {
        return r2dbcentitytemplate.select(Employee.class).all();
    }

    @MessageMapping("find-by-last-name/{last-name}")
    public Flux<Employee> findByLastName(@DestinationVariable("last-name") String lastName) {
        return employeeRepository.findByLastName(lastName);
    }

    @MessageMapping("find-by-last-name-by-r2dbc-entity-template/{last-name}")
    public Flux<Employee> findByLastNameByEntityTemplate(@DestinationVariable("last-name") String lastName) {
        return r2dbcentitytemplate.select(
                Query.query(
                        Criteria.where("last_name").is(lastName)
                ),
                Employee.class);
    }

}
