package com.example.api_gateway.filter;

import com.example.api_gateway.grpc.TestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.nio.ByteBuffer;

@Component
public class GrpcToJsonFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        if (!originalResponse.getHeaders().containsKey("Trailer")) {
            return chain.filter(exchange);
        }

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                return super.writeWith(
                        fluxBody.map(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);

                            ObjectMapper objectMapper = new ObjectMapper();

                            String prettyResp;
                                    try {
										Object jsonObject = objectMapper.readValue(bytes, Object.class);
                                        prettyResp = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                            TestResponse response;

                            try {
								TestResponse.Builder builder = TestResponse.newBuilder();
                                JsonFormat.parser().merge(prettyResp, builder);
								response = builder.build();
                            } catch (InvalidProtocolBufferException e) {
                                throw new RuntimeException(e);
                            }

                            ByteBuffer buf = getStructuredGrpcResponse(response);

                            return bufferFactory.wrap(buf.array());

                        })
                );
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private ByteBuffer getStructuredGrpcResponse(TestResponse response) {
        byte[] byteResponse = response.toByteArray();

        int payloadSize = byteResponse.length;
        ByteBuffer buf = ByteBuffer.allocate(payloadSize + 5);
        buf.put((byte) 0);
        buf.putInt(payloadSize);
        buf.put(byteResponse);

        return buf;
    }
}