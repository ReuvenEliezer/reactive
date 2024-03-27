//package com.example.reactive.config;
//
//
////import io.r2dbc.h2.H2ConnectionFactory;
//import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
//import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
//import io.asyncer.r2dbc.mysql.MySqlConnectionFactoryProvider;
//import io.r2dbc.spi.Connection;
//import io.r2dbc.spi.ConnectionFactories;
//import org.reactivestreams.Publisher;
//import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
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
//import java.time.ZoneId;
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
//     * https://dev.mysql.com/downloads/file/?id=526407
//     */
//
//
//    @Bean
//    @Override
//    public ConnectionFactory connectionFactory() {
//            return MySqlConnectionFactory.from(
//                MySqlConnectionConfiguration.builder()
//                        .host("127.0.0.1")
//                        .port(3306)
//                        .username("root")
//                        .password("administrator")
//                        .database("testdb")
//                        .serverZoneId(ZoneId.of("UTC")) //https://stackoverflow.com/questions/76020871/springboot-3-webflux-r2dbc-mysql-timezone-issue/78234333#78234333
//                        .connectTimeout(Duration.ofSeconds(3))
//                        .createDatabaseIfNotExist(true)
//                        .useServerPrepareStatement()
//                        .build()
//        );
////        ConnectionFactoryOptions connectionFactoryOptions = builder()
////                .option(DRIVER, "h2")
////                .option(PROTOCOL, "mem")
////                .option(USER, "root")
////                .option(PASSWORD, "administrator")
////                .option(DATABASE, "r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1")//r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS testdb")
////                .build();
////        return ConnectionFactories.get(connectionFactoryOptions);
//    }
//
//}