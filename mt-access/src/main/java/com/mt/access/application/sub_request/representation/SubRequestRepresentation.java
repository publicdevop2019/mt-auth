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

@Data
public class SubRequestRepresentation {
    private final String id;
    private final String projectId;
    private final String endpointId;
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
    private String projectName;
    private String endpointName;

    public SubRequestRepresentation(SubRequest subRequest) {
        id = subRequest.getSubRequestId().getDomainId();
        projectId = subRequest.getProjectId().getDomainId();
        endpointId = subRequest.getEndpointId().getDomainId();
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

    public static void updateProjectNames(SumPagedRep<SubRequestRepresentation> list) {
        if (!list.getData().isEmpty()) {
            Set<ProjectId> collect =
                list.getData().stream().map(e -> new ProjectId(e.getProjectId()))
                    .collect(Collectors.toSet());
            Set<Project> collect2 =
                ApplicationServiceRegistry.getProjectApplicationService().internalQuery(collect);
            list.getData().forEach(e -> collect2.stream().filter(ee ->
                ee.getProjectId().equals(new ProjectId(e.getProjectId()))).findAny()
                .ifPresent(eee -> {
                    e.setProjectName(eee.getName());
                }));
        }
    }

    public static void updateEndpointNames(SumPagedRep<SubRequestRepresentation> list) {
        if (!list.getData().isEmpty()) {
            Set<EndpointId> collect =
                list.getData().stream().map(e -> new EndpointId(e.getEndpointId()))
                    .collect(Collectors.toSet());
            Set<Endpoint> collect2 =
                ApplicationServiceRegistry.getEndpointApplicationService().internalQuery(collect);
            list.getData().forEach(e -> collect2.stream().filter(ee ->
                ee.getEndpointId().equals(new EndpointId(e.getEndpointId()))).findAny()
                .ifPresent(eee -> {
                    e.setEndpointName(eee.getName());
                }));
        }
    }
}
