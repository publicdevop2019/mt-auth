package com.mt.access.domain.model.proxy;

import com.mt.access.application.endpoint.representation.EndpointProxyCardRepresentation;
import com.mt.access.application.proxy.representation.CheckSumRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class ProxyService {
    public CheckSumRepresentation checkSumValue() {
        Map<ProxyInfo, CheckSumValue> cacheEndpointSum = DomainRegistry.getRemoteProxyService().getCacheEndpointSum();

        Set<Endpoint> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getEndpointRepository().endpointsOfQuery((EndpointQuery) query), new EndpointQuery());
        Set<EndpointProxyCardRepresentation> collect = allByQuery.stream().map(EndpointProxyCardRepresentation::new).collect(Collectors.toSet());
        EndpointProxyCardRepresentation.updateDetail(new ArrayList<>(collect));
        //sort before generate check sum
        TreeSet<EndpointProxyCardRepresentation> endpointProxyCardRepresentations = new TreeSet<>();
        collect.stream().sorted().forEach(endpointProxyCardRepresentations::add);
        CheckSumValue checkSumValue = new CheckSumValue(endpointProxyCardRepresentations);
        return new CheckSumRepresentation(checkSumValue, cacheEndpointSum);
    }

    public void checkSum() {
        CheckSumRepresentation checkSumRepresentation = checkSumValue();
        String hostValue = checkSumRepresentation.getHostValue();
        HashSet<String> strings = new HashSet<>(checkSumRepresentation.getProxyValue().values());
        if (strings.isEmpty()) {
            return;
        }
        if (strings.size() != 1) {
            CommonDomainRegistry.getDomainEventRepository().append(new ProxyCacheCheckFailedEvent());
            return;
        }
        if (!strings.stream().findFirst().get().equals(hostValue)) {
            CommonDomainRegistry.getDomainEventRepository().append(new ProxyCacheCheckFailedEvent());
        }

    }
}
