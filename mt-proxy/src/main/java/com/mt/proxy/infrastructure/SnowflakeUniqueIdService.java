package com.mt.proxy.infrastructure;

import com.mt.proxy.domain.InstanceInfo;
import com.mt.proxy.domain.UniqueIdGeneratorService;
import com.mt.proxy.domain.exception.DefinedRuntimeException;
import com.mt.proxy.domain.exception.HttpResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnowflakeUniqueIdService implements UniqueIdGeneratorService {
    private static final long INSTANCE_ID_LENGTH = 6L;
    private static final long SEQUENCE_ID_LENGTH = 13L;
    @Autowired
    private InstanceInfo instanceInfo;
    private Long sequenceId = 0L;
    private Long lastSuccessSecond = -1L;

    public synchronized long id() {
        validateInstanceId();
        long currentSecond = getCurrentSecond();
        if (currentSecond < lastSuccessSecond) {
            throw new DefinedRuntimeException("clock reverted", "2007",
                HttpResponseCode.INTERNAL_SERVER_ERROR);
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
        Integer instanceId = instanceInfo.getId();
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

    private void validateInstanceId() {
        Integer instanceId = instanceInfo.getId();
        if (instanceId == null || instanceId > ~(-1L << 4L) || instanceId < 0) {
            throw new DefinedRuntimeException("invalid instance id", "2008",
                HttpResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    private long getCurrentSecond() {
        return System.currentTimeMillis() / 1000L;
    }
}
