package com.mt.common.domain.model.develop;

import static com.mt.common.domain.model.constant.AppInfo.TRACE_ID_LOG;

import com.mt.common.domain.model.domain_event.StoredEvent;
import java.time.Instant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

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

    public static void stopPublish(Long startAt, StoredEvent event, Long sequenceId, boolean isBatch) {
        long stopAt = Instant.now().toEpochMilli();
        long elapse = stopAt - startAt;
        if (elapse > Type.EVENT_PUBLISH_CONFIRM.getBudget()) {
            if (event == null) {
                log.error("{} exceed budget, took {} event detail unknown, sequence id {} start at {} batch {}",
                    Type.EVENT_PUBLISH_CONFIRM.getLabel(),
                    prettyTime(elapse), sequenceId, startAt, isBatch);
            } else {
                String traceId = event.getTraceId();
                MDC.put(TRACE_ID_LOG, traceId);
                log.warn("{} exceed budget, took {} event id {} name {}",
                    Type.EVENT_PUBLISH_CONFIRM.getLabel(),
                    prettyTime(elapse), event.getId(), event.getName());
                MDC.clear();
            }
        }
    }

    public void stop() {
        long stopAt = Instant.now().toEpochMilli();
        long elapsed = stopAt - startAt;
        if (elapsed > type.getBudget()) {
            log.warn("{} exceed budget, took {}", type.getLabel(), prettyTime(elapsed));
        }
        log.debug("stop {} at {} start at {} elapsed {}", type.getLabel(), stopAt, startAt,
            elapsed);
    }

    public static void stopEvent(String consumerTag, StoredEvent storedEvent) {
        Long startAt = storedEvent.getTimestamp();
        long elapsed = Instant.now().toEpochMilli() - startAt;
        if (elapsed > Type.EVENT_RECEIVED.getBudget()) {
            log.warn(
                "{} exceed budget, took {} details: tag {} name {} id {}",
                Type.EVENT_RECEIVED.getLabel(),
                prettyTime(elapsed),
                consumerTag,
                storedEvent.getName(),
                storedEvent.getId());
        }
    }

    public static String prettyTime(Long milli) {
        long l = Math.floorDiv(milli, 1000L);
        long i = Math.floorMod(milli, 1000L);
        return l + "." + i + "s";
    }

    @Getter
    public enum Type {
        LOCK_ACQUIRE("lock_acquire", 2000L),
        DATA_PERSISTENCE("data_persistence", 3000L),
        JOB_EXECUTION("job_execution", 10000L),
        EVENT_START_PUBLISH("event_start_publish", 50L),
        IDEMPOTENT_CHECK("idempotent_check", 1000L),
        DOMAIN_LOGIC("domain_logic", 3000L),
        HANGING_TX("hanging_tx", 1000L),
        DOMAIN_LOGIC_AND_IDEMPOTENT_ENTITY("domain_logic_and_idempotent_record", 3500L),
        EVENT_RECEIVED("event_received", 4000L),
        EVENTS_PUBLISH("events_publish", 1000L),
        EVENT_PUBLISH_CONFIRM("event_publish_confirm", 500L),
        PERMISSION_CHECK("permission_check", 1000L),
        MARK_EVENT("mark_event", 1000L);
        private final String label;
        private final Long budget;

        Type(String label, Long budget) {
            this.label = label;
            this.budget = budget;
        }
    }
}
