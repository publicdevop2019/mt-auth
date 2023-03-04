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
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicSubRequest {
    private String projectId;
    private String endpointId;
    private String projectName;
    private String endpointName;

    public BasicSubRequest(SubRequest subRequest) {
        projectId = subRequest.getProjectId().getDomainId();
        endpointId = subRequest.getEndpointId().getDomainId();
    }

    /**
     * update view with project name
     *
     * @param list paginated data
     */
    public static void updateProjectNames(SumPagedRep<? extends BasicSubRequest> list) {
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

    /**
     * update view with endpoint name
     *
     * @param list paginated data
     */
    public static void updateEndpointNames(SumPagedRep<? extends BasicSubRequest> list) {
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
