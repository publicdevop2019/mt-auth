package com.mt.access.domain.model.report;

import com.mt.access.domain.model.endpoint.EndpointId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class EndpointReport {
    private final EndpointId endpointId;
    private final Integer totalInvokeCount;
    private final AtomicInteger failureResponseCount = new AtomicInteger(0);
    private final AtomicInteger badRequestCount = new AtomicInteger(0);//400
    private final AtomicInteger unauthorizedRequestCount = new AtomicInteger(0);//401
    private final AtomicInteger notModifiedRequestCount = new AtomicInteger(0);//304
    private final AtomicInteger authenticationRequiredRequestCount = new AtomicInteger(0);//403
    private final AtomicInteger internalServerErrorCount = new AtomicInteger(0);//500
    private final AtomicInteger serviceUnavailableErrorCount = new AtomicInteger(0);//503
    private final Map<Integer, AtomicInteger> statusCodeMap = new HashMap<>();//503
    private Long averageSuccessRoundTimeInMili;
    private Integer averageResponseSize;

    public EndpointReport(Set<FormattedAccessRecord> records, EndpointId endpointId) {
        this.endpointId = endpointId;
        Set<FormattedAccessRecord> successRecords =
            records.stream().filter(e -> e.getResponseCode() == 200).collect(Collectors.toSet());
        Optional<Long> reduce = successRecords.stream().map(e ->
            e.getResponseAt() -
                e.getRequestAt()).reduce((a, b) -> (a + b) >> 1);
        reduce.ifPresent(e -> averageSuccessRoundTimeInMili = e);
        Optional<Integer> reduce1 = successRecords.stream().map(
            FormattedAccessRecord::getResponseContentSize).reduce((a, b) -> (a + b) >> 1);
        reduce1.ifPresent(e -> averageResponseSize = e);
        totalInvokeCount = records.size();
        records.forEach(e -> {
            if (e.getResponseCode() != 200) {
                failureResponseCount.getAndIncrement();
                if (e.getResponseCode() == 400) {
                    badRequestCount.getAndIncrement();
                } else if (e.getResponseCode() == 401) {
                    unauthorizedRequestCount.getAndIncrement();
                } else if (e.getResponseCode() == 304) {
                    notModifiedRequestCount.getAndIncrement();
                } else if (e.getResponseCode() == 403) {
                    authenticationRequiredRequestCount.getAndIncrement();
                } else if (e.getResponseCode() == 500) {
                    internalServerErrorCount.getAndIncrement();
                } else if (e.getResponseCode() == 503) {
                    serviceUnavailableErrorCount.getAndIncrement();
                }
            }
            AtomicInteger atomicInteger = statusCodeMap.get(e.getResponseCode());
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger(1);
                statusCodeMap.put(e.getResponseCode(), atomicInteger);
            } else {
                atomicInteger.getAndIncrement();
            }
        });
    }
}
