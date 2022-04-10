package com.mt.access.application.proxy;

import com.mt.access.application.proxy.representation.CheckSumRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.infrastructure.CleanUpThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public class ProxyApplicationService {
    @Autowired
    CleanUpThreadPoolExecutor taskExecutor;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 180 * 1000)
    protected void checkSum() {
        taskExecutor.execute(() -> CommonDomainRegistry.getSchedulerDistLockService()
            .executeIfLockSuccess("check_sum", 45, (nullValue) -> {
                TransactionTemplate template = new TransactionTemplate(transactionManager);
                template.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(
                        TransactionStatus transactionStatus) {
                        log.debug("[checking proxy cache value] started");
                        DomainRegistry.getProxyService().checkSum();
                        CommonApplicationServiceRegistry.getJobApplicationService()
                            .createOrUpdateJob(JobDetail.proxyValidation());
                        log.debug("[checking proxy cache value] completed");
                    }
                });
            }));
    }

    public CheckSumRepresentation checkSumValue() {
        return DomainRegistry.getProxyService().checkSumValue();
    }
}
