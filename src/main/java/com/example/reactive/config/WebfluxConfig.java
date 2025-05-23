//package com.example.reactive.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.config.ResourceHandlerRegistry;
//import org.springframework.web.reactive.config.WebFluxConfigurer;
//
//@Configuration
//public class WebfluxConfig implements WebFluxConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//        registry.addResourceHandler("/webjars/swagger-ui/index.html**")
//                .addResourceLocations("classpath:/META-INF/resources/");
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//}