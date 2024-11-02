package com.mt.common.domain.model.logging;

import java.util.Collections;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OAuthRestTemplateConfig {

    private static final int MAX_CONN_PER_ROUTE = 10;
    private static final int MAX_CONN_TOTAL = 100;

    @Bean
    public RestTemplate getRestTemplate(HttpComponentsClientHttpRequestFactory factory,
                                        OutgoingReqInterceptor requestInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(requestInterceptor));
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory getHttpComponentsClientHttpRequestFactory() {
        CloseableHttpClient build =
            HttpClientBuilder.create().setMaxConnPerRoute(MAX_CONN_PER_ROUTE).setMaxConnTotal(
                MAX_CONN_TOTAL).build();
        return new HttpComponentsClientHttpRequestFactory(build);
    }

}