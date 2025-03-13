package com.example.reactive.services.security;

import com.example.reactive.dto.login.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    void save(String refreshToken, RefreshToken refreshTokenData);

    void remove(String refreshToken);

    Optional<RefreshToken> get(String refreshToken);
}
