package com.example.reactive.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ExceptionHandlerController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<String> serverExceptionHandler(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return Mono.just(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<String> unauthorizedExceptionHandler(BadCredentialsException ex) {
        logger.error(ex.getMessage(), ex);
        return Mono.just(ex.getMessage());
    }


    //test error
    @GetMapping(value = "/error")
    public Mono<String> exceptionReturn() {
        return Mono.error(new RuntimeException("test error"));
    }
}
