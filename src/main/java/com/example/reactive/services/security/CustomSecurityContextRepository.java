//package com.example.reactive.services.security;
//
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.server.context.ServerSecurityContextRepository;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//public class CustomSecurityContextRepository implements ServerSecurityContextRepository {
//
//    @Override
//    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
//        // לא נדרש לשמור שום דבר כאן, כי ברוב המקרים ה-SecurityContext נשמר אוטומטית על ידי SecurityContextHolder
//        return Mono.empty();
//    }
//
//    @Override
//    public Mono<SecurityContext> load(ServerWebExchange exchange) {
//        // מחזירים את ה-SecurityContext הקיים
//        return Mono.justOrEmpty(SecurityContextHolder.getContext());
//    }
//}
//
