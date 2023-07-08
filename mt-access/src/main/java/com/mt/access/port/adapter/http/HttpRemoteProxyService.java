package com.mt.access.port.adapter.http;

import com.mt.access.domain.model.RemoteProxyService;
import com.mt.access.domain.model.proxy.CheckSumValue;
import com.mt.access.domain.model.proxy.ProxyInfo;
import com.mt.access.infrastructure.AppConstant;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HttpRemoteProxyService implements RemoteProxyService {
    @Autowired
    private EurekaClient discoveryClient;
    @Autowired
    private RestTemplate restTemplate;
    private static final String PROXY_CHECKSUM_URL ="info/checkSum";

    @Override
    public Map<ProxyInfo, CheckSumValue> getCacheEndpointSum() {
        HashMap<ProxyInfo, CheckSumValue> valueHashMap = new HashMap<>();
        Application application =
            discoveryClient.getApplication(AppConstant.MT_AUTH_PROXY_APP_ID.toUpperCase());
        if (application != null && !application.getInstances().isEmpty()) {
            List<InstanceInfo> instances = application.getInstances();
            instances.forEach((e) -> {
                int i = instances.indexOf(e);
                ProxyInfo proxyInfo = new ProxyInfo(i);
                ResponseEntity<String> exchange = null;
                try {
                    exchange = restTemplate
                        .exchange(e.getHomePageUrl() + PROXY_CHECKSUM_URL, HttpMethod.GET, null,
                            String.class);
                } catch (Exception ex) {
                    log.error("error during get sum value from proxy", ex);
                    valueHashMap.put(proxyInfo, CheckSumValue.failed());
                }
                if (exchange != null && exchange.getStatusCode().value() == 200) {
                    valueHashMap.put(proxyInfo, CheckSumValue.raw(exchange.getBody()));
                } else {
                    valueHashMap.put(proxyInfo, CheckSumValue.failed());
                }
            });
        }
        return valueHashMap;
    }
}
