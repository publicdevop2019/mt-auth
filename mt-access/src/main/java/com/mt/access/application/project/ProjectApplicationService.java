package com.mt.access.application.project;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_PROJECT;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_CACHE_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.PATCH_TENANT_PROJECT;
import static com.mt.access.domain.model.audit.AuditActionName.REMOVE_TENANT_PROJECT;
import static com.mt.access.domain.model.audit.AuditActionName.UPDATE_TENANT_PROJECT;
import static com.mt.access.domain.model.permission.Permission.VIEW_PROJECT_INFO;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.project.command.ProjectCreateCommand;
import com.mt.access.application.project.command.ProjectPatchCommand;
import com.mt.access.application.project.command.ProjectUpdateCommand;
import com.mt.access.application.project.representation.DashboardRepresentation;
import com.mt.access.application.project.representation.ProjectRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProjectApplicationService {

    private static final String PROJECT = "PROJECT";


    public SumPagedRep<Project> mgmtQuery(String queryParam, String pageParam,
                                          String skipCount) {
        ProjectQuery projectQuery = new ProjectQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getProjectRepository().getByQuery(projectQuery);
    }

    public ProjectRepresentation tenantQueryDetail(String id) {
        ProjectId projectId = new ProjectId(id);
        canReadProject(Collections.singleton(projectId));
        Optional<Project> byId = DomainRegistry.getProjectRepository().getById(projectId);
        if (byId.isEmpty()) {
            throw new DefinedRuntimeException("no project found", "0076",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        Project project = byId.get();
        long clientCount = DomainRegistry.getClientRepository().countProjectTotal(projectId);
        long epCount = DomainRegistry.getEndpointRepository().countProjectTotal(projectId);
        long userCount =
            DomainRegistry.getUserRelationRepository().countProjectOwnedTotal(projectId);
        long permissionCount =
            DomainRegistry.getPermissionRepository().countProjectCreateTotal(projectId);
        long roleCount = DomainRegistry.getRoleRepository().countProjectCreateTotal(projectId);
        return new ProjectRepresentation(project, clientCount, epCount, userCount, permissionCount,
            roleCount);
    }


    @AuditLog(actionName = UPDATE_TENANT_PROJECT)
    public void update(String id, ProjectUpdateCommand command, String changeId) {
        ProjectId projectId = new ProjectId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Optional<Project> first =
                    DomainRegistry.getProjectRepository().getByQuery(new ProjectQuery(projectId))
                        .findFirst();
                first.ifPresent(e -> {
                    e.replace(command.getName());
                    DomainRegistry.getProjectRepository().add(e);
                });
                return null;
            }, PROJECT);
    }


    @AuditLog(actionName = REMOVE_TENANT_PROJECT)
    public void remove(String id, String changeId) {
        ProjectId projectId = new ProjectId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<Project> corsProfile =
                DomainRegistry.getProjectRepository().getById(projectId);
            corsProfile.ifPresent(e -> {
                DomainRegistry.getProjectRepository().remove(e);
                DomainRegistry.getAuditService()
                    .storeAuditAction(REMOVE_TENANT_PROJECT,
                        e);
                DomainRegistry.getAuditService()
                    .logUserAction(log, REMOVE_TENANT_PROJECT,
                        e);
            });
            return null;
        }, PROJECT);
    }


    @AuditLog(actionName = PATCH_TENANT_PROJECT)
    public void patch(String id, JsonPatch command, String changeId) {
        ProjectId projectId = new ProjectId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                Optional<Project> corsProfile =
                    DomainRegistry.getProjectRepository().getById(projectId);
                if (corsProfile.isPresent()) {
                    Project corsProfile1 = corsProfile.get();
                    ProjectPatchCommand beforePatch = new ProjectPatchCommand(corsProfile1);
                    ProjectPatchCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, ProjectPatchCommand.class);
                    corsProfile1.replace(
                        afterPatch.getName()
                    );
                }
                return null;
            }, PROJECT);
    }


    @AuditLog(actionName = CREATE_TENANT_PROJECT)
    public String tenantCreate(ProjectCreateCommand command, String changeId) {
        ProjectId projectId = new ProjectId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                UserId userId = DomainRegistry.getCurrentUserService().getUserId();
                Project project = new Project(projectId, command.getName(), userId);
                DomainRegistry.getProjectRepository().add(project);
                return projectId.getDomainId();
            }, PROJECT);
    }

    public SumPagedRep<Project> tenantQuery(String pageParam) {
        Set<ProjectId> tenantIds = DomainRegistry.getCurrentUserService().getTenantIds();
        if (tenantIds.size() == 0) {
            return SumPagedRep.empty();
        }
        return DomainRegistry.getProjectRepository()
            .getByQuery(new ProjectQuery(tenantIds, pageParam));
    }

    /**
     * used for representation query and other internal query
     *
     * @param projectIds project id needs to query
     * @return all project matched
     */
    public Set<Project> internalQuery(Set<ProjectId> projectIds) {
        return QueryUtility.getAllByQuery(e -> DomainRegistry.getProjectRepository()
            .getByQuery(e), new ProjectQuery(projectIds));
    }

    public DashboardRepresentation mgmtQuery() {
        long projectCount = DomainRegistry.getProjectRepository().countTotal();
        long clientCount = DomainRegistry.getClientRepository().countTotal();
        long epCount = DomainRegistry.getEndpointRepository().countTotal();
        long epSharedCount = DomainRegistry.getEndpointRepository().countSharedTotal();
        long epPublicCount = DomainRegistry.getEndpointRepository().countPublicTotal();
        long userCount = DomainRegistry.getUserRepository().countTotal();
        return new DashboardRepresentation(projectCount, clientCount, epCount, epSharedCount,
            epPublicCount, userCount);
    }

    private void canReadProject(Set<ProjectId> ids) {
        if (ids == null) {
            throw new DefinedRuntimeException("no project id found", "0014",
                HttpResponseCode.FORBIDDEN,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        if (ids.size() == 0) {
            throw new DefinedRuntimeException("no project id found", "0015",
                HttpResponseCode.FORBIDDEN,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        //first check access to target project
        Set<ProjectId> authorizedTenantId = DomainRegistry.getCurrentUserService().getTenantIds();
        boolean b = authorizedTenantId.containsAll(ids);
        if (!b) {
            throw new DefinedRuntimeException("not allowed project", "0016",
                HttpResponseCode.FORBIDDEN,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        //second check has read project access to current project
        PermissionQuery permissionQuery = PermissionQuery
            .ofProjectWithTenantIds(new ProjectId(AppConstant.MT_AUTH_PROJECT_ID), ids);
        permissionQuery.setNames(Collections.singleton(VIEW_PROJECT_INFO));
        Set<Permission> allByQuery = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getPermissionRepository().getByQuery(e),
                permissionQuery);
        boolean b1 = DomainRegistry.getCurrentUserService().getPermissionIds().containsAll(
            allByQuery.stream().map(Permission::getPermissionId).collect(Collectors.toSet()));
        if (!b1) {
            throw new DefinedRuntimeException("no project read access", "0017",
                HttpResponseCode.FORBIDDEN,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
    }
}
