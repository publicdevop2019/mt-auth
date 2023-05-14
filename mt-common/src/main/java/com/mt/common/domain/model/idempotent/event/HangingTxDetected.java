package com.mt.common.domain.model.idempotent.event;

import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HangingTxDetected extends DomainEvent {
    public static final String HANGING_TX_DETECTED = "hanging_tx_detected";
    public static final String name = "HANGING_TX_DETECTED";
    @Getter
    private String changeId;
    {
        setTopic(HANGING_TX_DETECTED);
        setName(name);
        setDomainId(new AnyDomainId());
    }
    public HangingTxDetected(String changeId) {
        this.changeId = changeId;
        setInternal(false);
    }
}
