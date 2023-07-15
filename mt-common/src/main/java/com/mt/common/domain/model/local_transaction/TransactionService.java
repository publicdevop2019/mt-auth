package com.mt.common.domain.model.local_transaction;

import com.mt.common.domain.CommonDomainRegistry;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
public class TransactionService {
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    @Qualifier("event-submit")
    private ThreadPoolExecutor eventSubmitExecutor;

    public void transactionalEvent(Consumer<TransactionContext> fn) {
        TransactionContext init = TransactionContext.init();
        TransactionTemplate template = new TransactionTemplate(platformTransactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                fn.accept(init);
            }
        });
        submitEvent(init);
    }

    public <T> T returnedTransactionalEvent(Function<TransactionContext, T> fn) {
        TransactionContext init = TransactionContext.init();
        TransactionTemplate template = new TransactionTemplate(platformTransactionManager);
        T t = template.execute(transactionStatus -> fn.apply(init));
        submitEvent(init);
        return t;
    }

    private void submitEvent(TransactionContext context) {
        eventSubmitExecutor.execute(() -> {
            log.debug("total domain event found {}", context.getEvents().size());
            context.getEvents().forEach(e -> {
                log.trace("publishing event {} with id {}", e.getName(),
                    e.getId());
                CommonDomainRegistry.getEventStreamService().next(e);
            });
        });
    }
}
