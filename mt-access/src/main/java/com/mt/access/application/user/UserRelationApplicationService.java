package com.mt.access.application.user;

import static com.mt.access.domain.model.audit.AuditActionName.ADD_TENANT_ADMIN;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_ADMIN;
import static com.mt.access.domain.model.permission.Permission.ADMIN_MGMT;
import static com.mt.access.domain.model.permission.Permission.EDIT_TENANT_USER;
import static com.mt.access.domain.model.permission.Permission.VIEW_TENANT_USER;
import static com.mt.access.domain.model.role.Role.PROJECT_USER;
import static com.mt.access.infrastructure.AppConstant.MT_AUTH_PROJECT_ID;

import com.mt.access.application.user.command.UpdateUserRelationCommand;
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
import com.mt.access.domain.model.user.event.UserDeleted;
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
            .query(new UserRelationQuery(userId, projectId)).findFirst();
    }

    public UserTenantRepresentation tenantUser(String projectId, String userId) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService()
            .canAccess(projectId1, VIEW_TENANT_USER);
        UserRelation relation =
            DomainRegistry.getUserRelationRepository().get(new UserId(userId), projectId1);
        User user = DomainRegistry.getUserRepository().get(relation.getUserId());
        return new UserTenantRepresentation(relation, user);
    }


    public SumPagedRep<User> tenantUsers(String queryParam, String pageParam, String config) {
        UserRelationQuery userRelationQuery = new UserRelationQuery(queryParam, pageParam, config);
        DomainRegistry.getPermissionCheckService()
            .canAccess(userRelationQuery.getProjectIds(), VIEW_TENANT_USER);
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
        SumPagedRep<UserRelation> byQuery = DomainRegistry.getUserRelationRepository()
            .query(UserRelationQuery.findTenantAdmin(tenantAdminRoleId, pageConfig));
        Set<UserId> collect =
            byQuery.getData().stream().map(UserRelation::getUserId).collect(Collectors.toSet());
        SumPagedRep<User> userSumPagedRep =
            DomainRegistry.getUserRepository().query(new UserQuery(collect));
        SumPagedRep<ProjectAdminRepresentation> rep =
            new SumPagedRep<>(userSumPagedRep, ProjectAdminRepresentation::new);
        rep.setTotalItemCount(byQuery.getTotalItemCount());
        return rep;
    }

    public UserRelation internalOnboardUserToTenant(UserId userId, ProjectId projectId) {
        AtomicReference<UserRelation> userRelation = new AtomicReference<>();
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(projectId.getDomainId() + "_onboard_user" + userId.getDomainId(),
                (ignored) -> {
                    userRelation.set(
                        CommonDomainRegistry.getTransactionService().returnedTransactional(() -> {
                            Optional<Role> first =
                                DomainRegistry.getRoleRepository()
                                    .query(new RoleQuery(projectId, PROJECT_USER))
                                    .findFirst();
                            if (first.isEmpty()) {
                                throw new DefinedRuntimeException(
                                    "unable to find default user role for project",
                                    "0024",
                                    HttpResponseCode.BAD_REQUEST,
                                    ExceptionCatalog.ILLEGAL_ARGUMENT);
                            }
                            return UserRelation.initNewUser(first.get().getRoleId(), userId,
                                projectId);
                        }));
                    return null;
                }, USER_RELATION);
        return userRelation.get();
    }

    public void update(String projectId, String userId, UpdateUserRelationCommand command,
                       String changeId) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                ProjectId projectId1 = new ProjectId(projectId);
                DomainRegistry.getPermissionCheckService().canAccess(projectId1, EDIT_TENANT_USER);
                UserRelation relation =
                    DomainRegistry.getUserRelationRepository()
                        .get(new UserId(userId), projectId1);
                Set<RoleId> collect =
                    command.getRoles().stream().map(RoleId::new).collect(Collectors.toSet());
                if (collect.size() > 0) {
                    Set<Role> allByQuery = QueryUtility
                        .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                            new RoleQuery(collect));
                    //remove default user so mt-auth will not be miss added to tenant list
                    Set<Role> removeDefaultUser = allByQuery.stream().filter(
                            e -> !AppConstant.MT_AUTH_DEFAULT_USER_ROLE.equals(
                                e.getRoleId().getDomainId()))
                        .collect(Collectors.toSet());
                    Set<ProjectId> collect1 =
                        removeDefaultUser.stream().map(Role::getTenantId)
                            .collect(Collectors.toSet());
                    //update tenant list based on role selected
                    relation.setStandaloneRoles(
                            command.getRoles().stream().map(RoleId::new)
                                .collect(Collectors.toSet()));
                    relation.setTenantIds(collect1);

                } else {
                        relation.setStandaloneRoles(Collections.emptySet());
                        relation.setTenantIds(Collections.emptySet());
                }
                return null;
            }, USER_RELATION);
    }

    @AuditLog(actionName = ADD_TENANT_ADMIN)
    public void addAdmin(String projectId, String rawUserId, String changeId) {
        ProjectId tenantProjectId = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(tenantProjectId, ADMIN_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                ProjectId projectId2 = new ProjectId(MT_AUTH_PROJECT_ID);
                UserId userId = new UserId(rawUserId);
                RoleId tenantAdminRoleId = getTenantAdminRoleId(tenantProjectId);
                UserRelation userRelation =
                    checkCondition(userId, tenantProjectId, projectId2, true);
                userRelation.addTenantAdmin(tenantProjectId, tenantAdminRoleId);
                DomainRegistry.getUserRelationRepository().add(userRelation);
                return null;
            }, USER_RELATION);
    }

    @AuditLog(actionName = DELETE_TENANT_ADMIN)
    public void removeAdmin(String projectId, String rawUserId, String changeId) {
        ProjectId tenantProjectId = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(tenantProjectId, ADMIN_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                ProjectId projectId2 = new ProjectId(MT_AUTH_PROJECT_ID);
                UserId userId = new UserId(rawUserId);
                UserRelation userRelation =
                    checkCondition(userId, tenantProjectId, projectId2, false);
                RoleId tenantAdminRoleId = getTenantAdminRoleId(tenantProjectId);
                userRelation.removeTenantAdmin(tenantProjectId, tenantAdminRoleId);
                DomainRegistry.getUserRelationRepository().add(userRelation);
                return null;
            }, USER_RELATION);
    }

    /**
     * remove all deleted user related user relation
     *
     * @param event user deleted event
     */
    public void handle(UserDeleted event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (ignored) -> {
                log.debug("handle user deleted event");
                UserId userId = new UserId(event.getDomainId().getDomainId());
                Set<UserRelation> allByQuery = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getUserRelationRepository().query(query),
                    new UserRelationQuery(userId));
                DomainRegistry.getUserRelationRepository().removeAll(allByQuery);
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
            .idempotent(event.getId().toString(), (ignored) -> {
                log.debug("handle new project role created event");
                RoleId adminRoleId = new RoleId(event.getDomainId().getDomainId());
                RoleId userRoleId = event.getUserRoleId();
                UserId creator = event.getCreator();
                ProjectId tenantId = event.getProjectId();
                UserRelation.onboardNewProject(adminRoleId, userRoleId, creator, tenantId,
                    new ProjectId(MT_AUTH_PROJECT_ID));
                return null;
            }, USER_RELATION);
    }

    private UserRelation checkCondition(UserId userId, ProjectId tenantProjectId,
                                        ProjectId projectId2, boolean isAdd) {
        if (tenantProjectId.equals(projectId2)) {
            throw new DefinedRuntimeException("admin modify is not allowed", "0077",
                HttpResponseCode.BAD_REQUEST, ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        DomainRegistry.getPermissionCheckService().canAccess(tenantProjectId, EDIT_TENANT_USER);
        if (userId
            .equals(DomainRegistry.getCurrentUserService().getUserId())) {
            throw new DefinedRuntimeException("you can not add/remove yourself", "0079",
                HttpResponseCode.BAD_REQUEST, ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        Optional<UserRelation> targetUser = DomainRegistry.getUserRelationRepository()
            .query(new UserRelationQuery(userId, tenantProjectId)).findFirst();
        if (targetUser.isEmpty()) {
            throw new DefinedRuntimeException("unable to find user relation", "0078",
                HttpResponseCode.BAD_REQUEST, ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        RoleId tenantAdminRoleId = getTenantAdminRoleId(tenantProjectId);
        UserRelation relation = DomainRegistry.getUserRelationRepository()
            .get(userId,
                new ProjectId(MT_AUTH_PROJECT_ID));
        if (isAdd) {
            if (relation.getStandaloneRoles().contains(tenantAdminRoleId)) {
                throw new DefinedRuntimeException("already admin", "0080",
                    HttpResponseCode.BAD_REQUEST, ExceptionCatalog.ILLEGAL_ARGUMENT);
            }
        } else {
            if (!relation.getStandaloneRoles().contains(tenantAdminRoleId)) {
                throw new DefinedRuntimeException("not admin", "0081",
                    HttpResponseCode.BAD_REQUEST, ExceptionCatalog.ILLEGAL_ARGUMENT);
            }
            //at least two admin present, for future maker & checker process
            long byQuery = DomainRegistry.getUserRelationRepository()
                .countProjectAdmin(tenantAdminRoleId);
            if (byQuery <= 2) {
                throw new DefinedRuntimeException("at least two admin", "0082",
                    HttpResponseCode.BAD_REQUEST, ExceptionCatalog.ILLEGAL_ARGUMENT);
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
