package com.example.reactive.services.cli;

import com.example.reactive.dto.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@ShellComponent
public class ReactiveCommand {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveCommand.class);
    private static final int MAX_RETRY = 3;
    private static final Duration FIXED_DELAY_ON_RETRY = Duration.ofSeconds(1);
    private final WebClient webClient;
    private final RSocketRequester rSocketRequester;
    private final int appPort;


    @Autowired
    public ReactiveCommand(WebClient webClient, RSocketRequester rSocketRequester, @Value("${server.port}") int appPort) {
        this.webClient = webClient;
        this.rSocketRequester = rSocketRequester;
        this.appPort = appPort;
    }

    @ShellMethod(key = "create-new-employee", value = "Create new employee")
    Employee createNewEmployee(@ShellOption(value = "-f", help = "example: \"Eliezer\"") String firstName,
                               @ShellOption(value = "-l", help = "example: \"Reuven\"") String lastName) {
        logger.info("Creating new employee with first name: {} and last name: {}", firstName, lastName);
//        return rSocketRequester.route("create-employee")
//                .metadata("create-employee"::equals)
//                .data(new Employee(null, firstName, lastName))
//                .retrieveMono(Employee.class)
//                .doOnNext(employee -> logger.info("Created new employee: {}", employee));
//        return rSocketRequester.route("create-employee")
//                .metadata("create-employee"::equals)
//                .data(new Employee(null, firstName, lastName))
//                .retrieveMono(Employee.class)
//                .block();
        return webClient.post()
                .uri("http://localhost:" + appPort + "/employee/create")
                .bodyValue(new Employee(null, firstName, lastName))
                .retrieve()
                .bodyToMono(Employee.class)
                .block();
//                .doOnNext(employee -> logger.info("Created new employee: {}", employee));
    }
}