package com.example.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GrpcTrailerFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();

        response.getHeaders().add(HttpHeaders.TRAILER, "grpc-status, grpc-message");

        return chain.filter(exchange)
                .then(Mono.defer(() -> {
                    response.beforeCommit(() -> {
                        response.getHeaders().add("grpc-status", "0");
                        response.getHeaders().add("grpc-message", "Success");
                        return Mono.empty();
                    });
                    return response.setComplete();
                }));
    }

    @Override
    public int getOrder() {
        return -2;
    }
}