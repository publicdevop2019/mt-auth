package com.mt.common.domain.model.local_transaction;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.idempotent.ChangeRecord;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Getter
public class TransactionContext {
    List<StoredEvent> events = new ArrayList<>();
    ChangeRecord changeRecord;

    public static TransactionContext init() {
        return new TransactionContext();
    }

    public void append(DomainEvent domainEvent) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new DefinedRuntimeException("no transaction detected", "0063",
                HttpResponseCode.INTERNAL_SERVER_ERROR);
        }
        StoredEvent storedEvent = new StoredEvent(domainEvent);
        CommonDomainRegistry.getDomainEventRepository().append(storedEvent);
        events.add(storedEvent);
    }

    public void setChangeRecord(ChangeRecord changeRecord) {
        this.changeRecord = changeRecord;
    }
}
