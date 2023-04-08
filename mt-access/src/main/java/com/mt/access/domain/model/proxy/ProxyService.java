package com.mt.access.domain.model.proxy;

import com.mt.access.application.endpoint.representation.EndpointProxyCacheRepresentation;
import com.mt.access.application.proxy.representation.CheckSumRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProxyService {
    public CheckSumRepresentation checkSumValue() {
        Map<ProxyInfo, CheckSumValue> cacheEndpointSum =
            DomainRegistry.getRemoteProxyService().getCacheEndpointSum();
        Set<Endpoint> allByQuery = QueryUtility.getAllByQuery(
            (query) -> DomainRegistry.getEndpointRepository().endpointsOfQuery(query),
            new EndpointQuery());
        Set<EndpointProxyCacheRepresentation> collect =
            allByQuery.stream().map(EndpointProxyCacheRepresentation::new)
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        EndpointProxyCacheRepresentation.updateDetail(new ArrayList<>(collect));
        CheckSumValue checkSumValue = new CheckSumValue(collect);
        return new CheckSumRepresentation(checkSumValue, cacheEndpointSum);
    }

    public void checkSum() {
        log.debug("[checking proxy cache value] started");
        CheckSumRepresentation checkSumRepresentation = checkSumValue();
        String hostValue = checkSumRepresentation.getHostValue();
        HashSet<String> values = new HashSet<>(checkSumRepresentation.getProxyValue().values());
        if (values.isEmpty()) {
            log.debug("pass check due to no proxy found");
            return;
        }
        if (values.size() != 1) {
            log.debug("failed check due to different proxy value found");
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ProxyCacheCheckFailedEvent());
            return;
        }
        if (!values.stream().findFirst().get().equals(hostValue)) {
            log.debug("failed check due to proxy value not matching host value");
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ProxyCacheCheckFailedEvent());
        }
        log.debug("[checking proxy cache value] completed");
    }
}
