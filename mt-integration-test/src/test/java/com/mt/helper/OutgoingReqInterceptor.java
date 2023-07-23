package com.mt.helper;

import java.io.IOException;
import java.util.UUID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
@Data
public class OutgoingReqInterceptor implements ClientHttpRequestInterceptor {
    private UUID testId;

    public OutgoingReqInterceptor(UUID testId) {
        this.testId = testId;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution)
        throws IOException {
        httpRequest.getHeaders().set("testId", testId.toString());
        if (httpRequest.getHeaders().get("changeId") == null) {
            String s = UUID.randomUUID().toString();
            log.trace("change id for request is {}", s);
            httpRequest.getHeaders().set("changeId", s);
        }
        if (httpRequest.getHeaders().get("uuid") == null) {
            String s = UUID.randomUUID().toString();
            log.info("{} uuid {}", httpRequest.getURI(), s);
            httpRequest.getHeaders().set("uuid", s);
        }
        httpRequest.getHeaders().set("X-XSRF-TOKEN", "123");
        httpRequest.getHeaders().add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
