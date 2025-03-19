package com.example.reactive.services.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthWebFilter implements WebFilter {

    private static final Logger logger = LogManager.getLogger(JwtAuthWebFilter.class);

    private final ReactiveAuthenticationManager authenticationManager;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public JwtAuthWebFilter(ReactiveAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private boolean isTestProfile() {
        return activeProfile != null && activeProfile.equals("test");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (isTestProfile()) { //TODO fix this patch
            logger.info("Filter skipped because 'test' profile is active");
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        logger.info("Filter executed - Path: {}, ID: {}", request.getPath(), request.getId());

        // מסלולים שאינם דורשים אימות
        if (path.startsWith("/api/v1/auth/")
                || path.startsWith("/webjars/swagger-ui")
                || path.startsWith("/v3/api-docs")
        ) {
            return chain.filter(exchange);
        }


        // שליפת הטוקן מהבקשה
        String token;
        try {
            token = extractJwtFromRequest(exchange);
        } catch (Exception e) {
            logger.error("JWT token extraction failed: {}", e.getMessage());
            return Mono.error(new BadCredentialsException("Invalid JWT token"));
        }

        // אימות הטוקן באמצעות ReactiveAuthenticationManager
        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(null, token))
                .flatMap(auth -> {

                    logger.info("Token valid for username: '{}'", auth.getName());
                    return chain.filter(exchange)
                            .doOnSubscribe(subscription -> logger.info("Subscribed once - Path: {}", path))
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                            .doOnSuccess(aVoid -> logger.info("Authentication successful: {}", auth))
                            .doOnTerminate(() -> logger.info("Filter chain finished - Path: {}, ReqID: {}", path, request.getId()))
                            .doFinally(signal -> logger.info("Filter completed - Path: {}, Signal: {}", path, signal));
                })
                .onErrorResume(e -> {
                    logger.error("Authentication failed: {}", e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }


//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        String path = exchange.getRequest().getURI().getPath();
//        logger.info("path: {}", path);
//        if (path.startsWith("/api/v1/auth/signup") || path.startsWith("/api/v1/auth/login")
//                || path.startsWith("/webjars/swagger-ui") || path.startsWith("/v3/api-docs")) {
//            return chain.filter(exchange);
//        }
//        String token = extractJwtFromRequest(exchange);
//        String username = JWTUtil.extractUsername(token);
//        logger.info("Token valid for username: '{}'", username);
//        UsernamePasswordAuthenticationToken auth =
//                new UsernamePasswordAuthenticationToken(username, token, new ArrayList<>());
//        return chain.filter(exchange)
//                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
//    }

    private String extractJwtFromRequest(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid JWT token");
        }
        return token.substring(7);
    }

}
