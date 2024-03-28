package com.example.reactive.config.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties("spring.r2dbc")
@EnableConfigurationProperties(MysqlConnProp.class)
public record MysqlConnProp(@Value("url") String url) {
}

