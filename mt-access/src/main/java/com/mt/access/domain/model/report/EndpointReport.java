package com.mt.access.domain.model.report;

import com.mt.access.domain.model.endpoint.EndpointId;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

@Getter
public class EndpointReport {
    private final EndpointId endpointId;
    private long averageRoundTimeInSeconds;
    private final int totalInvokeCount;
    private final AtomicInteger failureResponseRate = new AtomicInteger(0);
    private final AtomicInteger badRequestCount = new AtomicInteger(0);//400
    private final AtomicInteger unauthorizedRequestCount = new AtomicInteger(0);//401
    private final AtomicInteger authenticationRequiredRequestCount = new AtomicInteger(0);//403
    private final AtomicInteger internalServerErrorCount = new AtomicInteger(0);//500
    private final AtomicInteger serviceUnavailableErrorCount = new AtomicInteger(0);//503
    private int averageResponseSize;

    public EndpointReport(Set<FormattedAccessRecord> records, EndpointId endpointId) {
        this.endpointId = endpointId;
        Optional<Long> reduce = records.stream().map(e ->
            e.getResponseAt().toInstant().getEpochSecond() - e.getRequestAt().toInstant().getEpochSecond()).reduce((a, b) -> (a + b) >> 1);
        reduce.ifPresent(e -> averageRoundTimeInSeconds = e);
        Optional<Integer> reduce1 = records.stream().map(
            FormattedAccessRecord::getResponseContentSize).reduce((a, b) -> (a + b) >> 1);
        reduce1.ifPresent(e -> averageResponseSize = e);
        totalInvokeCount = records.size();
        records.forEach(e -> {
            if (e.getResponseCode() != 200) {
                failureResponseRate.getAndIncrement();
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
