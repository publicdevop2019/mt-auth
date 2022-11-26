package com.mt.proxy.domain;

import com.mt.proxy.domain.rate_limit.Subscription;
import java.util.Set;

public interface RetrieveSubscriptionService {
    Set<Subscription> loadAllSubscriptions();
}
