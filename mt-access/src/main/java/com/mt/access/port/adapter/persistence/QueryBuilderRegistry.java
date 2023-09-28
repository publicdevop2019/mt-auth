package com.mt.access.port.adapter.persistence;

import com.mt.access.port.adapter.persistence.client.SpringDataJpaClientRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderRegistry {
    @Getter
    private static SpringDataJpaClientRepository.JpaCriteriaApiClientAdaptor
        clientSelectQueryBuilder;

    @Autowired
    public void setClientQueryBuilder(
        SpringDataJpaClientRepository.JpaCriteriaApiClientAdaptor clientSelectQueryBuilder) {
        QueryBuilderRegistry.clientSelectQueryBuilder = clientSelectQueryBuilder;
    }
}
