//package com.example.reactive.dto;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.relational.core.mapping.Column;
//import org.springframework.data.relational.core.mapping.Table;
//
//@Table(name = "department")
//public record Department(
//        @Id
//        @NotNull
//        @Column(value = "dept_no")
//        String deptNo,
//
//        @NotNull
//        @NotBlank(message = "'Department Name' is mandatory")
//        @Column("dept_name")
//        String deptName
//) {
//}
