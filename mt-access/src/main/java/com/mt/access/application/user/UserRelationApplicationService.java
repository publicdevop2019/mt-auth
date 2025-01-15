package com.mt.access.application.user;

import static com.mt.access.domain.model.audit.AuditActionName.ADD_TENANT_ADMIN;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_ADMIN;
import static com.mt.access.domain.model.permission.Permission.ADMIN_MGMT;
import static com.mt.access.domain.model.permission.Permission.USER_MGMT;
import static com.mt.access.domain.model.role.Role.PROJECT_USER;
import static com.mt.access.infrastructure.AppConstant.MT_AUTH_PROJECT_ID;

import com.mt.access.application.user.command.AssignRoleCommand;
import com.mt.access.application.user.representation.ProjectAdminRepresentation;
import com.mt.access.application.user.representation.UserTenantRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserQuery;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.domain.model.user.UserRelationQuery;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserRelationApplicationService {
    private static final String USER_RELATION = "UserRelation";

    public Optional<UserRelation> query(UserId userId, ProjectId projectId) {
        return DomainRegistry.getUserRelationRepository()
            .query(userId, projectId);
    }

    public UserTenantRepresentation tenantUser(String projectId, String userId) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService()
            .canAccess(projectId1, USER_MGMT);
        UserRelation relation =
            DomainRegistry.getUserRelationRepository().get(new UserId(userId), projectId1);
        User user = DomainRegistry.getUserRepository().get(relation.getUserId());
        return new UserTenantRepresentation(relation, user);
    }


    public SumPagedRep<User> tenantUsers(String queryParam, String pageParam, String config) {
        UserRelationQuery userRelationQuery = new UserRelationQuery(queryParam, pageParam, config);
        DomainRegistry.getPermissionCheckService()
            .canAccess(userRelationQuery.getProjectIds(), USER_MGMT);
        SumPagedRep<UserRelation> byQuery =
            DomainRegistry.getUserRelationRepository().query(userRelationQuery);
        if (byQuery.getData().isEmpty()) {
            SumPagedRep<User> empty = SumPagedRep.empty();
            empty.setTotalItemCount(byQuery.getTotalItemCount());
            return empty;
        }
        Set<UserId> collect =
            byQuery.getData().stream().map(UserRelation::getUserId).collect(Collectors.toSet());
        UserQuery userQuery = new UserQuery(collect);
        SumPagedRep<User> userSumPagedRep =
            DomainRegistry.getUserRepository().query(userQuery);
        userSumPagedRep.setTotalItemCount(byQuery.getTotalItemCount());
        return userSumPagedRep;
    }

    /**
     * find user relation for project id.
     *
     * @param id project id
     * @return boolean if relation can be found or not
     */
    public boolean checkExist(String id) {
        ProjectId projectId = new ProjectId(id);
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        Optional<UserRelation> userRelation = query(userId, projectId);
        return userRelation.isPresent();
    }

    public SumPagedRep<ProjectAdminRepresentation> adminQuery(String pageConfig,
                                                              String projectId) {
        ProjectId tenantProjectId = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(tenantProjectId, ADMIN_MGMT);
        RoleId tenantAdminRoleId = getTenantAdminRoleId(tenantProjectId);
        SumPagedRep<UserRelation> mappedUserRelation = DomainRegistry.getUserRelationRepository()
            .query(UserRelationQuery.findTenantAdmin(tenantAdminRoleId, pageConfig));
        Set<UserId> ids =
            mappedUserRelation.getData().stream().map(UserRelation::getUserId)
                .collect(Collectors.toSet());
        Set<User> userSumPagedRep =
            QueryUtility.getAllByQuery(e -> DomainRegistry.getUserRepository().query(e),
                new UserQuery(ids));
        SumPagedRep<ProjectAdminRepresentation> rep =
            new SumPagedRep<>();
        rep.setData(userSumPagedRep.stream().map(ProjectAdminRepresentation::new)
            .collect(Collectors.toList()));
        rep.setTotalItemCount(mappedUserRelation.getTotalItemCount());
        return rep;
    }

    public UserRelation internalOnboardUserToTenant(UserId userId, ProjectId projectId) {
        AtomicReference<UserRelation> userRelation = new AtomicReference<>();
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(projectId.getDomainId() + "_onboard_user" + userId.getDomainId(),
                (context) -> {
                    Optional<Role> first =
                        DomainRegistry.getRoleRepository()
                            .query(new RoleQuery(projectId, PROJECT_USER))
                            .findFirst();
                    if (first.isEmpty()) {
                        throw new DefinedRuntimeException(
                            "unable to find default user role for project",
                            "1024",
                            HttpResponseCode.BAD_REQUEST);
                    }
                    userRelation.set(
                        UserRelation.initNewUser(first.get().getRoleId(), userId, projectId));
                    return null;
                }, USER_RELATION);
        return userRelation.get();
    }

    public void tenantRoleAssign(String rawProjectId, String userId, AssignRoleCommand command,
                                 String changeId) {
        Validator.notNull(command.getRoleIds());
        Validator.notEmpty(command.getRoleIds());
        ProjectId projectId = new ProjectId(rawProjectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId, USER_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                UserRelation relation =
                    DomainRegistry.getUserRelationRepository()
                        .get(new UserId(userId), projectId);
                UserRelation userRelation = relation.assignRole(command.getRoleIds());
                DomainRegistry.getUserRelationRepository().update(relation, userRelation);
                return null;
            }, USER_RELATION);
    }

    public void tenantRoleRemove(String rawProjectId, String rawUserId, String rawRoleId,
                                 String changeId) {
        ProjectId projectId = new ProjectId(rawProjectId);
        RoleId roleId = new RoleId(rawRoleId);
        UserId userId = new UserId(rawUserId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId, USER_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                UserRelation relation =
                    DomainRegistry.getUserRelationRepository()
                        .get(userId, projectId);
                UserRelation userRelation = relation.removeRole(roleId);
                DomainRegistry.getUserRelationRepository().update(relation, userRelation);
                return null;
            }, USER_RELATION);
    }

    @AuditLog(actionName = ADD_TENANT_ADMIN)
    public void addAdmin(String projectId, String rawUserId, String changeId) {
        ProjectId tenantProjectId = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(tenantProjectId, ADMIN_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                ProjectId projectId2 = new ProjectId(MT_AUTH_PROJECT_ID);
                UserId userId = new UserId(rawUserId);
                RoleId tenantAdminRoleId = getTenantAdminRoleId(tenantProjectId);
                UserRelation current =
                    checkCondition(userId, tenantProjectId, projectId2, true);
                UserRelation updated =
                    current.addTenantAdmin(tenantProjectId, tenantAdminRoleId);
                DomainRegistry.getUserRelationRepository().update(current, updated);
                return null;
            }, USER_RELATION);
    }

    @AuditLog(actionName = DELETE_TENANT_ADMIN)
    public void removeAdmin(String projectId, String rawUserId, String changeId) {
        ProjectId tenantProjectId = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(tenantProjectId, ADMIN_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                ProjectId projectId2 = new ProjectId(MT_AUTH_PROJECT_ID);
                UserId userId = new UserId(rawUserId);
                UserRelation userRelation =
                    checkCondition(userId, tenantProjectId, projectId2, false);
                RoleId tenantAdminRoleId = getTenantAdminRoleId(tenantProjectId);
                UserRelation update =
                    userRelation.removeTenantAdmin(tenantProjectId, tenantAdminRoleId);
                DomainRegistry.getUserRelationRepository().update(userRelation, update);
                return null;
            }, USER_RELATION);
    }

    /**
     * create user relation to mt-auth and target project as well.
     *
     * @param event new project role created event
     */
    public void handle(NewProjectRoleCreated event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                RoleId adminRoleId = new RoleId(event.getDomainId().getDomainId());
                RoleId userRoleId = event.getUserRoleId();
                UserId creator = event.getCreator();
                ProjectId tenantId = event.getProjectId();
                log.info("handle new project role created event, project id {}",
                    tenantId.getDomainId());
                UserRelation.onboardNewProject(adminRoleId, userRoleId, creator, tenantId,
                    new ProjectId(MT_AUTH_PROJECT_ID), context);
                return null;
            }, USER_RELATION);
    }

    private UserRelation checkCondition(UserId userId, ProjectId tenantProjectId,
                                        ProjectId projectId2, boolean isAdd) {
        if (tenantProjectId.equals(projectId2)) {
            throw new DefinedRuntimeException("admin modify is not allowed", "1077",
                HttpResponseCode.BAD_REQUEST);
        }
        DomainRegistry.getPermissionCheckService().canAccess(tenantProjectId, USER_MGMT);
        if (userId
            .equals(DomainRegistry.getCurrentUserService().getUserId())) {
            throw new DefinedRuntimeException("you can not add/remove yourself", "1079",
                HttpResponseCode.BAD_REQUEST);
        }
        Optional<UserRelation> targetUser = DomainRegistry.getUserRelationRepository()
            .query(userId, tenantProjectId);
        if (targetUser.isEmpty()) {
            throw new DefinedRuntimeException("unable to find user relation", "1078",
                HttpResponseCode.BAD_REQUEST);
        }
        RoleId tenantAdminRoleId = getTenantAdminRoleId(tenantProjectId);
        UserRelation relation = DomainRegistry.getUserRelationRepository()
            .get(userId,
                new ProjectId(MT_AUTH_PROJECT_ID));
        if (isAdd) {
            if (relation.getStandaloneRoles().contains(tenantAdminRoleId)) {
                throw new DefinedRuntimeException("already admin", "1080",
                    HttpResponseCode.BAD_REQUEST);
            }
        } else {
            if (!relation.getStandaloneRoles().contains(tenantAdminRoleId)) {
                throw new DefinedRuntimeException("not admin", "1081",
                    HttpResponseCode.BAD_REQUEST);
            }
            //at least two admin present, for future maker & checker process
            long byQuery = DomainRegistry.getUserRelationRepository()
                .countProjectAdmin(tenantAdminRoleId);
            if (byQuery <= 2) {
                throw new DefinedRuntimeException("at least two admin", "1082",
                    HttpResponseCode.BAD_REQUEST);
            }

        }
        return relation;
    }

    private RoleId getTenantAdminRoleId(ProjectId tenantProjectId) {
        Optional<Role> first =
            DomainRegistry.getRoleRepository().query(RoleQuery.tenantAdmin(tenantProjectId))
                .findFirst();
        return first.get().getRoleId();
    }

}
