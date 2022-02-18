package com.mt.access.application.proxy;

import com.mt.access.application.endpoint.representation.EndpointProxyCardRepresentation;
import com.mt.access.application.proxy.representation.CheckSumRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.proxy.CheckSumValue;
import com.mt.access.domain.model.proxy.ProxyInfo;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class ProxyApplicationService {
    public CheckSumRepresentation checkSumValue() {
        Map<ProxyInfo, CheckSumValue> cacheEndpointSum = DomainRegistry.getProxyService().getCacheEndpointSum();

        Set<Endpoint> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getEndpointRepository().endpointsOfQuery((EndpointQuery) query), new EndpointQuery());
        Set<EndpointProxyCardRepresentation> collect = allByQuery.stream().map(EndpointProxyCardRepresentation::new).collect(Collectors.toSet());
        EndpointProxyCardRepresentation.updateDetail(new ArrayList<>(collect));
        //sort before generate check sum
        TreeSet<EndpointProxyCardRepresentation> endpointProxyCardRepresentations = new TreeSet<>();
        collect.stream().sorted().forEach(endpointProxyCardRepresentations::add);
        CheckSumValue checkSumValue = new CheckSumValue(endpointProxyCardRepresentations);
        return new CheckSumRepresentation(checkSumValue,cacheEndpointSum);
    }
}
