package com.example.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GrpcEndStreamFilter extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpResponse response = exchange.getResponse();

            response.beforeCommit(() -> {
                response.getHeaders().add("grpc-status", "0");
                response.getHeaders().add("endStream", "true");
                return Mono.empty();
            });

            return chain.filter(exchange);
        };
    }
}