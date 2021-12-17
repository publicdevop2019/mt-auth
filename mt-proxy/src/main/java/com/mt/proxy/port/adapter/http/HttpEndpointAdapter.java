package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.Endpoint;
import com.netflix.discovery.EurekaClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class HttpEndpointAdapter implements EndpointAdapter {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EurekaClient eurekaClient;
    @Value("${manytree.url.endpoint}")
    private String endpointUrl;
    @Value("${manytree.mt0.name}")
    private String appName;

    @Override
    public Set<Endpoint> fetchAllEndpoints() {
        Set<Endpoint> data;
        if(eurekaClient.getApplication(appName)!=null){
            String homePageUrl = eurekaClient.getApplication(appName).getInstances().get(0).getHomePageUrl();
            String url = homePageUrl + endpointUrl;
            ResponseEntity<SumPagedRep<Endpoint>> exchange = restTemplate.exchange(url + "?page=size:40,num:0", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });
            if (exchange.getStatusCode().is2xxSuccessful()) {
                SumPagedRep<Endpoint> body = exchange.getBody();
                if (body == null)
                    throw new IllegalStateException("unable to load endpoint profile from remote: " + url);
                if (body.getData().size() == 0)
                    throw new IllegalStateException("endpoint profile from remote: " + url + " is empty");
                data = new HashSet<>(body.getData());
                double l = (double) body.getTotalItemCount() / body.getData().size();
                double ceil = Math.ceil(l);
                int i = BigDecimal.valueOf(ceil).intValue();
                for (int a = 1; a < i; a++) {
                    ResponseEntity<SumPagedRep<Endpoint>> exchange2 = restTemplate.exchange(url + "?page=size:40,num:" + a, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });
                    SumPagedRep<Endpoint> body2 = exchange2.getBody();
                    if (body2 == null || body2.getData().size() == 0)
                        throw new IllegalStateException("unable to load endpoint profile from remote");
                    data.addAll(body2.getData());
                }
            } else {
                log.error("error during load endpoint profile {}", exchange.getBody());
                throw new IllegalStateException("error during load endpoint profile, check log for more details");
            }
        }else{
            log.error("reload request was ignore due to service is not ready");
            throw new IllegalStateException("reload request was ignore due to service is not ready");
        }
        return data;
    }

    @Data
    public static class SumPagedRep<T> {
        protected List<T> data;
        protected Long totalItemCount;
    }

}
