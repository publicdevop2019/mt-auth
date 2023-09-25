package com.mt.common.port.adapter.persistence;

import com.mt.common.port.adapter.persistence.job.SpringDataJpaJobRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonQueryBuilderRegistry {
    @Getter
    private static SpringDataJpaJobRepository.JpaCriteriaApiJobAdaptor jobAdaptor;


    @Autowired
    public void setJpaCriteriaApiJobAdaptor(
        SpringDataJpaJobRepository.JpaCriteriaApiJobAdaptor jobAdaptor) {
        CommonQueryBuilderRegistry.jobAdaptor = jobAdaptor;
    }

}
