package com.mt.common.domain;


import com.mt.common.domain.model.cache.HibernateCacheService;
import com.mt.common.domain.model.distributed_lock.SchedulerDistLockService;
import com.mt.common.domain.model.domain_event.DomainEventRepository;
import com.mt.common.domain.model.domain_event.SagaEventStreamService;
import com.mt.common.domain.model.idempotent.ChangeRecordRepository;
import com.mt.common.domain.model.notification.PublishedEventTrackerRepository;
import com.mt.common.domain.model.unique_id.UniqueIdGeneratorService;
import com.mt.common.domain.model.serializer.CustomObjectSerializer;
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
    private static SchedulerDistLockService schedulerDistLockService;

    @Autowired
    public void setSchedulerDistLockService(SchedulerDistLockService schedulerDistLockService) {
        CommonDomainRegistry.schedulerDistLockService = schedulerDistLockService;
    }
    @Autowired
    public void setHibernateCacheService(HibernateCacheService hibernateCacheService) {
        CommonDomainRegistry.hibernateCacheService = hibernateCacheService;
    }
    @Autowired
    public void setPublishedEventTrackerRepository(PublishedEventTrackerRepository publishedEventTrackerRepository) {
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
