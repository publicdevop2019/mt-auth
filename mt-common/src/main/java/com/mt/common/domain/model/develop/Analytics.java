package com.mt.common.domain.model.develop;

import com.mt.common.domain.model.domain_event.StoredEvent;
import java.time.Instant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Analytics {
    private Long startAt;
    private Type type;

    private Analytics() {
    }

    /**
     * start a new analytics session
     *
     * @param taskName type of task
     * @return analytics
     */
    public static Analytics start(Type taskName) {
        Analytics analytics = new Analytics();
        analytics.startAt = Instant.now().toEpochMilli();
        analytics.type = taskName;
        return analytics;
    }

    public static Analytics start(StoredEvent storedEvent) {
        Analytics analytics = new Analytics();
        analytics.startAt = storedEvent.getTimestamp();
        analytics.type = Type.EVENT_DELIVER;
        return analytics;
    }

    public void stop() {
        long elapse = Instant.now().toEpochMilli() - startAt;
        if (elapse > type.getBudget()) {
            log.warn("{} exceed budget, took {}", type.getLabel(), elapse);
        }
    }

    public void stopEvent(int channelNumber, String consumerTag, StoredEvent storedEvent) {
        long elapse = Instant.now().toEpochMilli() - startAt;
        if (elapse > type.getBudget()) {
            log.warn(
                "{} exceed budget, took {} details: channel {} tag {} name {} id {}",
                type.getLabel(),
                elapse,
                channelNumber,
                consumerTag,
                storedEvent.getName(),
                storedEvent.getId());
        }
    }

    @Getter
    public enum Type {
        LOCK_ACQUIRE("lock_acquire", 2000L),
        DATA_PERSISTENCE("data_persistence", 3000L),
        JOB_EXECUTION("job_execution", 10000L),
        EVENT_EMIT("event_emit", 500L),
        IDEMPOTENT_CHECK("idempotent_check", 1000L),
        DOMAIN_LOGIC("domain_logic", 3000L),
        HANGING_TX("hanging_tx", 1000L),
        DOMAIN_LOGIC_AND_IDEMPOTENT_ENTITY("domain_logic_and_idempotent_record", 3500L),
        EVENT_DELIVER("event_deliver", 4000L),
        EVENT_PUBLISH_CONFIRM("event_publish_confirm", 500L);
        private final String label;
        private final Long budget;

        Type(String label, Long budget) {
            this.label = label;
            this.budget = budget;
        }
    }
}
