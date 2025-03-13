package com.example.reactive.services.security;

import com.example.reactive.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
public class JWTAuthenticationManager implements ReactiveAuthenticationManager {

    private static final Logger logger = LogManager.getLogger(JWTAuthenticationManager.class);
    private final UserService userService;

    public JWTAuthenticationManager(UserService userService) {
        this.userService = userService;
    }

//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
//        String token = authentication.getCredentials().toString();
//        String username = JWTUtil.extractUsername(token);
//
//        return userService.findByUsername(username)
//                .switchIfEmpty(Mono.error(new AuthenticationException("User not found") {
//                }))
//                .doOnNext(logger::info)
//                .doOnNext(userDetails -> {
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                })
//                .handle((userDetails, sink) -> {
//                    if (JWTUtil.validateToken(token, userDetails.username())) {
//                        sink.next(authentication);
//                    } else {
//                        sink.error(new AuthenticationException("Invalid JWT token") {
//                        });
//                    }
//                });
//    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();
        // חילוץ שם משתמש מהטוקן
        String username;
        try {
            username = JWTUtil.extractUsername(token);
        } catch (Exception e) {
            logger.error("Failed to extract username from token: {}", e.getMessage());
            return Mono.error(new AuthenticationException("Invalid JWT token") {
            });
        }

        return userService.findByUsername(username)
                .switchIfEmpty(Mono.error(new AuthenticationException("User not found") {
                }))
                .flatMap(userDetails -> {
                    // אימות הטוקן
                    if (JWTUtil.validateToken(token, userDetails.username())) {
                        logger.info("Token is valid for user: {}", username);

                        // יצירת Authentication עם הנתונים
                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                userDetails.username(),
                                token,
                                new ArrayList<>()  //userDetails.getAuthorities()
                        );

                        // שמירת הקונטקסט
                        ReactiveSecurityContextHolder.withAuthentication(auth);

                        return Mono.just(auth)
//                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
//                                .doOnNext(ReactiveSecurityContextHolder::withAuthentication)
                                ;
                    } else {
                        logger.warn("Invalid token for user: {}", username);
                        return Mono.error(new AuthenticationException("Invalid JWT token") {
                        });
                    }
                })
//                .filter(Authentication::isAuthenticated)
                ;
    }

//    public ServerAuthenticationConverter authenticationConverter() {
//        return exchange -> {
//            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7);
//                return Mono.just(SecurityContextHolder.getContext().getAuthentication());
//            }
//            return Mono.empty();
//        };
//    }
}