package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.Router;
import com.mt.proxy.domain.RetrieveRouterService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpRetrieveRouterService
    implements RetrieveRouterService {

    private static final String ROUTER_URL = "/routers/proxy";
    @Autowired
    private HttpUtility httpHelper;

    @Override
    public Set<Router> fetchAll() {
        return httpHelper.loadAllData(httpHelper.getAccessUrl() + ROUTER_URL, 40, false,
            new ParameterizedTypeReference<>() {
            });
    }


}
