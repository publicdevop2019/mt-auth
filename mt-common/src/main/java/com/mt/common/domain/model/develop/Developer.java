package com.mt.common.domain.model.develop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class Developer {
    public static void checkTransactional() {
        boolean actualTransactionActive =
            TransactionSynchronizationManager.isActualTransactionActive();
        log.info("transaction is {}", actualTransactionActive ? "active" : "inactivate");
    }
}
