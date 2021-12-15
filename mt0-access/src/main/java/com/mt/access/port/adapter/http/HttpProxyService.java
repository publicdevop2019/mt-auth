package com.mt.access.port.adapter.http;

import com.mt.access.domain.model.ProxyService;
import com.mt.access.domain.model.proxy.CheckSumValue;
import com.mt.access.domain.model.proxy.ProxyInfo;
import com.mt.access.infrastructure.AppConstant;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class HttpProxyService implements ProxyService {
    @Autowired
    private EurekaClient discoveryClient;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${mt.url.proxy.check}")
    private String proxyUrl;

    @Override
    public Map<ProxyInfo, CheckSumValue> getCacheEndpointSum() {
        Application application = discoveryClient.getApplication(AppConstant.MT1_PROXY.toUpperCase());
        List<InstanceInfo> instances = application.getInstances();
        HashMap<ProxyInfo, CheckSumValue> valueHashMap = new HashMap<>();
        instances.forEach((e) -> {
            int i = instances.indexOf(e);
            ProxyInfo proxyInfo = new ProxyInfo(i);
            ResponseEntity<String> exchange=null;
            try {
                exchange = restTemplate.exchange(e.getHomePageUrl() + proxyUrl, HttpMethod.GET, null, String.class);
            } catch (Exception ex) {
                log.error("error during get sum value from proxy",ex);
                valueHashMap.put(proxyInfo, CheckSumValue.failed());
            }
            if (exchange!=null && exchange.getStatusCode().value() == 200) {
                valueHashMap.put(proxyInfo, CheckSumValue.raw(exchange.getBody()));
            } else {
                valueHashMap.put(proxyInfo, CheckSumValue.failed());
            }
        });
        return valueHashMap;
    }
}
