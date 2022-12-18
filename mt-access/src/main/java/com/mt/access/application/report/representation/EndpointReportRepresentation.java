package com.mt.access.application.report.representation;

import com.mt.access.domain.model.report.EndpointReport;
import lombok.Data;

@Data
public class EndpointReportRepresentation {
    private String endpointId;
    private String averageRoundTimeInSeconds;
    private String totalInvokeCount;
    private String failureResponseRate;
    private String averageResponseSize;
    private String badRequestCount;//400
    private String unauthorizedRequestCount;//401
    private String authenticationRequiredRequestCount;//403
    private String internalServerErrorCount;//500
    private String serviceUnavailableErrorCount;//503

    public EndpointReportRepresentation(EndpointReport report) {
        endpointId=report.getEndpointId().getDomainId();
        averageRoundTimeInSeconds=String.valueOf(report.getAverageRoundTimeInSeconds());
        totalInvokeCount=String.valueOf(report.getTotalInvokeCount());
        failureResponseRate=report.getFailureResponseRate().toString();
        badRequestCount=report.getBadRequestCount().toString();
        unauthorizedRequestCount=report.getUnauthorizedRequestCount().toString();
        authenticationRequiredRequestCount=report.getAuthenticationRequiredRequestCount().toString();
        internalServerErrorCount=report.getInternalServerErrorCount().toString();
        serviceUnavailableErrorCount=report.getServiceUnavailableErrorCount().toString();
        averageResponseSize=String.valueOf(report.getAverageResponseSize());
    }
}
