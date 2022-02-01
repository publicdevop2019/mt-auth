package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.RetrieveRegisterApplicationService;
import com.mt.proxy.domain.RegisteredApplication;
import com.mt.proxy.domain.SumPagedRep;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class HttpRetrieveRegisteredApplicationService implements RetrieveRegisterApplicationService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EurekaClient eurekaClient;
    @Value("${manytree.url.clients}")
    private String url;
    @Value("${manytree.mt-access.appId}")
    private String appName;

    @Override
    public Set<RegisteredApplication> fetchAll() {
        Set<RegisteredApplication> data;
        if(eurekaClient.getApplication(appName)!=null){
            String homePageUrl = eurekaClient.getApplication(appName).getInstances().get(0).getHomePageUrl();
            String url = homePageUrl + this.url;
            ResponseEntity<SumPagedRep<RegisteredApplication>> exchange = restTemplate.exchange(url + "?page=size:40,num:0", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });
            if (exchange.getStatusCode().is2xxSuccessful()) {
                SumPagedRep<RegisteredApplication> body = exchange.getBody();
                if (body == null)
                    throw new IllegalStateException("unable to load registered application from remote: " + url);
                if (body.getData().size() == 0)
                    throw new IllegalStateException("registered application from remote: " + url + " is empty");
                data = new HashSet<>(body.getData());
                double l = (double) body.getTotalItemCount() / body.getData().size();
                double ceil = Math.ceil(l);
                int i = BigDecimal.valueOf(ceil).intValue();
                for (int a = 1; a < i; a++) {
                    ResponseEntity<SumPagedRep<RegisteredApplication>> exchange2 = restTemplate.exchange(url + "?page=size:40,num:" + a, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });
                    SumPagedRep<RegisteredApplication> body2 = exchange2.getBody();
                    if (body2 == null || body2.getData().size() == 0)
                        throw new IllegalStateException("unable to load registered application from remote");
                    data.addAll(body2.getData());
                }
            } else {
                log.error("error during load registered application {}", exchange.getBody());
                throw new IllegalStateException("error during load endpoint profile, check log for more details");
            }
        }else{
            log.error("reload request was ignore due to service is not ready");
            throw new IllegalStateException("reload request was ignore due to service is not ready");
        }
        return data;
    }


}
