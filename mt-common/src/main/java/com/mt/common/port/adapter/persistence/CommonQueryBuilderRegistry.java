package com.mt.common.port.adapter.persistence;

import com.mt.common.port.adapter.persistence.domain_event.SpringDataJpaDomainEventRepository;
import com.mt.common.port.adapter.persistence.idempotent.SpringDataJpaChangeRecordRepository;
import com.mt.common.port.adapter.persistence.job.SpringDataJpaJobRepository;
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
    @Getter
    private static SpringDataJpaJobRepository.JpaCriteriaApiJobAdaptor jobAdaptor;


    @Autowired
    public void setJpaCriteriaApiJobAdaptor(
        SpringDataJpaJobRepository.JpaCriteriaApiJobAdaptor jobAdaptor) {
        CommonQueryBuilderRegistry.jobAdaptor = jobAdaptor;
    }
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
