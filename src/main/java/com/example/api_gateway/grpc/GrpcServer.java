package com.example.api_gateway.grpc;


//import com.example.api_gateway.filter.GrpcFilter;
import com.example.api_gateway.filter.GrpcToJsonFilter;
import net.devh.boot.grpc.server.service.GrpcService;
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
