package com.mt.proxy.domain;

import static com.mt.proxy.infrastructure.AppConstant.REQ_CLIENT_IP;

import java.time.Instant;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

public class ApiReportService {
    public void logResponseDetail(ServerHttpResponse response) {
        long epochSecond = Instant.now().getEpochSecond();
        int value = response.getStatusCode().value();

    }

    public void logRequestDetails(ServerHttpRequest request) {
        long epochSecond = Instant.now().getEpochSecond();
        String s = MDC.get(REQ_CLIENT_IP);
    }
}
