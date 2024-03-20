//package com.example.reactive.config;
//
//
////import io.r2dbc.h2.H2ConnectionFactory;
//import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
//import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
//import io.r2dbc.spi.Connection;
//import io.r2dbc.spi.ConnectionFactories;
//import org.reactivestreams.Publisher;
//import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
//import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
//import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
//import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
//
//import io.r2dbc.spi.ConnectionFactory;
//import io.r2dbc.spi.ConnectionFactoryOptions;
//import org.springframework.r2dbc.core.DatabaseClient;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//
//import static io.r2dbc.spi.ConnectionFactoryOptions.*;
//
//
//import static io.r2dbc.spi.ConnectionFactoryOptions.*;
//
//@Configuration
//public class R2dbcConfiguration extends AbstractR2dbcConfiguration {
//
//    /**
//     * https://github.com/r2dbc/r2dbc-h2
//     */
//
//    @Bean
//    @Override
//    public ConnectionFactory connectionFactory() {
//            return MySqlConnectionFactory.from(
//                MySqlConnectionConfiguration.builder()
//                        .host("127.0.0.1")
//                        .username("mysql")
//                        .port(3306)
//                        .password("mysql")
//                        .database("testdb")
//                        .connectTimeout(Duration.ofSeconds(3))
//                        .useServerPrepareStatement()
//                        .build()
//        );
////        ConnectionFactoryOptions connectionFactoryOptions = builder()
////                .option(DRIVER, "h2")
////                .option(PROTOCOL, "mem")
////                .option(USER, "mysql")
////                .option(PASSWORD, "mysql")
////                .option(DATABASE, "r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1")//r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS testdb")
////                .build();
////        return ConnectionFactories.get(connectionFactoryOptions);
//    }
//
//}