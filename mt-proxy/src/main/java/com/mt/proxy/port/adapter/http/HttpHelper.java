package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.SumPagedRep;
import com.mt.proxy.infrastructure.LogHelper;
import com.netflix.discovery.EurekaClient;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class HttpHelper {
    @Value("${manytree.mt-access.appId}")
    private String appName;
    @Autowired
    private EurekaClient eurekaClient;
    @Autowired
    @Getter
    private RestTemplate restTemplate;

    public String resolveAccessPath() {
        if (eurekaClient.getApplication(appName) != null) {
            log.debug("update property value with resolve access path");
            return eurekaClient.getApplication(appName).getInstances().get(0).getHomePageUrl();
        } else {
            log.error("unable to resolve due to service is not ready");
            throw new IllegalStateException(
                "unable to resolve due to service is not ready");
        }
    }

    /**
     * load all paginated data
     * @param urlWithoutPagination url
     * @param pageSize page size
     * @param allowEmpty allow empty result
     * @param <T> type T
     * @return unique <T> set
     */
    public <T> Set<T> loadAllData(String urlWithoutPagination, int pageSize, boolean allowEmpty,ParameterizedTypeReference<SumPagedRep<T>> parameterizedTypeReference) {
        Set<T> data;
        ResponseEntity<SumPagedRep<T>> exchange = getRestTemplate()
            .exchange(urlWithoutPagination + "?page=size:" + pageSize + ",num:0", HttpMethod.GET,
                null,
                parameterizedTypeReference);
        if (exchange.getStatusCode().is2xxSuccessful()) {
            SumPagedRep<T> body = exchange.getBody();
            if (body == null) {
                throw new IllegalStateException(
                    "unable to load data from remote: " + urlWithoutPagination);
            }
            if (body.getData().size() == 0) {
                if (allowEmpty) {
                    return Collections.emptySet();
                }
                throw new IllegalStateException(
                    "data from remote: " + urlWithoutPagination + " is empty");
            }
            data = new LinkedHashSet<>(body.getData());
            double l = (double) body.getTotalItemCount() / body.getData().size();
            double ceil = Math.ceil(l);
            int i = BigDecimal.valueOf(ceil).intValue();
            for (int a = 1; a < i; a++) {
                ResponseEntity<SumPagedRep<T>> exchange2 = getRestTemplate()
                    .exchange(urlWithoutPagination + "?page=size:" + pageSize + ",num:" + a,
                        HttpMethod.GET, null,
                        parameterizedTypeReference);
                SumPagedRep<T> body2 = exchange2.getBody();
                if (body2 == null || body2.getData().size() == 0) {
                    throw new IllegalStateException(
                        "unable to load data from remote");
                }
                data.addAll(body2.getData());
            }
        } else {
            log.error("error during load data {}", exchange.getBody());
            throw new IllegalStateException(
                "error during load data, check log for more details");
        }
        return data;
    }
}
