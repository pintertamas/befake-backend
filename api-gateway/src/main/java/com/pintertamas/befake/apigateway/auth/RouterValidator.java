package com.pintertamas.befake.apigateway.auth;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public final class RouterValidator {

    public static final List<String> openApiEndpoints= List.of(
            "/auth/login",
            "/user/register",
            "/"
    );

    public final Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}