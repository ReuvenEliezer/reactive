package com.example.reactive.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "employee")
public record Employee(@Id
                       @Column(value = "id")
                       Long id,
                       @Column("first_name")
                       String firstName,
                       @Column("last_name")
                       String lastName
) {
}
