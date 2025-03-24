package com.example.reactive.services.security;

import com.example.reactive.dto.login.RefreshToken;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    Mono<RefreshToken> createRefreshToken(String username);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken refreshToken);
}
