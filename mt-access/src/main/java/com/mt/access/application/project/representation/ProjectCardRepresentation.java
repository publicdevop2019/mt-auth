package com.mt.access.application.project.representation;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class ProjectCardRepresentation {
    private final String name;
    private final String id;
    private final String createdBy;
    private final Long createdAt;
    private String creatorName;

    public ProjectCardRepresentation(Project project) {
        this.name = project.getName();
        this.id = project.getProjectId().getDomainId();
        this.createdBy = project.getCreatedBy();
        this.createdAt = project.getCreatedAt().getTime();
    }

    public static void updateCreatorName(SumPagedRep<ProjectCardRepresentation> sumPagedRep) {
        Set<UserId> collect = sumPagedRep.getData().stream().map(e -> new UserId(e.getCreatedBy()))
            .collect(Collectors.toSet());
        Set<User> users = ApplicationServiceRegistry.getUserApplicationService().users(collect);
        sumPagedRep.getData().forEach(
            e -> users.stream().filter(ee -> ee.getUserId().getDomainId().equals(e.getCreatedBy()))
                .findAny().ifPresent(ee -> e.setCreatorName(ee.getDisplayName())));
    }
}
