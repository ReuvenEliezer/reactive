package com.example.reactive.controllers;

import com.example.reactive.dto.User;
import com.example.reactive.dto.login.LoginRequest;
import com.example.reactive.dto.login.RefreshToken;
import com.example.reactive.dto.login.RefreshTokenRequest;
import com.example.reactive.dto.login.TokenResponse;
import com.example.reactive.services.UserService;
import com.example.reactive.services.security.JWTAuthenticationManager;
import com.example.reactive.services.security.JWTUtil;
import com.example.reactive.services.security.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    private final ReactiveAuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          ReactiveAuthenticationManager authenticationManager,
                          RefreshTokenService refreshTokenService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

//    @PostMapping("/login")
//    public Mono<ResponseEntity<TokenResponse>> login(@RequestBody LoginRequest loginRequest) {
//        return userService.findByUsername(loginRequest.username())
//                .switchIfEmpty(Mono.error(new AuthenticationException("User not found") {
//                }))
//                .doOnError(e -> logger.error("User '{}' not found", loginRequest.username()))
//                .flatMap(user -> {

    /// /                    String encodePass = passwordEncoder.encode(user.password());
//                    if (passwordEncoder.matches(loginRequest.password(), user.password())) {
//                        String token = JWTUtil.generateToken(user.username());
//                        logger.info("Authentication successful: username={}", user.username());
//                        Mono<TokenResponse> refreshToken = refreshTokenService
//                                .createRefreshToken(loginRequest.username())
//                                .map(refreshToken1 -> new TokenResponse(token, refreshToken1.token()));
//
//                        return refreshToken.map(ResponseEntity::ok);
//                    } else {
//                        logger.warn("Invalid password for username: {}", loginRequest.username());
//                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
//                    }
//                });


//        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), token))
//                .doOnNext(authentication -> logger.info("Authentication successful: principal={}, authorities={}",
//                        authentication.getPrincipal(),
//                        authentication.getAuthorities()))
//                .map(authentication -> ResponseEntity.ok(new LoginResponse(token, null))) //TODO
//                .onErrorResume(e -> {
//                    logger.error("Authentication failed: {} for user: '{}'", e.getMessage(), loginRequest.username(), e);
//                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
//                });
//    }
    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody LoginRequest loginRequest) {
        return userService.findByUsername(loginRequest.username())
                .switchIfEmpty(Mono.error(new AuthenticationException("User not found") {
                }))
                .flatMap(user -> {
                    // בדיקת סיסמה
                    if (!passwordEncoder.matches(loginRequest.password(), user.password())) {
                        logger.warn("Invalid password for username: {}", loginRequest.username());
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                    }
                    // יצירת JWT
                    String token = JWTUtil.generateToken(user.username());
                    logger.info("Generated JWT for user '{}': {}", user.username(), token);

                    // אימות באמצעות authenticationManager עם הטוקן
                    return authenticationManager.authenticate(
                                    new UsernamePasswordAuthenticationToken(
                                            user.username(),
                                            token,
                                            new ArrayList<>()))
                            .doOnNext(auth -> logger.info("Authentication successful for user: {}", auth.getPrincipal()))
                            .flatMap(auth -> {
                                // יצירת Refresh Token
                                return refreshTokenService.createRefreshToken(user.username())
                                        .map(refreshToken -> new TokenResponse(token, refreshToken.token()))
                                        .map(ResponseEntity::ok);
                            });
                })
                .onErrorResume(e -> {
                    logger.error("Authentication failed for user '{}': {}", loginRequest.username(), e.getMessage());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body("Authentication failed"));
                });
    }


//    @PostMapping("/login")
//    public Mono<ResponseEntity<TokenResponse>> login(@RequestBody LoginRequest loginRequest) {
//        return userService.findByUsername(loginRequest.username())
//                .switchIfEmpty(Mono.error(new AuthenticationException("User not found") {}))
//                .doOnError(e -> logger.error("User '{}' not found", loginRequest.username()))
//                .flatMap(user -> {
//                    // קריאה ל-authenticate
//                    return authenticationManager.authenticate(
//                                    new UsernamePasswordAuthenticationToken(user.username(), loginRequest.password()))
//                            .flatMap(authenticated -> {
//                                // יצירת ה-Access Token
//                                String token = JWTUtil.generateToken(user.username());
//                                logger.info("Authentication successful: username={}", user.username());
//
//                                // יצירת Refresh Token
//                                return refreshTokenService.createRefreshToken(user.username())
//                                        .map(refreshToken -> new TokenResponse(token, refreshToken.token()))
//                                        .map(ResponseEntity::ok);
//                            });
//                })
//                .onErrorResume(e -> {
//                    logger.error("Authentication failed for user '{}': {}", loginRequest.username(), e.getMessage());
//                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
//                });
//    }

//    @PostMapping("/login")
//    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest authRequest) {
//        return userService.findByUsername(authRequest.email())
//                .map(userDetails -> {
//                    if (userDetails.password().equals(authRequest.password())) {
//                        return ResponseEntity.ok(new LoginResponse(JWTUtil.generateToken(authRequest.email()), null));
//                    } else {
//                        throw new BadCredentialsException("Invalid username or password");
//                    }
//                }).switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username or password")));
//    }

    @PostMapping("/refresh-token")
    public Mono<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        Assert.notNull(refreshToken, "Refresh token is null");
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::username)
                .map(username -> refreshTokenService.createRefreshToken(username)
                        .map(refreshedToken -> {
                            String accessToken = JWTUtil.generateToken(username);
                            return new TokenResponse(accessToken, refreshedToken.token());
                        })).orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<String>> signup(@RequestBody User user) {
        return userService.createUser(user)
                .map(savedUser -> ResponseEntity.ok("User signed up successfully"));
    }
}
