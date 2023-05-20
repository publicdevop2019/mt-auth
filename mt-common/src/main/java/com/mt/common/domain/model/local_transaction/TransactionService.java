package com.mt.common.domain.model.local_transaction;

import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class TransactionService {
    @Autowired
    private PlatformTransactionManager transactionManager;

    public void transactional(Runnable fn) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(
                TransactionStatus transactionStatus) {
                fn.run();
            }
        });
    }
    public <T> T returnedTransactional(Supplier<T> fn) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        return template.execute(transactionStatus -> fn.get());
    }
}
