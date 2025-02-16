package com.mt.common.domain.model.unique_id;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {
    private static final long INSTANCE_ID_LENGTH = 6L;
    private static final long SEQUENCE_ID_LENGTH = 13L;
    @Value("${mt.misc.instance-id}")
    private Long instanceId;
    private Long sequenceId = 0L;
    private Long lastSuccessSecond = -1L;

    @PostConstruct
    private void validateInstanceId() {
        if (instanceId > ~(-1L << 4L) || instanceId < 0) {
            throw new DefinedRuntimeException("invalid instance id", "0034",
                HttpResponseCode.NOT_HTTP);
        }
    }

    public synchronized long id() {
        long currentSecond = getCurrentSecond();
        if (currentSecond < lastSuccessSecond) {
            throw new DefinedRuntimeException("clock reverted", "0035",
                HttpResponseCode.NOT_HTTP);
        }
        if (lastSuccessSecond == currentSecond) {
            long sequenceMaxValue = ~(-1L << SEQUENCE_ID_LENGTH);
            sequenceId = (sequenceId + 1) & sequenceMaxValue;
            if (sequenceId == 0) {
                currentSecond = waitForNextSecond(lastSuccessSecond);
            }
        } else {
            sequenceId = 0L;
        }
        lastSuccessSecond = currentSecond;
        return (currentSecond << (INSTANCE_ID_LENGTH + SEQUENCE_ID_LENGTH))
            | (instanceId << SEQUENCE_ID_LENGTH)
            | sequenceId;
    }

    private long waitForNextSecond(long lastTimestamp) {
        long timestamp = getCurrentSecond();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentSecond();
        }
        return timestamp;
    }

    private long getCurrentSecond() {
        return System.currentTimeMillis() / 1000L;
    }
}