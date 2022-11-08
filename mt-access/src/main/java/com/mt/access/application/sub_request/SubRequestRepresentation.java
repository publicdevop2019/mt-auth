package com.mt.access.application.sub_request;

import com.mt.access.domain.model.sub_request.SubRequest;
import lombok.Data;

@Data
public class SubRequestRepresentation {
    private final String projectId;
    private final String endpointId;
    private final String endpointProjectId;
    private final int maxInvokePerSec;
    private final int maxInvokePerMin;
    private final String approvedBy;
    private final String status;
    private final String rejectionReason;
    private final String createdBy;
    private final long updateAt;
    private final long createAt;

    public SubRequestRepresentation(SubRequest subRequest) {
        projectId = subRequest.getProjectId().getDomainId();
        endpointId = subRequest.getEndpointId().getDomainId();
        endpointProjectId = subRequest.getEndpointProjectId().getDomainId();
        maxInvokePerSec = subRequest.getMaxInvokePerSec();
        maxInvokePerMin = subRequest.getMaxInvokePerMin();
        approvedBy = subRequest.getApprovedBy().getDomainId();
        status = subRequest.getSubRequestStatus().name();
        rejectionReason = subRequest.getRejectionReason();
        createdBy = subRequest.getCreatedBy();
        updateAt = subRequest.getModifiedAt().getTime();
        createAt = subRequest.getCreatedAt().getTime();
    }
}
