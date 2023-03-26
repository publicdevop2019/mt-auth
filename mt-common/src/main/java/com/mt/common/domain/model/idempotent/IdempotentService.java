package com.mt.common.domain.model.idempotent;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.idempotent.CreateChangeRecordCommand;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
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

    public void idempotentMsg(String changeId, Function<CreateChangeRecordCommand, String> function,
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
                log.debug("change already exist, no change will happen");
            } else {
                SumPagedRep<ChangeRecord> forwardChanges =
                    CommonDomainRegistry.getChangeRecordRepository()
                        .changeRecordsOfQuery(ChangeRecordQuery
                            .idempotentQuery(changeId.replace(CANCEL, ""), aggregateName));

                Optional<ChangeRecord> forwardChange = forwardChanges.findFirst();
                if (forwardChange.isPresent()) {
                    log.debug("cancelling change...");
                    function.apply(command);
                    reply.apply(command);
                    CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                        .createReverse(command);
                } else {
                    log.debug("change not found, do empty cancel");
                    command.setEmptyOpt(true);
                    reply.apply(command);
                    //change not found
                    CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                        .createEmptyReverse(command);
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
                    reply.apply(command);
                    CommonDomainRegistry.getDomainEventRepository()
                        .append(new HangingTxDetected(changeId));
                    CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                        .createEmptyForward(command);
                } else {
                    log.debug("making change with {} aggregate {}", command.getChangeId(),
                        aggregateName);
                    function.apply(command);
                    reply.apply(command);
                    CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                        .createForward(command);
                }
            }
        }
    }

    /**
     * make sure change is idempotent, start transaction if change is allowed
     *
     * @param changeId      change id
     * @param function      additional function to be executed
     * @param aggregateName (aggregate name + change id) = change identifier
     * @return new aggregate id if needed
     */
    public String idempotent(String changeId,
                             Function<CreateChangeRecordCommand, String> function,
                             String aggregateName) {
        if (isCancelChange(changeId)) {
            //reverse action
            SumPagedRep<ChangeRecord> reverseChanges =
                CommonDomainRegistry.getChangeRecordRepository().changeRecordsOfQuery(
                    ChangeRecordQuery.idempotentQuery(changeId, aggregateName));
            Optional<ChangeRecord> reverseChange = reverseChanges.findFirst();
            if (reverseChange.isPresent()) {
                log.debug("change already exist, return saved results");
                return reverseChange.get().getReturnValue();
            } else {
                SumPagedRep<ChangeRecord> forwardChanges =
                    CommonDomainRegistry.getChangeRecordRepository().changeRecordsOfQuery(
                        ChangeRecordQuery
                            .idempotentQuery(changeId.replace(CANCEL, ""), aggregateName));

                Optional<ChangeRecord> forwardChange = forwardChanges.findFirst();
                if (forwardChange.isPresent()) {
                    CreateChangeRecordCommand command =
                        createChangeRecordCommand(changeId, aggregateName, null);
                    log.debug("cancelling change...");
                    String apply = function.apply(command);
                    CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                        .createReverse(command);
                    return apply;
                } else {
                    //change not found
                    CreateChangeRecordCommand command =
                        createChangeRecordCommand(changeId, aggregateName, null);
                    CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                        .createEmptyReverse(command);
                    return null;
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
                return forwardChange.get().getReturnValue();
            } else {
                SumPagedRep<ChangeRecord> reverseChanges =
                    CommonDomainRegistry.getChangeRecordRepository().changeRecordsOfQuery(
                        ChangeRecordQuery.idempotentQuery(changeId + CANCEL, aggregateName));
                Optional<ChangeRecord> reverseChange = reverseChanges.findFirst();
                if (reverseChange.isPresent()) {
                    //change has been cancelled, perform null operation
                    log.debug("change already cancelled, do empty change");
                    CommonDomainRegistry.getTransactionService().transactional(() -> {
                        CommonDomainRegistry.getDomainEventRepository()
                            .append(new HangingTxDetected(changeId));
                        CreateChangeRecordCommand command =
                            createChangeRecordCommand(changeId, aggregateName, null);
                        CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                            .createEmptyForward(command);
                    });
                    return null;
                } else {
                    log.debug("making change...");
                    return CommonDomainRegistry.getTransactionService()
                        .returnedTransactional(() -> {
                            CreateChangeRecordCommand command =
                                createChangeRecordCommand(changeId, aggregateName, null);
                            String apply = function.apply(command);
                            CommonApplicationServiceRegistry.getChangeRecordApplicationService()
                                .createForward(command);
                            return apply;
                        });
                }
            }
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
