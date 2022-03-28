package com.mt.common.port.adapter.persistence;

import com.mt.common.port.adapter.persistence.domain_event.SpringDataJpaDomainEventRepository;
import com.mt.common.port.adapter.persistence.idempotent.SpringDataJpaChangeRecordRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonQueryBuilderRegistry {
    @Getter
    private static SpringDataJpaChangeRecordRepository.SpringDataJpaCriteriaApiChangeRecordAdaptor
        changeRecordQueryBuilder;
    @Getter
    private static SpringDataJpaDomainEventRepository.JpaCriteriaApiStoredEventQueryAdapter
        storedEventQueryAdapter;

    @Autowired
    public void setChangeRecordQueryBuilder(
        SpringDataJpaChangeRecordRepository.SpringDataJpaCriteriaApiChangeRecordAdaptor
            changeRecordQueryBuilder) {
        CommonQueryBuilderRegistry.changeRecordQueryBuilder = changeRecordQueryBuilder;
    }

    @Autowired
    public void setCreateOrderDtxQueryAdapter(
        SpringDataJpaDomainEventRepository.JpaCriteriaApiStoredEventQueryAdapter
            storedEventQueryAdapter) {
        CommonQueryBuilderRegistry.storedEventQueryAdapter = storedEventQueryAdapter;
    }
}
