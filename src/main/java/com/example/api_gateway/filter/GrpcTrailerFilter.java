package com.example.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;

@Component
public class GrpcTrailerFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (exchange.getRequest().getHeaders().get(HttpHeaders.CONTENT_TYPE).contains("application/grpc")) {
            exchange.getResponse().getHeaders().add(HttpHeaders.TRAILER, "grpc-status, grpc-message");
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -2;
    }
}