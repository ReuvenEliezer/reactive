# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.3/maven-plugin/reference/html/#build-image)
* [Spring Data R2DBC](https://docs.spring.io/spring-boot/docs/3.2.3/reference/htmlsingle/index.html#data.sql.r2dbc)
* [RSocket](https://rsocket.io/)
* [Spring Shell](https://spring.io/projects/spring-shell)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.2.3/reference/htmlsingle/index.html#web.reactive)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing data with R2DBC](https://spring.io/guides/gs/accessing-data-r2dbc/)
* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)

### Additional Links

These additional references should also help you:

* [R2DBC Homepage](https://r2dbc.io)

## Missing R2DBC Driver

Make sure to include a [R2DBC Driver](https://r2dbc.io/drivers/) to connect to your database.

# Documentation

## Swagger
- http://localhost:8080/webjars/swagger-ui/index.html 


mysql :
Install mysql-server for developer from here: [https://dev.mysql.com/downloads/file/?id=526407][1] and configure it according to your properties (username & password)

DB-type | r2dbc-url | liquibase/flyway-migrator url | 
--- | - | -- |
h2 (in-memory) | r2dbc:h2:mem:///${spring.r2dbc.name}?options=DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false | jdbc:h2:mem:${spring.r2dbc.name};DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS ${spring.r2dbc.name} |
Mysql | r2dbc:mysql://${spring.r2dbc.username}:${spring.r2dbc.password}@127.0.0.1:3306/${spring.r2dbc.name}?allowPublicKeyRetrieval=true&useSSL=false&autoReconnect=true&createDatabaseIfNotExist=true&serverTimezone=UTC | jdbc:mysql://${spring.r2dbc.username}:${spring.r2dbc.password}@127.0.0.1:3306/${spring.r2dbc.name}?allowPublicKeyRetrieval=true&useSSL=false&autoReconnect=true&createDatabaseIfNotExist=true&serverTimezone=UTC |
