package com.example.reactive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "titles")
public record Titles(
        @Id
        @NotNull
        @Column(value = "emp_no")
        String empNo,

        @NotNull
        @NotBlank(message = "'title' is mandatory")
        @Column("title")
        String title,

        @NotNull
        @Column(value = "from_date")
        LocalDate fromDate,

        @NotNull
        @Column(value = "to_date")
        LocalDate toDate
) {
}
