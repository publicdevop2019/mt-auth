package com.mt.common.domain;


import com.mt.common.domain.model.domain_event.DomainEventRepository;
import com.mt.common.domain.model.domain_event.SagaEventStreamService;
import com.mt.common.domain.model.idempotent.ChangeRecordRepository;
import com.mt.common.domain.model.job.DistributedJobService;
import com.mt.common.domain.model.job.JobRepository;
import com.mt.common.domain.model.local_transaction.TransactionService;
import com.mt.common.domain.model.logging.LogService;
import com.mt.common.domain.model.serializer.CustomObjectSerializer;
import com.mt.common.domain.model.unique_id.UniqueIdGeneratorService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private static ChangeRecordRepository changeRecordRepository;
    @Getter
    private static DomainEventRepository domainEventRepository;
    @Getter
    private static DistributedJobService jobService;
    @Getter
    private static JobRepository jobRepository;
    @Getter
    private static TransactionService transactionService;
    @Getter
    private static LogService logService;
    @Getter
    private static JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        CommonDomainRegistry.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        CommonDomainRegistry.transactionService = transactionService;
    }

    @Autowired
    public void setLogService(LogService logService) {
        CommonDomainRegistry.logService = logService;
    }

    @Autowired
    public void setJobRepository(JobRepository jobRepository) {
        CommonDomainRegistry.jobRepository = jobRepository;
    }

    @Autowired
    public void setJobService(DistributedJobService jobService) {
        CommonDomainRegistry.jobService = jobService;
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
