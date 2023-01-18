package com.mt.common.domain;


import com.mt.common.domain.model.cache.HibernateCacheService;
import com.mt.common.domain.model.constant.ApplicationInfoService;
import com.mt.common.domain.model.job.JobService;
import com.mt.common.domain.model.local_transaction.TransactionService;
import com.mt.common.infrastructure.RedisJobService;
import com.mt.common.domain.model.domain_event.DomainEventRepository;
import com.mt.common.domain.model.domain_event.SagaEventStreamService;
import com.mt.common.domain.model.idempotent.ChangeRecordRepository;
import com.mt.common.domain.model.job.JobRepository;
import com.mt.common.domain.model.notification.PublishedEventTrackerRepository;
import com.mt.common.domain.model.serializer.CustomObjectSerializer;
import com.mt.common.domain.model.unique_id.UniqueIdGeneratorService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonDomainRegistry {
    @Getter
    private static UniqueIdGeneratorService uniqueIdGeneratorService;
    @Getter
    private static CustomObjectSerializer customObjectSerializer;
    @Getter
    private static SagaEventStreamService eventStreamService;
    @Getter
    private static HibernateCacheService hibernateCacheService;
    @Getter
    private static ChangeRecordRepository changeRecordRepository;
    @Getter
    private static DomainEventRepository domainEventRepository;
    @Getter
    private static PublishedEventTrackerRepository publishedEventTrackerRepository;
    @Getter
    private static JobService jobService;
    @Getter
    private static JobRepository jobRepository;
    @Getter
    private static ApplicationInfoService applicationInfoService;
    @Getter
    private static TransactionService transactionService;

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        CommonDomainRegistry.transactionService = transactionService;
    }

    @Autowired
    public void setApplicationInfoService(ApplicationInfoService applicationInfoService) {
        CommonDomainRegistry.applicationInfoService = applicationInfoService;
    }

    @Autowired
    public void setJobRepository(JobRepository jobRepository) {
        CommonDomainRegistry.jobRepository = jobRepository;
    }

    @Autowired
    public void setJobService(JobService jobService) {
        CommonDomainRegistry.jobService = jobService;
    }

    @Autowired
    public void setHibernateCacheService(HibernateCacheService hibernateCacheService) {
        CommonDomainRegistry.hibernateCacheService = hibernateCacheService;
    }

    @Autowired
    public void setPublishedEventTrackerRepository(
        PublishedEventTrackerRepository publishedEventTrackerRepository) {
        CommonDomainRegistry.publishedEventTrackerRepository = publishedEventTrackerRepository;
    }

    @Autowired
    public void setDomainEventRepository(DomainEventRepository domainEventRepository) {
        CommonDomainRegistry.domainEventRepository = domainEventRepository;
    }

    @Autowired
    public void setEventStreamService(SagaEventStreamService eventStreamService) {
        CommonDomainRegistry.eventStreamService = eventStreamService;
    }

    @Autowired
    public void setChangeRecordRepository(ChangeRecordRepository changeRecordRepository) {
        CommonDomainRegistry.changeRecordRepository = changeRecordRepository;
    }

    @Autowired
    public void setCustomObjectSerializer(CustomObjectSerializer customObjectSerializer) {
        CommonDomainRegistry.customObjectSerializer = customObjectSerializer;
    }

    @Autowired
    public void setUniqueIdGeneratorService(UniqueIdGeneratorService uniqueIdGeneratorService) {
        CommonDomainRegistry.uniqueIdGeneratorService = uniqueIdGeneratorService;
    }

}
