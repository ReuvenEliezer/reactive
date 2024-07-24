package com.example.reactive;

import com.example.reactive.dto.Employee;
import com.example.reactive.repositories.EmployeeRepository;
import com.example.reactive.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.BadSqlGrammarException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Mono;

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
    }

    @Test
    void employeeNotNullFirstAndLastNameTest() {
        assertThatThrownBy(() -> employeeRepository.save(new Employee(null, null)).block())
                .isInstanceOf(BadSqlGrammarException.class);
    }

    @Test
    void transactionalFailureTest() {
        transactionalOperator.execute(new TransactionCallback<Employee>() {
                    @Override
                    public Mono<Employee> doInTransaction(ReactiveTransaction status) {
                        try {
                            Mono<Employee> employee = employeeService.createEmployee(new Employee("E", "R"));
                            return employee.then(employeeService.createEmployee(new Employee(null, null)));
                        } catch (Exception ex) {
                            status.setRollbackOnly();
                            return Mono.empty();
                        }
                    }
                }).doOnComplete(() -> logger.info("Complete to create employees"))
                .collectList()
                .block();
        assertThat(employeeRepository.findAll().collectList().block()).isEmpty();
    }

    @Test
    void transactionalSuccessTest() {
        transactionalOperator.execute(new TransactionCallback<Employee>() {
                    @Override
                    public Mono<Employee> doInTransaction(ReactiveTransaction status) {
                        try {
                            Mono<Employee> employee = employeeService.createEmployee(new Employee("E", "R"));
                            return employee.then(employeeService.createEmployee(new Employee("EE", "R1")));
                        } catch (Exception ex) {
                            status.setRollbackOnly();
                            return Mono.empty();
                        }
                    }
                }).doOnComplete(() -> logger.info("Complete to create employees"))
                .collectList()
                .block();
        assertThat(employeeRepository.findAll().collectList().block()).hasSize(2);
    }


    @Test
    @Disabled
    void transactionalTest2() throws SQLException {
        /**
         * https://docs.spring.io/spring-framework/reference/data-access/transaction/programmatic.html#transaction-programmatic-rtm
         */
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// explicitly setting the transaction name is something that can be done only programmatically
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        Mono<ReactiveTransaction> reactiveTransaction = reactiveTransactionManager
                .getReactiveTransaction(def);
        Mono<Void> voidMono = reactiveTransaction.flatMap(status -> {

            Mono<Employee> tx = employeeService.createEmployee(new Employee("E", "R"));
//            employeeService.createEmployee(new Employee(null, null));
//            employeeService.createEmployee(new Employee("E", "R1"));

            return tx.then(reactiveTransactionManager.commit(status))
                    .onErrorResume(ex -> reactiveTransactionManager.rollback(status).then(Mono.error(ex)));
        });//.block()

        Void block = voidMono.block();
        List<Employee> block1 = employeeRepository.findAll().collectList().block();

    }


}
