package com.mt.access.port.adapter.http;

import static com.mt.access.infrastructure.AppConstant.PROXY_NAME;

import com.mt.access.domain.model.RemoteProxyService;
import com.mt.access.domain.model.proxy.CheckSumValue;
import com.mt.access.domain.model.proxy.ProxyInfo;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.instance.Instance;
import com.mt.common.domain.model.validate.Checker;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HttpRemoteProxyService implements RemoteProxyService {
    private static final String PROXY_CHECKSUM_URL = "/info/checkSum";
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map<ProxyInfo, CheckSumValue> getCacheEndpointSum() {
        List<String> urls =
            CommonDomainRegistry.getInstanceRepository().getAllInstances().stream()
                .filter(e -> PROXY_NAME.equalsIgnoreCase(e.getName())).map(Instance::getUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        HashMap<ProxyInfo, CheckSumValue> valueHashMap = new HashMap<>();
        if (Checker.isEmpty(urls)) {
            log.warn("proxy check skipped due to no url configured");
            return valueHashMap;
        }
        urls.forEach((url) -> {
            ProxyInfo proxyInfo = new ProxyInfo(url);
            ResponseEntity<String> exchange = null;
            try {
                exchange = restTemplate
                    .exchange(url + PROXY_CHECKSUM_URL, HttpMethod.GET, null,
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
        return valueHashMap;
    }
}
