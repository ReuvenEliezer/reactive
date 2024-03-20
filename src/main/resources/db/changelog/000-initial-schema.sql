-- liquibase formatted sql
-- changeset liquibase:1111111-1::initial-schema
CREATE TABLE IF NOT EXISTS employee (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255)
);