package com.example.reactive.services;

import com.example.reactive.dto.User;
import com.example.reactive.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<User> createUser(User user) {
        return userRepository
                .save(new User(null, user.username(), user.email(), passwordEncoder.encode(user.password())))
                .doOnNext(user1 -> logger.info("Created new user: {}", user1.username()));
    }

    @Override
    public Mono<User> findById(Long userId) {
        return userRepository
                .findById(userId)
                .doOnNext(user1 -> logger.info("get user: {}", user1.username()));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .doOnNext(user1 -> logger.info("get user: {}", user1.username()));
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .doOnNext(user1 -> logger.info("get user: {}", user1.username()));
    }

    @Override
    public Flux<User> findAll() {
        return userRepository
                .findAll()
                .doOnNext(user1 -> logger.info("get user: {}", user1.username()));
    }

}
