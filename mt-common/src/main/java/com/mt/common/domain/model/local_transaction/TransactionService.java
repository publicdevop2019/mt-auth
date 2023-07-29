package com.mt.common.domain.model.local_transaction;

import static com.mt.common.domain.model.constant.AppInfo.TRACE_ID_LOG;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.develop.Analytics;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
    @Qualifier("event-pub")
    private ThreadPoolExecutor eventSubmitExecutor;

    public void transactionalEvent(Consumer<TransactionContext> fn) {
        TransactionContext init = TransactionContext.init();
        TransactionTemplate template = new TransactionTemplate(platformTransactionManager);
        AtomicReference<Analytics> persistAnalytics = new AtomicReference<>();
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Analytics wrapperAnalytics =
                    Analytics.start(Analytics.Type.DOMAIN_LOGIC_AND_IDEMPOTENT_ENTITY);
                fn.accept(init);
                wrapperAnalytics.stop();
                persistAnalytics.set(Analytics.start(Analytics.Type.DATA_PERSISTENCE));
            }
        });
        persistAnalytics.get().stop();
        submitEvent(init);
    }

    public <T> T returnedTransactionalEvent(Function<TransactionContext, T> domainLogicWrapper) {
        TransactionContext init = TransactionContext.init();
        TransactionTemplate template = new TransactionTemplate(platformTransactionManager);
        AtomicReference<Analytics> persistAnalytics = new AtomicReference<>();
        T t = template.execute(transactionStatus -> {
            Analytics wrapperAnalytics =
                Analytics.start(Analytics.Type.DOMAIN_LOGIC_AND_IDEMPOTENT_ENTITY);
            T apply = domainLogicWrapper.apply(init);
            wrapperAnalytics.stop();
            persistAnalytics.set(Analytics.start(Analytics.Type.DATA_PERSISTENCE));
            return apply;
        });
        persistAnalytics.get().stop();
        submitEvent(init);
        return t;
    }

    private void submitEvent(TransactionContext context) {
        if (context.getEvents().isEmpty()) {
            log.trace("skip publishing event since no event found");
            return;
        }
        log.debug("submitted event publish job");
        if (log.isTraceEnabled()) {
            int size = eventSubmitExecutor.getQueue().size();
            int activeCount = eventSubmitExecutor.getActiveCount();
            long completedTaskCount = eventSubmitExecutor.getCompletedTaskCount();
            int poolSize = eventSubmitExecutor.getPoolSize();
            log.trace(
                "event submit pool status, queue size {} active count {} completed task count {} pool size {}",
                size, activeCount, completedTaskCount, poolSize);
        }
        Analytics start = Analytics.start(Analytics.Type.EVENT_START_PUBLISH);
        eventSubmitExecutor.execute(() -> {
            MDC.put(TRACE_ID_LOG, context.getEvents().get(0).getTraceId());
            log.debug("submitting event publish job");
            start.stop();
            Analytics analytics = Analytics.start(Analytics.Type.EVENTS_PUBLISH);
            log.debug("total domain event found {}", context.getEvents().size());
            context.getEvents().forEach(e -> {
                log.trace("publishing event {} with id {}", e.getName(),
                    e.getId());
                CommonDomainRegistry.getEventStreamService().next(e);
            });
            analytics.stop();
        });
    }
}
