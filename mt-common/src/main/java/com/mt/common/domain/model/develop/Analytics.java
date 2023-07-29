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
     * @param type type of task
     * @return analytics
     */
    public static Analytics start(Type type) {
        Analytics analytics = new Analytics();
        analytics.startAt = Instant.now().toEpochMilli();
        analytics.type = type;
        log.debug("start {} at {}", type.getLabel(), analytics.startAt);
        return analytics;
    }

    public static Analytics start(StoredEvent storedEvent) {
        Analytics analytics = new Analytics();
        analytics.startAt = storedEvent.getTimestamp();
        analytics.type = Type.EVENT_RECEIVED;
        return analytics;
    }

    public void stop() {
        long stopAt = Instant.now().toEpochMilli();
        long elapse = stopAt - startAt;
        if (elapse > type.getBudget()) {
            log.warn("{} exceed budget, took {}", type.getLabel(), prettyTime(elapse));
        }
        log.debug("stop {} at {} start at {} elapsed {}", type.getLabel(), stopAt, startAt,
            elapse);
    }

    public void stopEvent(int channelNumber, String consumerTag, StoredEvent storedEvent) {
        long elapse = Instant.now().toEpochMilli() - startAt;
        if (elapse > type.getBudget()) {
            log.warn(
                "{} exceed budget, took {} details: channel {} tag {} name {} id {}",
                type.getLabel(),
                prettyTime(elapse),
                channelNumber,
                consumerTag,
                storedEvent.getName(),
                storedEvent.getId());
        }
    }

    public String prettyTime(Long milli) {
        long l = Math.floorDiv(milli, 1000L);
        long i = Math.floorMod(milli, 1000L);
        return l + "." + i + "s";
    }

    @Getter
    public enum Type {
        LOCK_ACQUIRE("lock_acquire", 2000L),
        DATA_PERSISTENCE("data_persistence", 3000L),
        JOB_EXECUTION("job_execution", 10000L),
        EVENT_START_PUBLISH("event_start_publish", 500L),
        IDEMPOTENT_CHECK("idempotent_check", 1000L),
        DOMAIN_LOGIC("domain_logic", 3000L),
        HANGING_TX("hanging_tx", 1000L),
        DOMAIN_LOGIC_AND_IDEMPOTENT_ENTITY("domain_logic_and_idempotent_record", 3500L),
        EVENT_RECEIVED("event_received", 4000L),
        EVENTS_PUBLISH("events_publish", 1000L),
        EVENT_PUBLISH_CONFIRM("event_publish_confirm", 500L);
        private final String label;
        private final Long budget;

        Type(String label, Long budget) {
            this.label = label;
            this.budget = budget;
        }
    }
}
