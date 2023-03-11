package com.mt.proxy.port.adapter.http;

import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;

import com.mt.proxy.domain.Utility;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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

            if (null == Utility.getUuid(httpRequest) &&
                !Utility.isWebSocket(httpRequest.getHeaders())) {
                String newUuid = UUID.randomUUID().toString();
                log.debug("uuid not found for outgoing request, auto generate value {} path {}",
                    newUuid,
                    httpRequest.getURI().getPath() + (httpRequest.getURI().getRawQuery() == null ?
                        ("?" + httpRequest.getURI().getRawQuery()) : ""));
                httpRequest.getHeaders().set(REQ_UUID, newUuid);
            }
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        }
    }
}
