package com.example.api_gateway.grpc;


//import com.example.api_gateway.filter.GrpcFilter;
import com.example.api_gateway.filter.GrpcToJsonFilter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

//@GrpcService
//public class GrpcServer extends ReactorMyGrpcServiceGrpc.MyGrpcServiceImplBase {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    private GrpcToJsonFilter grpcToJsonFilter = new GrpcToJsonFilter();
//
//    @Override
//    public Mono<TestResponse> testGateway(Mono<TestRequest> request) {
//
//
////            TestResponse response = grpcToJsonFilter.filter(request);
//
//
//        return
//                request
//                        .map(TestRequest::getData)
//                        .map(data -> {
//                            TestResponse response = TestResponse.newBuilder().setResult(data).build();
//                            return response;
//                        });
//
//    }
//
//
//
//}
@GrpcService
public class GrpcServer extends MyGrpcServiceGrpc.MyGrpcServiceImplBase {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void testGateway(TestRequest request, StreamObserver<TestResponse> responseObserver) {

        try {

            System.out.println(JsonFormat.printer().print(request));

            responseObserver.onNext(TestResponse.newBuilder().setResult(request.getData()).build());
            responseObserver.onCompleted();
        } catch (InvalidProtocolBufferException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("Invalid JSON format")
                    .withCause(e)
                    .asRuntimeException());
        }


    }

}