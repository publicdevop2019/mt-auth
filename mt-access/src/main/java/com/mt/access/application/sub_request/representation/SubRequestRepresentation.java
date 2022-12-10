package com.mt.access.application.sub_request.representation;

import com.mt.access.domain.model.sub_request.SubRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubRequestRepresentation extends BasicSubRequest{
    private final String id;
    private final String status;
    private final String rejectionReason;
    private final int replenishRate;
    private final int burstCapacity;
    private  String approvedBy;
    private final String createdBy;
    private  String rejectedBy;
    private final long updateAt;
    private final long createAt;
    private final String endpointProjectId;

    public SubRequestRepresentation(SubRequest subRequest) {
        super(subRequest);
        id = subRequest.getSubRequestId().getDomainId();
        status = subRequest.getSubRequestStatus().name();
        rejectionReason = subRequest.getRejectionReason();
        replenishRate = subRequest.getReplenishRate();
        burstCapacity = subRequest.getBurstCapacity();
        if (subRequest.getApprovedBy() != null) {
            approvedBy = subRequest.getApprovedBy().getDomainId();
        }
        if (subRequest.getRejectionBy() != null) {
            rejectedBy = subRequest.getRejectionBy().getDomainId();
        }
        createdBy = subRequest.getCreatedBy();
        updateAt = subRequest.getModifiedAt().getTime();
        createAt = subRequest.getCreatedAt().getTime();
        endpointProjectId = subRequest.getEndpointProjectId().getDomainId();
    }

}
