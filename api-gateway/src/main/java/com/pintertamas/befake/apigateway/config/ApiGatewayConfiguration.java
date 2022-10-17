package com.pintertamas.befake.apigateway.config;

import com.pintertamas.befake.apigateway.auth.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

    final AuthenticationFilter filter;

    public ApiGatewayConfiguration(AuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/user/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-service"))

                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth-service"))

                .route("post-service", r -> r.path("/post/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://post-service"))

                .route("friend-service", r -> r.path("/friendlist/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://friend-service"))

                .route("interaction-service", r -> r.path("/comment/**", "/reaction/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://interaction-service"))

                .route("notification-service", r -> r.path("/notification/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://notification-service"))
                .build();
    }
}
