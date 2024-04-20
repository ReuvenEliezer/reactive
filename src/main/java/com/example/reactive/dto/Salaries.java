package com.example.reactive.dto;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "salaries")
public record Salaries(

        @Column(value = "emp_no")
        Long empNo,

        @Column(value = "salary")
        Integer salary,

        @Column(value = "from_date")
        LocalDate fromDate,

        @Column(value = "to_date")
        LocalDate toDate
) {
}
