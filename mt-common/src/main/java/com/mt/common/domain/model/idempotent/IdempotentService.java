package com.mt.common.domain.model.idempotent;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.idempotent.CreateChangeRecordCommand;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IdempotentService {
    private static final String CANCEL = "_cancel";

    /**
     * <p>used in message processing context when messages are possible conflict with each other</p>
     * <p>identical to idempotent except:</p>
     * <p>1. does not allow return value</p>
     * <p>2. has reply call back</p>
     * <p>start transaction if change is allowed</p>
     * <p>reply is not executed in transactions</p>
     *
     * @param changeId      change id
     * @param function      additional function to be executed
     * @param reply         reply if execute success or already executed
     * @param aggregateName (aggregate name + change id) = change identifier
     */
    public void idempotentMsg(String changeId, Function<TransactionContext, String> function,
                              Function<CreateChangeRecordCommand, String> reply,
                              String aggregateName) {
        CreateChangeRecordCommand command =
            createChangeRecordCommand(changeId, aggregateName, null);
        if (isCancelChange(changeId)) {
            //reverse action
            SumPagedRep<ChangeRecord> reverseChanges =
                CommonDomainRegistry.getChangeRecordRepository().changeRecordsOfQuery(
                    ChangeRecordQuery.idempotentQuery(changeId, aggregateName));
            Optional<ChangeRecord> reverseChange = reverseChanges.findFirst();
            if (reverseChange.isPresent()) {
                log.debug("change already cancelled, no change will happen");
            } else {
                SumPagedRep<ChangeRecord> forwardChanges =
                    CommonDomainRegistry.getChangeRecordRepository()
                        .changeRecordsOfQuery(ChangeRecordQuery
                            .idempotentQuery(changeId.replace(CANCEL, ""), aggregateName));

                Optional<ChangeRecord> forwardChange = forwardChanges.findFirst();
                if (forwardChange.isPresent()) {
                    log.debug("cancelling change...");
                    CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
                        context.setChangeRecord(command);
                        function.apply(context);
                        CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                            .createReverse(command);
                    });
                    reply.apply(command);
                } else {
                    log.debug("change not found, do empty cancel");
                    command.setEmptyOpt(true);
                    CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
                        //change not found
                        CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                            .createEmptyReverse(command);
                        //create empty forward change for concurrent safe and hanging transaction safe
                        CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                            .createEmptyForward(command);
                    });
                    reply.apply(command);
                }
            }
        } else {
            //forward action
            SumPagedRep<ChangeRecord> forwardChanges =
                CommonDomainRegistry.getChangeRecordRepository().changeRecordsOfQuery(
                    ChangeRecordQuery.idempotentQuery(changeId, aggregateName));
            Optional<ChangeRecord> forwardChange = forwardChanges.findFirst();
            if (forwardChange.isPresent()) {
                log.debug("change already exist, return saved results");
            } else {
                SumPagedRep<ChangeRecord> reverseChanges =
                    CommonDomainRegistry.getChangeRecordRepository().changeRecordsOfQuery(
                        ChangeRecordQuery.idempotentQuery(changeId + CANCEL, aggregateName));
                Optional<ChangeRecord> reverseChange = reverseChanges.findFirst();
                if (reverseChange.isPresent()) {
                    //change has been cancelled, perform null operation
                    log.debug("change already cancelled, do empty change");
                    command.setEmptyOpt(true);
                    CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
                        context.append(new HangingTxDetected(changeId));
                        CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                            .createEmptyForward(command);
                    });
                    reply.apply(command);
                } else {
                    log.debug("making change with {} aggregate {}", command.getChangeId(),
                        aggregateName);
                    CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
                        function.apply(context);
                        CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                            .createForward(command);
                    });
                    reply.apply(command);
                }
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
     * @param function      additional function to be executed
     * @param aggregateName (aggregate name + change id) = change identifier
     * @return new aggregate id if needed
     */
    public String idempotent(String changeId,
                             Function<TransactionContext, String> function,
                             String aggregateName) {
        Optional<ChangeRecord> changeRecord =
            CommonDomainRegistry.getChangeRecordRepository().changeRecordsOfQuery(
                ChangeRecordQuery.idempotentQuery(changeId, aggregateName)).findFirst();
        if (changeRecord.isPresent()) {
            log.debug("change already exist, return saved result");
            return changeRecord.get().getReturnValue();
        } else {
            log.debug("making change...");
            return CommonDomainRegistry.getTransactionService()
                .returnedTransactionalEvent((context) -> {
                    CreateChangeRecordCommand command =
                        createChangeRecordCommand(changeId, aggregateName, null);
                    String apply = function.apply(context);
                    context.setChangeRecord(command);
                    CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                        .createForward(command);
                    return apply;
                });
        }
    }

    private boolean isCancelChange(String changeId) {
        return changeId.contains("_cancel");
    }

    private CreateChangeRecordCommand createChangeRecordCommand(String changeId,
                                                                String aggregateName,
                                                                @Nullable String returnValue) {
        CreateChangeRecordCommand changeRecord = new CreateChangeRecordCommand();
        changeRecord.setChangeId(changeId);
        changeRecord.setAggregateName(aggregateName);
        changeRecord.setReturnValue(returnValue);
        return changeRecord;
    }

}
