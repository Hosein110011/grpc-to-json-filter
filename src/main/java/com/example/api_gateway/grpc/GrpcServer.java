package com.example.api_gateway.grpc;


import com.example.api_gateway.filter.GrpcToJsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@GrpcService
public class GrpcServer extends MyGrpcServiceGrpc.MyGrpcServiceImplBase {

    private final RestTemplate restTemplate = new RestTemplate();

    private GrpcToJsonFilter grpcToJsonFilter = new GrpcToJsonFilter();

    @Override
    public void testGateway(TestRequest request, StreamObserver<TestResponse> responseObserver) {

        try {

            TestResponse response = grpcToJsonFilter.filter(request);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (InvalidProtocolBufferException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("Invalid JSON format")
                    .withCause(e)
                    .asRuntimeException());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

}
