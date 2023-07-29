package com.mt.common.domain.model.idempotent;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.develop.Analytics;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IdempotentService {

    /**
     * used in message processing context when messages are possible conflict with each other.
     * only one of forward and backward change can execute successfully
     * <p>
     * identical to idempotent except:
     * <ul>
     * <li>does not allow return value</li>
     * <li>has reply call back</li>
     * </ul>
     * start transaction if change is allowed
     * <p>
     * reply is not executed in transactions
     *
     * @param changeId  change id
     * @param function  additional function to be executed
     * @param reply     reply if execute success or already executed
     * @param aggregate (aggregate name + change id) = change identifier
     */
    public void idempotentMsg(String changeId, Function<TransactionContext, String> function,
                              Function<ChangeRecord, String> reply,
                              String aggregate) {
        if (!ChangeRecord.isBackwardChange(changeId)) {
            //forward change
            Analytics idempotentAnalytics = Analytics.start(Analytics.Type.IDEMPOTENT_CHECK);
            Optional<ChangeRecord> forwardChange =
                CommonDomainRegistry.getChangeRecordRepository().internalQuery(changeId, aggregate);
            idempotentAnalytics.stop();
            if (forwardChange.isPresent()) {
                if (Checker.isTrue(forwardChange.get().getEmptyOpt())) {
                    log.debug("change already empty cancelled");
                } else {
                    log.debug("change already exist");
                }
                reply.apply(forwardChange.get());
            } else {
                //new change
                log.debug("making change with id {} aggregate {}", changeId, aggregate);
                AtomicReference<ChangeRecord> changeRecord = new AtomicReference<>();
                CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
                    ChangeRecord changeRecord1 =
                        CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                            .saveChange(changeId, aggregate);
                    changeRecord.set(changeRecord1);
                    context.setChangeRecord(changeRecord1);
                    Analytics domainAnalytics = Analytics.start(Analytics.Type.DOMAIN_LOGIC);
                    function.apply(context);
                    domainAnalytics.stop();
                });
                reply.apply(changeRecord.get());
            }
        } else {
            //backward change
            Analytics idempotentAnalytics = Analytics.start(Analytics.Type.IDEMPOTENT_CHECK);
            Optional<ChangeRecord> backwardChange =
                CommonDomainRegistry.getChangeRecordRepository().internalQuery(changeId, aggregate);
            if (backwardChange.isPresent()) {
                idempotentAnalytics.stop();
                log.debug("change already cancelled");
            } else {
                Optional<ChangeRecord> forwardChange =
                    CommonDomainRegistry.getChangeRecordRepository()
                        .internalQuery(ChangeRecord.getForwardChangeId(changeId), aggregate);
                idempotentAnalytics.stop();
                AtomicReference<ChangeRecord> cancelChangeRecordRef =
                    new AtomicReference<>();
                CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
                    ChangeRecord changeRecord;
                    if (forwardChange.isPresent()) {
                        //cancel an existing forward change
                        log.debug("cancelling change");
                        changeRecord =
                            CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                                .saveChange(changeId, aggregate);
                    } else {
                        //cancel none existing forward change
                        log.debug("hanging change found, do empty actions");
                        changeRecord =
                            CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                                .saveEmptyChange(changeId, aggregate);
                        context.append(new HangingTxDetected(changeId));
                    }
                    cancelChangeRecordRef.set(changeRecord);
                    context.setChangeRecord(changeRecord);
                    Analytics domainAnalytics = Analytics.start(Analytics.Type.DOMAIN_LOGIC);
                    function.apply(context);
                    domainAnalytics.stop();
                });
                reply.apply(cancelChangeRecordRef.get());
            }

        }
    }

    /**
     * make sure change is idempotent, start transaction if change is allowed
     * <p>
     * note: this is not safe for cancel changes, like create and delete
     * <p>
     * for change that is eligible to cancel, use idempotentMsg
     *
     * @param changeId      change id
     * @param domainLogic   additional function to be executed
     * @param aggregateName (aggregate name + change id) = change identifier
     * @return new aggregate id if needed
     */
    public String idempotent(String changeId,
                             Function<TransactionContext, String> domainLogic,
                             String aggregateName) {
        Analytics analytics = Analytics.start(Analytics.Type.IDEMPOTENT_CHECK);
        Optional<ChangeRecord> changeRecord =
            CommonDomainRegistry.getChangeRecordRepository().query(
                ChangeRecordQuery.idempotentQuery(changeId, aggregateName)).findFirst();
        analytics.stop();
        if (changeRecord.isPresent()) {
            log.debug("change already exist, return saved result");
            return changeRecord.get().getReturnValue();
        } else {
            log.debug("making change");
            return CommonDomainRegistry.getTransactionService()
                .returnedTransactionalEvent((context) -> {
                    ChangeRecord record =
                        CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                            .saveChange(changeId, aggregateName);
                    context.setChangeRecord(record);
                    Analytics domainAnalytics = Analytics.start(Analytics.Type.DOMAIN_LOGIC);
                    String apply = domainLogic.apply(context);
                    domainAnalytics.stop();
                    return apply;
                });
        }
    }
}
