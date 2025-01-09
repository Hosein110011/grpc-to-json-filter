package com.example.api_gateway.filter;
//
//import com.example.api_gateway.grpc.TestRequest;
//import com.example.api_gateway.grpc.TestResponse;
//import com.fasterxml.jackson.databind.*;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.google.protobuf.DescriptorProtos;
//import com.google.protobuf.Descriptors;
//import com.google.protobuf.DynamicMessage;
//import com.google.protobuf.InvalidProtocolBufferException;
//import io.grpc.*;
//import io.grpc.netty.NettyChannelBuilder;
//import io.grpc.protobuf.ProtoUtils;
//import org.reactivestreams.Publisher;
//import org.springframework.cloud.gateway.config.GrpcSslConfigurer;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.cloud.gateway.route.Route;
//import org.springframework.cloud.gateway.support.GatewayToStringStyler;
//import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
//import org.springframework.core.ResolvableType;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.http.codec.json.Jackson2JsonDecoder;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
//import org.springframework.util.MimeType;
//import org.springframework.web.client.RestClient;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import javax.net.ssl.SSLException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//import java.util.function.Function;
//
//
//public class GrpcToJsonFilter extends AbstractGatewayFilterFactory<GrpcToJsonFilter.Config> {
////        private final GrpcSslConfigurer grpcSslConfigurer;
////        private final ResourceLoader resourceLoader;
//
//    public GrpcToJsonFilter() {}
//
////        public GrpcToJsonFilter(GrpcSslConfigurer grpcSslConfigurer, ResourceLoader resourceLoader) {
////            super(Config.class);
//////            this.grpcSslConfigurer = grpcSslConfigurer;
//////            this.resourceLoader = resourceLoader;
////        }
//
//        public List<String> shortcutFieldOrder() {
//            return Arrays.asList("protoDescriptor", "protoFile", "service", "method");
//        }
//
//        @Override
//        public GatewayFilter apply(Config config) {
//            GatewayFilter filter = new GatewayFilter() {
//                public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//                    GRPCResponseDecorator modifiedResponse = GrpcToJsonFilter.this.new GRPCResponseDecorator(exchange, config);
//                    ServerWebExchangeUtils.setAlreadyRouted(exchange);
//                    return modifiedResponse.writeWith(exchange.getRequest().getBody()).then(chain.filter(exchange.mutate().response(modifiedResponse).build()));
//                }
//
//                public String toString() {
//                    return GatewayToStringStyler.filterToStringCreator(GrpcToJsonFilter.this).toString();
//                }
//            };
//            int order = -2;
//            return new OrderedGatewayFilter(filter, order);
//        }
//
//        public static class Config {
//            private String protoDescriptor;
//            private String protoFile;
//            private String service;
//            private String method;
//
//            public Config() {
//            }
//
//            public String getProtoDescriptor() {
//                return this.protoDescriptor;
//            }
//
//            public Config setProtoDescriptor(String protoDescriptor) {
//                this.protoDescriptor = protoDescriptor;
//                return this;
//            }
//
//            public String getProtoFile() {
//                return this.protoFile;
//            }
//
//            public Config setProtoFile(String protoFile) {
//                this.protoFile = protoFile;
//                return this;
//            }
//
//            public String getService() {
//                return this.service;
//            }
//
//            public Config setService(String service) {
//                this.service = service;
//                return this;
//            }
//
//            public String getMethod() {
//                return this.method;
//            }
//
//            public Config setMethod(String method) {
//                this.method = method;
//                return this;
//            }
//        }
//
//        class GRPCResponseDecorator extends ServerHttpResponseDecorator {
//            private final ServerWebExchange exchange;
////            private final Descriptors.Descriptor descriptor;
//            private final ObjectWriter objectWriter;
//            private final ObjectReader objectReader;
////            private final ClientCall<DynamicMessage, DynamicMessage> clientCall;
//            private final ObjectNode objectNode;
//            private final ObjectMapper objectMapper = new ObjectMapper();
//
//            GRPCResponseDecorator(ServerWebExchange exchange, Config config) {
//                super(exchange.getResponse());
//                this.exchange = exchange;
//                this.objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
//
////                try {
////                    Resource descriptorFile = GrpcToJsonFilter.this.resourceLoader.getResource(config.getProtoDescriptor());
////                    Resource protoFile = GrpcToJsonFilter.this.resourceLoader.getResource(config.getProtoFile());
////                    this.descriptor = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorFile.getInputStream()).getDescriptorForType();
////                    Descriptors.MethodDescriptor methodDescriptor = this.getMethodDescriptor(config, descriptorFile.getInputStream());
////                    Descriptors.ServiceDescriptor serviceDescriptor = methodDescriptor.getService();
////                    Descriptors.Descriptor outputType = methodDescriptor.getOutputType();
////                    this.clientCall = this.createClientCallForType(config, serviceDescriptor, outputType);
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
////                    this.objectWriter = objectMapper.writer();
//                    this.objectReader = objectMapper.readerFor(JsonNode.class);
//                    this.objectNode = objectMapper.createObjectNode();
////                } catch (Descriptors.DescriptorValidationException | IOException e) {
////                    throw new RuntimeException(e);
////                }
//            }
//
//            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                this.exchange.getResponse().getHeaders().set("Content-Type", "application/grpc");
//                this.getDelegate().writeWith(this.serializeGRPCRequest().map(this.callRestServer()).map(this.sendGRPCResponse()));
//                return null;
//            }
//
////            private Function<JsonNode, Void> sendToGrpcClient() {
////
////                return (grpcResponse) -> {
////                    try {
////                        byte[] response = this.objectWriter.writeValueAsBytes(grpcResponse);
////
////                        ClientCalls.blockingUnaryCall(this.clientCall, DynamicMessage.parseFrom(this.descriptor, request));
////                    } catch (IOException e) {
////                        throw new RuntimeException(e);
////                    }
////                };
////
////            }
//
////            private ClientCall<DynamicMessage, DynamicMessage> createClientCallForType(Config config, Descriptors.ServiceDescriptor serviceDescriptor, Descriptors.Descriptor outputType) {
////                MethodDescriptor.Marshaller<DynamicMessage> marshaller = ProtoUtils.marshaller(DynamicMessage.newBuilder(outputType).build());
////                MethodDescriptor<DynamicMessage, DynamicMessage> methodDescriptor = MethodDescriptor.newBuilder().setType(MethodDescriptor.MethodType.UNKNOWN).setFullMethodName(MethodDescriptor.generateFullMethodName(serviceDescriptor.getFullName(), config.getMethod())).setRequestMarshaller(marshaller).setResponseMarshaller(marshaller).build();
////                Channel channel = this.createChannel();
////                return channel.newCall(methodDescriptor, CallOptions.DEFAULT);
////            }
//
//            private Descriptors.MethodDescriptor getMethodDescriptor(Config config, InputStream descriptorFile) throws IOException, Descriptors.DescriptorValidationException {
//                DescriptorProtos.FileDescriptorSet fileDescriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(descriptorFile);
//                DescriptorProtos.FileDescriptorProto fileProto = fileDescriptorSet.getFile(0);
//                Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(fileProto, new Descriptors.FileDescriptor[0]);
//                Descriptors.ServiceDescriptor serviceDescriptor = fileDescriptor.findServiceByName(config.getService());
//                if (serviceDescriptor == null) {
//                    throw new NoSuchElementException("No Service found");
//                } else {
//                    List<Descriptors.MethodDescriptor> methods = serviceDescriptor.getMethods();
//                    return (Descriptors.MethodDescriptor)methods.stream().filter((method) -> method.getName().equals(config.getMethod())).findFirst().orElseThrow(() -> new NoSuchElementException("No Method found"));
//                }
//            }
//
////            private ManagedChannel createChannel() {
////                URI requestURI = ((Route)this.exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR)).getUri();
////                return this.createChannelChannel(requestURI.getHost(), requestURI.getPort());
////            }
//
//            private Function<JsonNode, TestResponse> callRestServer() {
//                return (jsonRequest) -> {
//                    try {
//                        return RestClient.create().post().uri(exchange.getRequest().getURI()).retrieve().body(TestResponse.class);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                };
//            }
//
//            private Function<TestResponse, DataBuffer> sendGRPCResponse() {
//                return (jsonResponse) -> {
//                    try {
//                        String jsonBody = this.objectMapper.writeValueAsString(jsonResponse);
//                        ServerHttpResponse response = exchange.getResponse();
//                        DataBuffer dataBuffer = response.bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));
//                        response.writeWith(Mono.just(dataBuffer));
//                        exchange.mutate().response(response).build();
//                        return dataBuffer;
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                };
//            }
//
//            private Flux<JsonNode> serializeGRPCRequest() {
//                return this.exchange.getRequest().getBody().mapNotNull((dataBufferBody) -> {
//                    if (dataBufferBody.capacity() == 0) {
//                        return this.objectNode;
//                    } else {
//                        TestRequest grpcRequest = TestRequest.newBuilder().build();
//                        byte[] bytes = new byte[dataBufferBody.readableByteCount()];
//                        dataBufferBody.read(bytes);
//                        DataBufferUtils.release(dataBufferBody);
//
//                        byte[] actualMessage = Arrays.copyOfRange(bytes, 5, bytes.length);
//
//                        try {
//                            grpcRequest = TestRequest.parseFrom(actualMessage);
//                        } catch (InvalidProtocolBufferException e) {
//                            throw new RuntimeException(e);
//                        }
//                        ResolvableType targetType = ResolvableType.forType(JsonNode.class);
//                        return grpcRequest;
//                    }
//                }).cast(JsonNode.class);
//            }
//
//            private Flux<JsonNode> wrapJsonResponse() {
//                return
//                        this.exchange.getRequest().getBody().mapNotNull((dataBufferBody) -> {
//                            if (dataBufferBody.capacity() == 0) {
//                                return this.objectNode;
//                            } else {
//                                ResolvableType targetType = ResolvableType.forType(JsonNode.class);
//                                return (new Jackson2JsonDecoder()).decode(dataBufferBody, targetType, (MimeType) null, (Map) null);
//                            }
//                        }).cast(JsonNode.class);
//            }
//

import com.example.api_gateway.grpc.TestRequest;
import com.example.api_gateway.grpc.TestResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.util.JsonFormat;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.JsonToGrpcGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.RouteMatcher;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

////            private ManagedChannel createChannelChannel(String host, int port) {
////                NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(host, port);
////
////                try {
////                    return GrpcToJsonFilter.this.grpcSslConfigurer.configureSsl(nettyChannelBuilder);
////                } catch (SSLException e) {
////                    throw new RuntimeException(e);
////                }
////            }
//        }
//
//
//    }

public class GrpcToJsonFilter extends AbstractGatewayFilterFactory<GrpcToJsonFilter.Config> {

    private final ObjectWriter objectWriter;

    public GrpcToJsonFilter(ObjectWriter objectWriter) {
        super(Config.class);
        this.objectWriter = objectWriter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (ServerWebExchange exchange, GatewayFilterChain chain) -> exchange.getRequest().getBody()
                .next()
                .publishOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(dataBuffer -> {

                    System.out.println(exchange.getRequest().getHeaders());

                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);

                        ObjectMapper objectMapper = new ObjectMapper();

                        byte[] actualMessage = Arrays.copyOfRange(bytes, 5, bytes.length);

                        TestRequest request = TestRequest.parseFrom(actualMessage);

                        System.out.println(request);

                        String jsonRequest = JsonFormat.printer().print(request);
                        System.out.println("json body: " + jsonRequest);

                        TestResponse response = TestResponse.newBuilder().build();

                        RestTemplate restTemplate = new RestTemplate();

                        exchange.getRequest().mutate().header("Content-Type", "application/json");

                        System.out.println(exchange.getRequest().getHeaders());

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

                        String responseEntity = restTemplate.postForObject(
                                "http://localhost:8088/test-grpc/example.MyGrpcService/testGateway",
                                requestEntity,
                                String.class
                        );

                        System.out.println("00000000000000000 " + responseEntity);

//                        Mono<byte[]> responseMono = webClient.post()
//                                .uri("/test-grpc/example.MyGrpcService/testGateway")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .bodyValue(jsonRequest)
//                                .retrieve()
//                                .bodyToMono(byte[].class);
//
//                        responseMono.subscribe(
//                                monoBytes -> {
//                                    try {
//                                        exchange.getResponse().getHeaders().add("Content-Type", "application/grpc");
//                                        System.out.println("dgfrhefrehreuhgj " + monoBytes);
//                                        byte[] actualData = Arrays.copyOfRange(bytes, 5, bytes.length);
//                                        TestResponse testResponse = TestResponse.parseFrom(actualData);
//                                        String jsonResponse = JsonFormat.printer().print(testResponse);
//                                        System.out.println("json resp: " + jsonResponse);
//                                        exchange.getResponse().getHeaders().add("Content-Type", "application/grpc");
//
//                                        String jsonBodyResponse = objectMapper.writeValueAsString(response);
//                                        DataBuffer dataBufferResponse = exchange.getResponse().bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
//                                        exchange.getResponse().writeWith(Mono.just(dataBufferResponse));
//                                        exchange.mutate().response(exchange.getResponse()).build();
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                },
//                                error -> {
//                                    System.err.println("Error occurred: " + error.getMessage());
//                                }
//                        );
//
//                        String jsonBody = JsonFormat.printer().print(response);
//                        System.out.println("json body: " + jsonBody);
//
//                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));


//                        String json = new String(bytes, StandardCharsets.UTF_8);
//
//                        ObjectMapper objectMapper = new ObjectMapper();
//
//                        TestRequestEntity requestEntity = new TestRequestEntity();
//                        requestEntity.setData(json);
//
//                        String jsonBody = objectMapper.writeValueAsString(requestEntity);
//                        System.out.println("Modified JSON Body: " + jsonBody);
//
//                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));

                        exchange.getResponse().getHeaders().add("Content-Type", "application/grpc");

//                        String jsonBodyResponse = objectMapper.writeValueAsString(response);
                        DataBuffer dataBufferResponse = exchange.getResponse().bufferFactory().wrap(responseEntity.getBytes(StandardCharsets.UTF_8));
                        exchange.getResponse().writeWith(Mono.just(dataBufferResponse));
                        exchange.mutate().response(exchange.getResponse()).build();

//                        URI requestURI = exchange.getRequest().getURI();
//                        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(requestURI.getHost(), requestURI.getPort());
//

                        return chain.filter(exchange);
//                                .then(Mono.defer(() -> exchange.getResponse().getBody()
//                                .next()
//                                .flatMap(responseDataBuffer -> {
//                                    try {
//                                        byte[] responseBytes = new byte[responseDataBuffer.readableByteCount()];
//                                        responseDataBuffer.read(responseBytes);
//                                        DataBufferUtils.release(responseDataBuffer);
//
//                                        TestResponse.Builder responseBuilder = TestResponse.newBuilder();
//                                        JsonFormat.parser().merge(new String(responseBytes, StandardCharsets.UTF_8), responseBuilder);
//
//                                        byte[] grpcResponse = responseBuilder.build().toByteArray();
//
//                                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
//                                        exchange.getResponse().setStatusCode(HttpStatus.OK);
//                                        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(grpcResponse)));
//                                    } catch (Exception e) {
//                                        return Mono.error(e);
//                                    }
//                                })));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }

    public static class Config {
    }

    private Function<JsonNode, byte[]> callGRPCServer() {
        return (jsonResponse) -> {
            try {
                this.objectWriter.writeValueAsBytes(jsonResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new byte[0];
        };
    }

}