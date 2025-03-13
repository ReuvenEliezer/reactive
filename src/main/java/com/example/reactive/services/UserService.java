package com.example.reactive.services;

import com.example.reactive.dto.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> createUser(User user);

    Mono<User> findById(Long userId);

    Mono<User> findByEmail(String email);

    Mono<User> findByUsername(String username);

    Flux<User> findAll();
}
