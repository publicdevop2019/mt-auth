package com.mt.common.port.adapter.persistence;

import com.mt.common.port.adapter.persistence.domain_event.SpringDataJpaEventRepository;
import com.mt.common.port.adapter.persistence.idempotent.SpringDataJpaChangeRecordRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonQueryBuilderRegistry {
    @Getter
    private static SpringDataJpaChangeRecordRepository.SpringDataJpaCriteriaApiChangeRecordAdaptor changeRecordQueryBuilder;

    @Autowired
    public void setChangeRecordQueryBuilder(SpringDataJpaChangeRecordRepository.SpringDataJpaCriteriaApiChangeRecordAdaptor changeRecordQueryBuilder) {
        CommonQueryBuilderRegistry.changeRecordQueryBuilder = changeRecordQueryBuilder;
    }
    @Getter
    private static SpringDataJpaEventRepository.JpaCriteriaApiStoredEventQueryAdapter storedEventQueryAdapter;

    @Autowired
    public void setCreateOrderDTXQueryAdapter(SpringDataJpaEventRepository.JpaCriteriaApiStoredEventQueryAdapter storedEventQueryAdapter) {
        CommonQueryBuilderRegistry.storedEventQueryAdapter = storedEventQueryAdapter;
    }
}
