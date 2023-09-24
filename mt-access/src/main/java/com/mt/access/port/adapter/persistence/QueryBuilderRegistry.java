package com.mt.access.port.adapter.persistence;

import com.mt.access.port.adapter.persistence.client.SpringDataJpaClientRepository;
import com.mt.access.port.adapter.persistence.endpoint.SpringDataJpaEndpointRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderRegistry {
    @Getter
    private static SpringDataJpaClientRepository.JpaCriteriaApiClientAdaptor
        clientSelectQueryBuilder;
    @Getter
    private static SpringDataJpaEndpointRepository.JpaCriteriaApiEndpointAdapter
        endpointQueryBuilder;


    @Autowired
    public void setEndpointQueryBuilder(
        SpringDataJpaEndpointRepository.JpaCriteriaApiEndpointAdapter endpointQueryBuilder) {
        QueryBuilderRegistry.endpointQueryBuilder = endpointQueryBuilder;
    }

    @Autowired
    public void setClientQueryBuilder(
        SpringDataJpaClientRepository.JpaCriteriaApiClientAdaptor clientSelectQueryBuilder) {
        QueryBuilderRegistry.clientSelectQueryBuilder = clientSelectQueryBuilder;
    }
}
