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
https://docs.spring.io/spring-data/relational/reference/r2dbc/getting-started.html
mysql :
Install mysql-server for developer from here: [https://dev.mysql.com/downloads/file/?id=526407][1] and configure it according to your properties (username & password)

DB-type | r2dbc-url | liquibase/flyway-migrator url | 
--- | - | -- |
h2 (in-memory) | r2dbc:h2:mem:///${spring.r2dbc.name}?options=DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false | jdbc:h2:mem:${spring.r2dbc.name};DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS ${spring.r2dbc.name} |
Mysql | r2dbc:mysql://${spring.r2dbc.username}:${spring.r2dbc.password}@127.0.0.1:3306/${spring.r2dbc.name}?allowPublicKeyRetrieval=true&useSSL=false&autoReconnect=true&createDatabaseIfNotExist=true&serverTimezone=UTC | jdbc:mysql://${spring.r2dbc.username}:${spring.r2dbc.password}@127.0.0.1:3306/${spring.r2dbc.name}?allowPublicKeyRetrieval=true&useSSL=false&autoReconnect=true&createDatabaseIfNotExist=true&serverTimezone=UTC |


        /*
        Future vs Mono
        1. Both can return zero or one value.
        2. Future start running as soon as I create it (eager) mono is lazy, it is just a data structure.
        3. Future represent *A value* may not computed yet,
           Mono represent a *recipe* to get a value in a non blocking way
           It means that I can use the same nono to trigger multiple computation (activate it more then once)
           In essence it is a code that I can manipulate like a data,
           compose it to create complex abstraction where
           In the case of Future the composition applied only for the current value, not to the recipe,
           For example it makes no sense to compose retries to a future that already send request and maybe return value.
           With Mono the composition give me another more complex
           recipe that I can use at will
        4. Mono has 2 kinds of final events (complete  (possibly empty), and error) thus I can know when Mono is empty,
           and I don't need null to represent absence of value
           while future just leave me hanging in case of empty value
        5. Mono has rich set of combinator compared to future,
           for example try to map the error a future return to another type of error or recover with value.
        6. A mono can be turned to a future at any time.
        7. Mono is not depends on spring, just on project-reactor.
        reference https://projectreactor.io/docs/core/release/reference/
         */

        // Mono is activated using the subscribe method (non-blocking), or the block method (blocking)

//        reactive protocol publisher/subscriber and subscription
//        The doOnX calls for side effect
//        map and flatmap (functor and monad)
//        many more combinators
//        mono and null are not good friends
//        exceptions are unchecked


//        val m = Mono.error<String>(IllegalAccessError("foo"))
//        m.log().toFuture().get()

        // well that is not very interesting
# Documentation

## Swagger
- http://localhost:8080/webjars/swagger-ui/index.html 



