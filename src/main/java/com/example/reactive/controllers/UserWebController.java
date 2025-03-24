package com.example.reactive.controllers;

import com.example.reactive.dto.User;
import com.example.reactive.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/user")
@Profile("!test")
public class UserWebController {

    private static final Logger logger = LoggerFactory.getLogger(UserWebController.class);
    private final UserService userService;

    public UserWebController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public Mono<User> createUser(@Valid @RequestBody User user) {
        return userService.createUser(user)
                .doOnNext(user1 -> logger.info("Creating new user: {}", user1.username()));
    }

    @GetMapping("/find-all")
    public Flux<User> createUser() {
        return userService.findAll()
                .doOnNext(user -> logger.info("find user: {}", user.username()));
    }

    @GetMapping("/find-by-username/{username}")
    public Mono<User> findByUsername(@PathVariable("username") String username) {
        return userService.findByUsername(username)
                .doOnNext(user -> logger.info("find user: {}", user.username()));
    }

    @GetMapping("/find-by-id/{id}")
    public Mono<User> findById(@PathVariable("id") Long id) {
        return userService.findById(id)
                .doOnNext(user -> logger.info("find user: {}", user.username()));
    }

    @GetMapping("/find-by-email/{email}")
    public Mono<User> findByEmail(@PathVariable("email") String email) {
        return userService.findByEmail(email)
                .doOnNext(user -> logger.info("find user: {}", user.username()));
    }

}
