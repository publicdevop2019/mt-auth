package com.mt.access.application.sub_request.representation;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.sub_request.SubRequest;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubRequestRepresentation extends BasicSubRequest{
    private final String id;
    private final String status;
    private final String rejectionReason;
    private final int maxInvokePerSecond;
    private final int maxInvokePerMinute;
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
        maxInvokePerSecond = subRequest.getMaxInvokePerSec();
        maxInvokePerMinute = subRequest.getMaxInvokePerMin();
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
