package com.example.api_gateway.grpc;


import com.example.api_gateway.filter.GrpcToJsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@GrpcService
public class GrpcServer extends ReactorMyGrpcServiceGrpc.MyGrpcServiceImplBase {

    private final RestTemplate restTemplate = new RestTemplate();

    private GrpcToJsonFilter grpcToJsonFilter = new GrpcToJsonFilter();

    @Override
    public Mono<TestResponse> testGateway(Mono<TestRequest> request) {


//            TestResponse response = grpcToJsonFilter.filter(request);


        return
                request
                        .map(TestRequest::getData)
                        .map(data -> {
                            TestResponse response = TestResponse.newBuilder().setResult(data).build();
                            return response;
                        });

    }



}
