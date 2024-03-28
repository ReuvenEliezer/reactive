package com.example.reactive.config.db;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


public class MysqlConditional implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MysqlConnProp r2dbcProperties = Binder.get(context.getEnvironment())
                .bind("spring.r2dbc", MysqlConnProp.class)
                .orElse(null);
        return r2dbcProperties != null && r2dbcProperties.url().contains("mysql");
    }


}
