package com.mt.access.application.proxy.representation;

import com.mt.access.domain.model.proxy.CheckSumValue;
import com.mt.access.domain.model.proxy.ProxyInfo;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class CheckSumRepresentation {
    private String hostValue;
    private Map<String, String> proxyValue;

    public CheckSumRepresentation(CheckSumValue hostCheckSum,
                                  Map<ProxyInfo, CheckSumValue> cacheEndpointSum) {
        hostValue = hostCheckSum.getValue();
        proxyValue = new HashMap<>();
        cacheEndpointSum.forEach((k, v) -> proxyValue.put(k.getId(), v.getValue()));

    }
}
