package com.example.api_gateway.filter;


import com.example.api_gateway.grpc.MyGrpcServiceGrpc;
import com.example.api_gateway.grpc.TestRequest;
import com.example.api_gateway.grpc.TestResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcToGrpcFilter {

    private ManagedChannel channel;
    private MyGrpcServiceGrpc.MyGrpcServiceBlockingStub blockingStub;

    public GrpcToGrpcFilter() {
        this.channel = ManagedChannelBuilder.forAddress("localhost", 9091)
                .usePlaintext()
                .build();

        this.blockingStub = MyGrpcServiceGrpc.newBlockingStub(channel);
    }

    public TestResponse filter(TestRequest request) {
        TestResponse response = blockingStub.testGateway(request);

        return response;

    }


}
