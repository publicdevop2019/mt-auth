package com.mt.access.application.project;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.project.command.ProjectCreateCommand;
import com.mt.access.application.project.command.ProjectPatchCommand;
import com.mt.access.application.project.command.ProjectUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.infrastructure.JwtAuthenticationService;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class ProjectApplicationService {

    private static final String PROJECT = "PROJECT";

    public SumPagedRep<Project> projects(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getProjectRepository().getByQuery(new ProjectQuery(queryParam, pageParam, skipCount));
    }

    public Optional<Project> project(String id) {
        return DomainRegistry.getProjectRepository().getById(new ProjectId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, ProjectUpdateCommand command, String changeId) {
        ProjectId projectId = new ProjectId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Optional<Project> first = DomainRegistry.getProjectRepository().getByQuery(new ProjectQuery(projectId)).findFirst();
            first.ifPresent(e -> {
                e.replace(command.getName());
                DomainRegistry.getProjectRepository().add(e);
            });
            return null;
        }, PROJECT);
    }

    @SubscribeForEvent
    @Transactional
    public void removeProject(String id, String changeId) {
        ProjectId projectId = new ProjectId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<Project> corsProfile = DomainRegistry.getProjectRepository().getById(projectId);
            corsProfile.ifPresent(e -> {
                DomainRegistry.getProjectRepository().remove(e);
            });
            return null;
        }, PROJECT);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        ProjectId projectId = new ProjectId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (ignored) -> {
            Optional<Project> corsProfile = DomainRegistry.getProjectRepository().getById(projectId);
            if (corsProfile.isPresent()) {
                Project corsProfile1 = corsProfile.get();
                ProjectPatchCommand beforePatch = new ProjectPatchCommand(corsProfile1);
                ProjectPatchCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, ProjectPatchCommand.class);
                corsProfile1.replace(
                        afterPatch.getName()
                );
            }
            return null;
        }, PROJECT);
    }

    @SubscribeForEvent
    @Transactional
    public String create(ProjectCreateCommand command, String changeId) {
        ProjectId projectId = new ProjectId();
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            UserId userId = DomainRegistry.getAuthenticationService().getUserId();
            Project project = new Project(projectId, command.getName(),userId);
            DomainRegistry.getProjectRepository().add(project);
            return projectId.getDomainId();
        }, PROJECT);
    }
}
