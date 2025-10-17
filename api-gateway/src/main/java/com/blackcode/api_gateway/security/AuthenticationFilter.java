package com.blackcode.api_gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("=========================Filter API===================");
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        logger.info(path);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequest().getMethod().name())) {
            logger.info("=========================OPTION LOLOS===================");
            return chain.filter(exchange);
        }

        logger.info("=========================OPTION TIDAK LOLOS===================");


        if (path.startsWith("/api/auth/registration") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/refresh-token") ||
                path.startsWith("/api/auth/v3/api-docs")||
                path.startsWith("/api/financial-plan/v3/api-docs")||
                path.startsWith("/api/financial-saving-targets/v3/api-docs")||
                path.startsWith("/api/transaction/v3/api-docs") ||
                path.startsWith("/api/financial-evaluations/v3/api-docs") ||
                path.startsWith("/swagger-ui")) {

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(r -> r.headers(headers -> headers.set("X-Internal-Token", "secret-key-123")))
                    .build();
            return chain.filter(mutatedExchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return this.onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            return this.onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        String userId = jwtUtils.getUserIdFromToken(token);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r.headers(headers -> {
                    headers.set("X-Username", username);
                    headers.set("X-User-Id", userId);
                    headers.set("X-Internal-Token", "secret-key-123");
                    if (path.startsWith("/api/auth/logout")) {
                        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
                    }
                }))
                .build();

        return chain.filter(mutatedExchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}