package com.example.reactive.services.security;

import com.example.reactive.dto.login.RefreshToken;
import com.example.reactive.repositories.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Duration expirationTimeDuration = Duration.ofMinutes(10);
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<RefreshToken> createRefreshToken(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid JWT token for user " + username)))
                .map(user -> new RefreshToken(
                        user.id(),
                        UUID.randomUUID().toString(),
                        LocalDateTime.now().plus(expirationTimeDuration), //set expiry of refresh token to 10 minutes - you can configure it application.properties file
                        user.username()))
                .doOnNext(refreshToken -> refreshTokenRepository.save(refreshToken.token(), refreshToken));
    }


    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.get(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.expiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.remove(refreshToken.token());
            throw new ExpiredJwtException(null, null, refreshToken.token() + " Refresh token is expired. Please make a new login..!");
        }
        return refreshToken;
    }

}
