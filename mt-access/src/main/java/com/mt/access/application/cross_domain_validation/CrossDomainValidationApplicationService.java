package com.mt.access.application.cross_domain_validation;

import static com.mt.access.infrastructure.AppConstant.DATA_VALIDATION_JOB_NAME;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cross_domain_validation.ValidationResult;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CrossDomainValidationApplicationService {

    @Scheduled(cron = "0 * * ? * *")
    public void validate() {
        log.trace("triggered scheduled task 1");
        CommonDomainRegistry.getJobService()
            .execute(DATA_VALIDATION_JOB_NAME,
                (context) -> DomainRegistry.getCrossDomainValidationService().validate(context),
                true, 1);
    }

    public void reset() {
        CommonDomainRegistry.getTransactionService().transactionalEvent(context -> {
            DomainRegistry.getValidationResultRepository().query().ifPresent(
                ValidationResult::resetFailureCount);
        });
    }
}
