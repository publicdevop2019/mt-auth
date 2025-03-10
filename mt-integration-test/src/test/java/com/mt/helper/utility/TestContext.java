package com.mt.helper.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.helper.OutgoingReqInterceptor;
import java.util.Collections;
import java.util.UUID;
import lombok.Getter;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class TestContext {
    private static final ThreadLocal<UUID> testId = new ThreadLocal<>();
    private static final ThreadLocal<TestRestTemplate> restTemplate = new ThreadLocal<>();
    @Getter
    public static ObjectMapper mapper =
        new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private TestContext() {

    }

    public static void init() {
        testId.set(UUID.randomUUID());
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        testRestTemplate.getRestTemplate()
            .setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(testId.get())));
        testRestTemplate.getRestTemplate()
            .setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        restTemplate.set(testRestTemplate);
    }

    public static RestTemplate getRestTemplate() {
        return restTemplate.get().getRestTemplate();
    }

    public static String getTestId() {
        return testId.get().toString();
    }
}
