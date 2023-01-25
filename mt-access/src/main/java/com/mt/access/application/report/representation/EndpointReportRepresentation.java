package com.mt.access.application.report.representation;

import com.mt.access.domain.model.report.EndpointReport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Data;

@Data
public class EndpointReportRepresentation {
    private String endpointId;
    private String averageRoundTimeInMili;
    private String totalInvokeCount;
    private String failureResponseRate;
    private String averageResponseSize;
    private String badRequestCount;//400
    private String unauthorizedRequestCount;//401
    private String authenticationRequiredRequestCount;//403
    private String internalServerErrorCount;//500
    private String serviceUnavailableErrorCount;//503

    public EndpointReportRepresentation(EndpointReport report) {
        endpointId = report.getEndpointId().getDomainId();
        averageRoundTimeInMili = String.valueOf(report.getAverageSuccessRoundTimeInMili());
        totalInvokeCount = String.valueOf(report.getTotalInvokeCount());
        BigDecimal up = new BigDecimal(report.getFailureResponseCount().get());
        BigDecimal divisor = new BigDecimal(report.getTotalInvokeCount());
        if (divisor.equals(BigDecimal.ZERO)) {
            failureResponseRate = "0.00";
        } else {
            failureResponseRate = up.divide(divisor, 2, RoundingMode.HALF_UP).toString();
        }
        badRequestCount = report.getBadRequestCount().toString();
        unauthorizedRequestCount = report.getUnauthorizedRequestCount().toString();
        authenticationRequiredRequestCount =
            report.getAuthenticationRequiredRequestCount().toString();
        internalServerErrorCount = report.getInternalServerErrorCount().toString();
        serviceUnavailableErrorCount = report.getServiceUnavailableErrorCount().toString();
        averageResponseSize = String.valueOf(report.getAverageResponseSize());
    }
}
