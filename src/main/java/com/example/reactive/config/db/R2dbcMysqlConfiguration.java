package com.example.reactive.config.db;


//import io.r2dbc.h2.H2ConnectionFactory;
import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import io.r2dbc.spi.ConnectionFactory;

import java.time.Duration;
import java.time.ZoneId;

@Configuration
//@ConditionalOnProperty(prefix = "spring.r2dbc.username", value = "root")
@Conditional(MysqlConditional.class)
public class R2dbcMysqlConfiguration extends AbstractR2dbcConfiguration {

    /**
     * https://github.com/r2dbc/r2dbc-h2
     * https://dev.mysql.com/downloads/file/?id=526407
     */


    private final R2dbcProperties r2dbcProperties;

    public R2dbcMysqlConfiguration(R2dbcProperties r2dbcProperties) {
        this.r2dbcProperties = r2dbcProperties;
    }

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        return MySqlConnectionFactory.from(
                MySqlConnectionConfiguration.builder()
                        .host("127.0.0.1")
                        .port(3306)
                        .username(r2dbcProperties.getUsername())
                        .password(r2dbcProperties.getPassword())
                        .database(r2dbcProperties.getName())
                        .serverZoneId(ZoneId.of("UTC")) //https://stackoverflow.com/questions/76020871/springboot-3-webflux-r2dbc-mysql-timezone-issue/78234333#78234333
                        .connectTimeout(Duration.ofSeconds(3))
                        .createDatabaseIfNotExist(true)
                        .useServerPrepareStatement()
                        .build()
        );
//        ConnectionFactoryOptions connectionFactoryOptions = builder()
//                .option(DRIVER, "h2")
//                .option(PROTOCOL, "mem")
//                .option(USER, "root")
//                .option(PASSWORD, "administrator")
//                .option(DATABASE, "r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1")//r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS testdb")
//                .build();
//        return ConnectionFactories.get(connectionFactoryOptions);
    }

}