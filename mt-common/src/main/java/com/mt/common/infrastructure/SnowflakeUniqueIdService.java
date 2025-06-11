package com.mt.common.infrastructure;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.unique_id.UniqueIdGeneratorService;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class SnowflakeUniqueIdService implements UniqueIdGeneratorService {
    private static final long INSTANCE_ID_LENGTH = 6L;
    private static final long SEQUENCE_ID_LENGTH = 13L;
    @Getter
    private Long instanceId;
    private Long sequenceId = 0L;
    private Long lastSuccessSecond = -1L;

    public void assignInstanceId(Integer integer) {
        this.instanceId = Long.valueOf(integer);
        validateInstanceId();
    }

    private void validateInstanceId() {
        if (instanceId == null || instanceId > ~(-1L << 4L) || instanceId < 0) {
            throw new DefinedRuntimeException("invalid instance id", "0061",
                HttpResponseCode.NOT_HTTP);
        }
    }

    public synchronized long id() {
        validateInstanceId();
        long currentSecond = getCurrentSecond();
        if (currentSecond < lastSuccessSecond) {
            throw new DefinedRuntimeException("clock reverted", "0062",
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

    @Override
    public String idString() {
        long id = id();
        return Long.toHexString(id);
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
