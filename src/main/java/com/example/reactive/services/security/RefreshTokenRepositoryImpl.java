package com.example.reactive.services.security;

import com.example.reactive.dto.login.RefreshToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private static final Map<String, RefreshToken> refreshTokenToUserDataMap = new ConcurrentHashMap<>();

    @Override
    public void save(String refreshToken, RefreshToken refreshTokenData) {
        refreshTokenToUserDataMap.put(refreshToken, refreshTokenData);
    }

    @Override
    public void remove(String refreshToken) {
        refreshTokenToUserDataMap.remove(refreshToken);
    }

    @Override
    public Optional<RefreshToken> get(String refreshToken) {
        return Optional.of(refreshTokenToUserDataMap.get(refreshToken));
    }

}
