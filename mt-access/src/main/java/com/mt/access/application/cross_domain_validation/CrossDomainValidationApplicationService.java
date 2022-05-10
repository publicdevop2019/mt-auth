package com.mt.access.application.cross_domain_validation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cross_domain_validation.ValidationResult;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.infrastructure.CleanUpThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CrossDomainValidationApplicationService {
    @Autowired
    CleanUpThreadPoolExecutor taskExecutor;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Scheduled(fixedRate = 1 * 60 * 1000, initialDelay = 60 * 1000)
    public void validate() {
        taskExecutor.execute(() -> CommonDomainRegistry.getSchedulerDistLockService()
            .executeIfLockSuccess("validation_task", 15, (nullValue) -> {
                TransactionTemplate template = new TransactionTemplate(transactionManager);
                template.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(
                        TransactionStatus transactionStatus) {
                        DomainRegistry.getCrossDomainValidationService().validate();
                    }
                });
            }));
    }

    @Transactional
    public void reset() {
        DomainRegistry.getValidationResultRepository().get().ifPresent(
            ValidationResult::resetFailureCount);
    }
}
