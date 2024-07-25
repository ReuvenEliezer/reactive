package com.example.reactive;

import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import com.example.reactive.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.r2dbc.BadSqlGrammarException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionContextManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ReactiveApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TransactionTest {

    private static final Logger logger = LoggerFactory.getLogger(TransactionTest.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TransactionalOperator transactionalOperator;

    @Autowired
    private ReactiveTransactionManager reactiveTransactionManager;


    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll().block();
        assertThat(employeeRepository.findAll().collectList().block()).isEmpty();
    }

    @Test
    void employeeNotNullFirstAndLastNameTest() {
        assertThatThrownBy(() -> employeeRepository.save(new Employee(null, null)).block())
                .isInstanceOfAny(BadSqlGrammarException.class, DataAccessResourceFailureException.class);
    }

    @Test
    void invalidEmployeeWithoutTransactionalTest() {
        Employee invalidEmployee = new Employee(null, null); // invalid employee
        List<Employee> employees = List.of(new Employee("E1", "R1"), invalidEmployee);
        Flux<Employee> employeeFlux = employeeRepository
                .saveAll(employees)
                .onErrorResume(e -> Mono.empty());
        assertThat(employeeFlux.collectList().block()).hasSize(1);
        assertThat(employeeRepository.findAll().collectList().block()).hasSize(1);
    }

    @Test
    void invalidEmployeeWithTransactionalTest() {
        Employee invalidEmployee = new Employee(null, null); // invalid employee
        List<Employee> employees = List.of(new Employee("E1", "R1"), invalidEmployee);
        Flux<Employee> employeeFlux = doInTransaction(employees);
        List<Employee> employee = employeeFlux.collectList().block();
//        assertThat(employeeFlux.collectList().block()).isEmpty();
        assertThat(employeeRepository.findAll().collectList().block()).isEmpty();
    }

    private Flux<Employee> doInTransaction(List<Employee> employees) {
        return transactionalOperator.execute(new TransactionCallback<Employee>() {
            @Override
            public Publisher<Employee> doInTransaction(ReactiveTransaction status) {
                return employeeRepository.saveAll(employees)
                        .doOnError(e -> status.setRollbackOnly())
                        .onErrorResume(e -> Mono.empty());
//                try {
//                    Mono<Employee> employee1 = employeeRepository.save(new Employee("E1", "R1"));
//                    return employee1.then(employeeRepository.save(invalidEmployee));
//                    Mono<Employee> nonValidEmployee = employee.then(employeeRepository.save(new Employee(null, null)));
//                    return Flux.concat(employee, nonValidEmployee);
//                } catch (Exception ex) {
//                    status.setRollbackOnly();
//                    return Flux.empty();
//                }
            }
        }).doOnComplete(() -> logger.info("Complete to create employees"));
    }

    @Test
    void transactionalSuccessTest() {
        List<Employee> employees = List.of(new Employee("E1", "R1"), new Employee("E2", "R2"));
        Flux<Employee> employeeFlux = doInTransaction(employees);
        assertThat(employeeFlux.collectList().block()).hasSize(2);
        assertThat(employeeRepository.findAll().collectList().block()).hasSize(2);
    }

    @Test
    void invalidEmployeeWithTransactionalOperatorTest() {
        Employee invalidEmployee = new Employee(null, null); // invalid employee
        List<Employee> employees = List.of(new Employee("E1", "R1"), invalidEmployee);
        Flux<Employee> employeeFlux = Flux.fromIterable(employees);
        employeeService.createEmployees(employeeFlux)
                .collectList()
                .as(StepVerifier::create)
                .consumeNextWith(employeesList -> assertThat(employeesList).hasSize(1))
                .verifyComplete();
        employeeRepository.findAll()
                .collectList()
                .as(StepVerifier::create)
                .consumeNextWith(employeesList -> assertThat(employeesList).isEmpty())
                .verifyComplete();
    }

    @Test
    void validEmployeeWithTransactionalOperatorTest() throws SQLException {
        /**
         * https://docs.spring.io/spring-framework/reference/data-access/transaction/programmatic.html#transaction-programmatic-rtm
         */
        List<Employee> employees = List.of(new Employee("E1", "R1"), new Employee("E2", "R2"));
        Flux<Employee> employeeFlux1 = Flux.fromIterable(employees);
        employeeService.createEmployees(employeeFlux1)
                .collectList()
                .as(StepVerifier::create)
                .consumeNextWith(employeesList -> assertThat(employeesList).hasSize(employees.size()))
                .verifyComplete();
        employeeRepository.findAll()
                .collectList()
                .as(StepVerifier::create)
                .consumeNextWith(employeesList -> assertThat(employeesList).hasSize(employees.size()))
                .verifyComplete();

//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setName("SomeTxName");
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        Mono<ReactiveTransaction> reactiveTx = reactiveTransactionManager.getReactiveTransaction(def);
//        Mono<Void> transaction = reactiveTx.flatMap(status -> {
//
//            Flux<Employee> tx = employeeRepository.saveAll(employees);
//
//            return tx.then(reactiveTransactionManager.commit(status))
//                    .onErrorResume(ex -> reactiveTransactionManager.rollback(status).then(Mono.error(ex)));
//        });
////        Mono<Void> transaction = reactiveTransactionManager.getReactiveTransaction(def)
////                .flatMap(status -> {
////                    return employeeRepository.saveAll(employees)
////                            .then(reactiveTransactionManager.commit(status))
////                            .onErrorResume(ex -> reactiveTransactionManager.rollback(status).then(Mono.error(ex)));
////                });

    }


}
