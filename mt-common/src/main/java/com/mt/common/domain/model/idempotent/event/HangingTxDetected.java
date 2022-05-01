package com.mt.common.domain.model.idempotent.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HangingTxDetected extends DomainEvent {
    public static final String MONITOR_TOPIC = "monitor";
    public static final String HANGING_TX_DETECTED = "HANGING_TX_DETECTED";
    @Getter
    private String changeId;

    public HangingTxDetected(String changeId) {
        this.changeId = changeId;
        setInternal(false);
        setTopic(MONITOR_TOPIC);
        setName(HANGING_TX_DETECTED);
    }
}
