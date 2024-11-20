package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.RegisteredApplication;
import com.mt.proxy.domain.RetrieveRegisterApplicationService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpRetrieveRegisteredApplicationService
    implements RetrieveRegisterApplicationService {

    private static final String CLIENT_URL = "/clients/proxy";
    @Autowired
    private HttpUtility httpHelper;

    @Override
    public Set<RegisteredApplication> fetchAll() {
        return httpHelper.loadAllData(httpHelper.getAccessUrl() + CLIENT_URL, 40, false,
            new ParameterizedTypeReference<>() {
            });
    }


}
