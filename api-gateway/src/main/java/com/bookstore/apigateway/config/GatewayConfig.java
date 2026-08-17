package com.bookstore.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("lb://USER-SERVICE"))
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .uri("lb://ORDER-SERVICE"))
                .route("book-service", r -> r
                        .path("/api/books/**")
                        .uri("lb://BOOK-SERVICE"))
                .build();
    }
}
