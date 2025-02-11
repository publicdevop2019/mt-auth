package com.mt.access.port.adapter.http;

import com.mt.access.domain.model.RemoteProxyService;
import com.mt.access.domain.model.proxy.CheckSumValue;
import com.mt.access.domain.model.proxy.ProxyInfo;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HttpRemoteProxyService implements RemoteProxyService {
    private static final String PROXY_CHECKSUM_URL = "/info/checkSum";
    private static final String URL_DELIMITER = ",";
    @Autowired
    private RestTemplate restTemplate;
    @Value("${mt.misc.url.proxy:#{null}}")
    private String url;

    @Override
    public Map<ProxyInfo, CheckSumValue> getCacheEndpointSum() {
        HashMap<ProxyInfo, CheckSumValue> valueHashMap = new HashMap<>();
        if (Checker.isBlank(url)) {
            log.warn("proxy check skipped due to no url configured");
            return valueHashMap;
        }
        String[] urls = url.split(URL_DELIMITER);
        Set<String> trimmedUrls = Arrays.stream(urls).map(String::trim).collect(Collectors.toSet());
        trimmedUrls.forEach((url) -> {
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
