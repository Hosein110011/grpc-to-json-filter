package com.example.api_gateway.filter;


import com.example.api_gateway.grpc.TestRequest;
import com.example.api_gateway.grpc.TestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class GrpcToJsonFilter {

    private RestTemplate restTemplate = new RestTemplate();

    public TestResponse filter(TestRequest request) throws InvalidProtocolBufferException, JsonProcessingException {

        String jsonRequest;
        try {
            jsonRequest = JsonFormat.printer().print(request);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

        String jsonResponse = restTemplate.postForObject(
                "http://localhost:8088/test-grpc/example.MyGrpcService/testGateway",
                requestEntity,
                String.class
        );

        System.out.println("json resp  " + jsonResponse);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(jsonResponse);

        TestResponse response = TestResponse.newBuilder().setResult(jsonNode.get("result").toString()).build();

        System.out.println("main " + response.getResult());

        return response;
    }

}