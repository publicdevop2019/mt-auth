package com.mt.proxy.infrastructure;

import com.mt.proxy.domain.UniqueIdGeneratorService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SnowflakeUniqueIdService implements UniqueIdGeneratorService {
    private static final long INSTANCE_ID_LENGTH = 6L;
    private static final long SEQUENCE_ID_LENGTH = 13L;
    @Value("${mt.misc.instance-id}")
    private Long instanceId;
    private Long sequenceId = 0L;
    private Long lastSuccessSecond = -1L;

    @PostConstruct
    private void validateInstanceId() {
        if (instanceId > ~(-1L << 4L) || instanceId < 0) {
            throw new RuntimeException("invalid instance id");
        }
    }

    public synchronized long id() {
        long currentSecond = getCurrentSecond();
        if (currentSecond < lastSuccessSecond) {
            throw new RuntimeException("clock reverted");
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
