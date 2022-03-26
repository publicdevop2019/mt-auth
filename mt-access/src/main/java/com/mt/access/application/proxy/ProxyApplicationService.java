package com.mt.access.application.proxy;

import com.mt.access.application.proxy.representation.CheckSumRepresentation;
import com.mt.access.domain.DomainRegistry;

import com.mt.common.infrastructure.CleanUpThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public class ProxyApplicationService {
    @Autowired
    CleanUpThreadPoolExecutor taskExecutor;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Scheduled(fixedRate = 10 * 1000, initialDelay = 30 * 1000)
//    @Scheduled(fixedRate = 60 * 1000, initialDelay = 180 * 1000)
    @Transactional
    protected void checkSum() {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                TransactionTemplate template = new TransactionTemplate(transactionManager);
                template.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                        log.debug("start of checking proxy cache value");
                        DomainRegistry.getProxyService().checkSum();
                        log.debug("end of checking proxy cache value");
                    }
                });
            }
        });
    }

    public CheckSumRepresentation checkSumValue() {
        return DomainRegistry.getProxyService().checkSumValue();
    }
}
