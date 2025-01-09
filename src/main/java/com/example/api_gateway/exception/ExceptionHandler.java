package com.example.api_gateway.exception;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class ExceptionHandler {

    @GrpcExceptionHandler(Exception.class)
    public Status handleException(Exception exp) {
        return Status.INTERNAL.withDescription("Server internal error").withCause(exp);
    }
}
