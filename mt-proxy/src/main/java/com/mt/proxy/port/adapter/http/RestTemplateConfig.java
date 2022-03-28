package com.mt.proxy.port.adapter.http;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate getRestTemplate(OutgoingReqInterceptor outgoingReqInterceptor) {
        RestTemplate restTemplate2 = new RestTemplate();
        restTemplate2.setInterceptors(Collections.singletonList(outgoingReqInterceptor));
        return restTemplate2;
    }

    @Slf4j
    @Component
    public static class OutgoingReqInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                            ClientHttpRequestExecution clientHttpRequestExecution)
            throws IOException {
            if (null == MDC.get("UUID")) {
                String s = UUID.randomUUID().toString();
                log.debug("UUID not found for outgoing request, auto generate value {}", s);
                MDC.put("UUID", s);
            }
            httpRequest.getHeaders().set("UUID", MDC.get("UUID"));
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        }
    }
}
