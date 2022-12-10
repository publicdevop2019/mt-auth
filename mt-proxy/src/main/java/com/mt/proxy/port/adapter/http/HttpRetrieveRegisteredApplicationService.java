package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.RegisteredApplication;
import com.mt.proxy.domain.RetrieveRegisterApplicationService;
import com.mt.proxy.domain.SumPagedRep;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpRetrieveRegisteredApplicationService
    implements RetrieveRegisterApplicationService {

    @Value("${manytree.url.clients}")
    private String url;
    @Autowired
    private HttpHelper httpHelper;

    @Override
    public Set<RegisteredApplication> fetchAll() {
        String homePageUrl = httpHelper.resolveAccessPath();
        String url = homePageUrl + this.url;
        return httpHelper.loadAllData(url, 40, false,
            new ParameterizedTypeReference<>() {
            });
    }


}
