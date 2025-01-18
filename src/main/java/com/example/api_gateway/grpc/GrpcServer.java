package com.example.api_gateway.grpc;


//@GrpcService
//public class GrpcServer extends MyGrpcServiceGrpc.MyGrpcServiceImplBase {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Override
//    public void testGateway(TestRequest request, StreamObserver<TestResponse> responseObserver) {
//
//        try {
//
//            System.out.println(JsonFormat.printer().print(request));
//
//            responseObserver.onNext(TestResponse.newBuilder().setResult(request.getData()).build());
//            responseObserver.onCompleted();
//        } catch (InvalidProtocolBufferException e) {
//            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
//                    .withDescription("Invalid JSON format")
//                    .withCause(e)
//                    .asRuntimeException());
//        }
//
//
//    }
//
//}