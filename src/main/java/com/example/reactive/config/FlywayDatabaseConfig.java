package com.example.reactive.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FlywayProperties.class)
public class FlywayDatabaseConfig {
    @Bean(initMethod = "migrate")
    public Flyway flyway(FlywayProperties flywayProperties) {
        return Flyway.configure()
                .dataSource(
                        flywayProperties.getUrl(),
                        flywayProperties.getUser(),
                        flywayProperties.getPassword()
                )
//                .initSql("CREATE SCHEMA IF NOT EXISTS " + flywayProperties.getDefaultSchema())
//                .createSchemas(true)
//                .schemas(flywayProperties.getDefaultSchema())
                .locations(flywayProperties.getLocations().toArray(String[]::new))
                .baselineOnMigrate(true)
                .load();
    }

//    @Bean
//    public FlywayProperties flywayProperties(){
//        return new FlywayProperties();
//    }
}