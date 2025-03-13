package com.example.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Hooks;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
@EnableR2dbcRepositories
@ComponentScan(basePackages = {
        "com.example.reactive.config",
        "com.example.reactive.controllers",
        "com.example.reactive.repositories",
        "com.example.reactive.services",
})
public class ReactiveApplication {

    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation(); //for reactive tracing log
        ReactorDebugAgent.init();
        SpringApplication.run(ReactiveApplication.class, args);
    }

}
