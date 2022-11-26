package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.RetrieveSubscriptionService;
import com.mt.proxy.domain.SumPagedRep;
import com.mt.proxy.domain.rate_limit.Subscription;
import java.util.Collections;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpRetrieveSubscriptionService implements RetrieveSubscriptionService {

    @Value("${manytree.url.subscription}")
    private String subscriptionUrl;
    @Autowired
    private HttpHelper httpHelper;

    @Override
    public Set<Subscription> loadAllSubscriptions() {
        String base = httpHelper.resolveAccessPath();
        String url = base + subscriptionUrl;
        return httpHelper.loadAllData(url, 40, true,
            new ParameterizedTypeReference<>() {
            });
    }
}
