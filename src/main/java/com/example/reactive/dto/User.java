package com.example.reactive.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Table(name = "users")
public record User(

        @Column(value = "id")
        @Null
        Long id,

        @Column(value = "email")
        @NotNull
        String email,

        @Column(value = "username")
        @NotNull
        String username,

        @Column(value = "password")
        @NotNull
        String password
) {

    public User(String email, String username, String password) {
        this(null, email, username, password);
    }

}