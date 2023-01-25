package com.mt.access.domain.model.report;

import com.mt.access.domain.model.endpoint.EndpointId;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class EndpointReport {
    private final EndpointId endpointId;
    private long averageSuccessRoundTimeInMili;
    private final int totalInvokeCount;
    private final AtomicInteger failureResponseCount = new AtomicInteger(0);
    private final AtomicInteger badRequestCount = new AtomicInteger(0);//400
    private final AtomicInteger unauthorizedRequestCount = new AtomicInteger(0);//401
    private final AtomicInteger authenticationRequiredRequestCount = new AtomicInteger(0);//403
    private final AtomicInteger internalServerErrorCount = new AtomicInteger(0);//500
    private final AtomicInteger serviceUnavailableErrorCount = new AtomicInteger(0);//503
    private int averageResponseSize;

    public EndpointReport(Set<FormattedAccessRecord> records, EndpointId endpointId) {
        this.endpointId = endpointId;
        Set<FormattedAccessRecord> successRecords =
            records.stream().filter(e -> e.getResponseCode() == 200).collect(Collectors.toSet());
        Optional<Long> reduce = successRecords.stream().map(e ->
            e.getResponseAt().toInstant().toEpochMilli() - e.getRequestAt().toInstant().toEpochMilli()).reduce((a, b) -> (a + b) >> 1);
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
                } else if (e.getResponseCode() == 403) {
                    authenticationRequiredRequestCount.getAndIncrement();
                } else if (e.getResponseCode() == 500) {
                    internalServerErrorCount.getAndIncrement();
                } else if (e.getResponseCode() == 503) {
                    serviceUnavailableErrorCount.getAndIncrement();
                } else {
                    throw new IllegalArgumentException(
                        "unknown response code " + e.getResponseCode());
                }
            }
        });
    }
}
