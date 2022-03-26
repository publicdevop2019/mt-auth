package com.mt.access.application.project;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.project.command.ProjectCreateCommand;
import com.mt.access.application.project.command.ProjectPatchCommand;
import com.mt.access.application.project.command.ProjectUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.AccessDeniedException;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.access.domain.model.permission.Permission.VIEW_PROJECT_INFO;

@Slf4j
@Service
public class ProjectApplicationService {

    private static final String PROJECT = "PROJECT";

    public static void canReadProject(Set<ProjectId> ids) {
        if (ids == null)
            throw new AccessDeniedException();
        if (ids.size() == 0)
            throw new AccessDeniedException();
        //first check access to target project
        Set<ProjectId> authorizedTenantId = DomainRegistry.getCurrentUserService().getTenantIds();
        boolean b = authorizedTenantId.containsAll(ids);
        if (!b) {
            throw new AccessDeniedException();
        }
        //second check if has read project access to current project
        PermissionQuery permissionQuery = PermissionQuery.ofProjectWithTenantIds(new ProjectId(AppConstant.MT_AUTH_PROJECT_ID), ids);
        permissionQuery.setNames(Collections.singleton(VIEW_PROJECT_INFO));
        Set<Permission> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository().getByQuery((PermissionQuery) e), permissionQuery);
        boolean b1 = DomainRegistry.getCurrentUserService().getPermissionIds().containsAll(allByQuery.stream().map(Permission::getPermissionId).collect(Collectors.toSet()));
        if (!b1) {
            throw new AccessDeniedException();
        }
    }

    public SumPagedRep<Project> adminQueryProjects(String queryParam, String pageParam, String skipCount) {
        ProjectQuery projectQuery = new ProjectQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getProjectRepository().getByQuery(projectQuery);
    }

    public Optional<Project> project(String id) {
        ProjectId projectId = new ProjectId(id);
        canReadProject(Collections.singleton(projectId));
        return DomainRegistry.getProjectRepository().getById(projectId);
    }

    
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

    
    @Transactional
    public String create(ProjectCreateCommand command, String changeId) {
        ProjectId projectId = new ProjectId();
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            UserId userId = DomainRegistry.getCurrentUserService().getUserId();
            Project project = new Project(projectId, command.getName(), userId);
            DomainRegistry.getProjectRepository().add(project);
            return projectId.getDomainId();
        }, PROJECT);
    }

    public SumPagedRep<Project> findTenantProjects(String pageParam) {
        Set<ProjectId> tenantIds = DomainRegistry.getCurrentUserService().getTenantIds();
        if (tenantIds.size() == 0) {
            return SumPagedRep.empty();
        }
        return DomainRegistry.getProjectRepository().getByQuery(new ProjectQuery(tenantIds, pageParam));
    }
}
