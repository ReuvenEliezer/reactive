//package com.example.reactive.services.security;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//public class JwtServerAuthConverter implements ServerAuthenticationConverter {
//
//    private static final Logger logger = LogManager.getLogger(JwtServerAuthConverter.class);
//
//    private final ReactiveAuthenticationManager authenticationManager;
//
//    public JwtServerAuthConverter(ReactiveAuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }
//
//    @Override
//    public Mono<Authentication> convert(ServerWebExchange exchange) {
//        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//            String username = JWTUtil.extractUsername(token);
//            if (username != null && JWTUtil.validateToken(token, username)) {
////                return Mono.just(new UsernamePasswordAuthenticationToken(
////                        new User(username, "", List.of(new SimpleGrantedAuthority("USER"))),
////                        token,
////                        List.of(new SimpleGrantedAuthority("USER"))
////                ));
//                return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, token))
//                        .doOnNext(auth -> {
//                            logger.info("Authentication successful: {}", auth);
//                            SecurityContextHolder.getContext().setAuthentication(auth);
//                        })
//                        .doOnError(e -> logger.error("Authentication error: {}", e.getMessage(), e));
//            }
//        }
//
//        return Mono.empty();
//    }
//
//}
