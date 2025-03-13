package com.example.reactive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.session.InMemoryReactiveSessionRegistry;
import org.springframework.security.core.session.ReactiveSessionRegistry;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)

//// @Profile("!test") // Uncomment if you want to exclude it from the "test" profile
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http
//            , ServerSecurityContextRepository serverSecurityContextRepository
            , ReactiveAuthenticationManager authenticationManager,
// , CustomUserDetailsService customUserDetailsService,
//            ServerAuthenticationConverter converter,
//            ReactiveSessionRegistry reactiveSessionRegistry,
            WebFilter jwtAuthWebFilter
    ) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(requests -> requests
                        .pathMatchers(
                                "/v3/api-docs/**",
                                "/webjars/swagger-ui/**",
                                "/api/v1/auth/**"
                        )
                        .permitAll()
                        .anyExchange()
                        .authenticated()
                )
//                .securityContextRepository(serverSecurityContextRepository) // שמירת ה-Authentication
                .authenticationManager(authenticationManager)
//                .sessionManagement(session -> session
//                        .concurrentSessions(Customizer.withDefaults())
//                )
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
                        .frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable)) // Enable access to H2 console (for example)
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec.authenticationEntryPoint(customAuthenticationEntryPoint())) // Custom authentication entry point
                .addFilterBefore(jwtAuthWebFilter, SecurityWebFiltersOrder.AUTHENTICATION) // Add any custom filters before the filter chain if needed
//                .addFilterAt(jwtAuthWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public ServerAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (exchange, exception) -> {
            // Respond with a JSON object and status code 401
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap("{\"error\":\"Unauthorized\"}".getBytes()))
            );
            return Mono.empty();
        };
    }

//    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//        String password = passwordEncoder().encode("user"); // הצפנת הסיסמה
//        UserDetails user = User
//                .withUsername("user")
//                .password(password)
//                .roles("ADMIN")
//                .build();
//        return new MapReactiveUserDetailsService(user);
//    }

//    @Bean
//    public ReactiveAuthenticationManager authenticationManager(MapReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
//        manager.setPasswordEncoder(passwordEncoder);
//        manager.setUserDetailsPasswordService(userDetailsService);
//        return manager;
//    }

//    @Bean
//    public ReactiveSessionRegistry reactiveSessionRegistry() {
//        return new InMemoryReactiveSessionRegistry();
//    }


}
