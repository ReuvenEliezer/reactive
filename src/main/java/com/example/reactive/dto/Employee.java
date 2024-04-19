package com.example.reactive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Date;

@Table(name = "employee")
public record Employee(@Id
                       @Null
                       @Column(value = "emp_no")
                       Long empNo,

                       @NotNull
                       @NotBlank(message = "'First Name' is mandatory")
                       @Column("first_name")
                       String firstName,

                       @NotNull
                       @Column("last_name")
                       String lastName,

                       @NotNull
                       @Column("gender")
                       Gender gender,

                       @NotNull
                       @Column("birth_date")
                       LocalDate birthDate,

                       @NotNull
                       @Column("hire_date")
                       LocalDate hireDate
) {
    public Employee(String firstName, String lastName) {
        this(null, firstName, lastName, null, null, null);
    }

    public Employee(String firstName, String lastName, Gender gender, LocalDate birthDate, LocalDate hireDate) {
        this(null, firstName, lastName, gender, birthDate, hireDate);
    }
}
