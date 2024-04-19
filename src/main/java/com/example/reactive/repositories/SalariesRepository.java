package com.example.reactive.repositories;

import com.example.reactive.dto.Salaries;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface SalariesRepository extends R2dbcRepository<Salaries, Long> {

    Flux<Salaries> findAllByEmpNo(Long id);

}
