package com.mt.access.domain.model;

import com.mt.access.domain.model.proxy.CheckSumValue;
import com.mt.access.domain.model.proxy.ProxyInfo;
import java.util.Map;

public interface RemoteProxyService {
    Map<ProxyInfo, CheckSumValue> getCacheEndpointSum();
}
