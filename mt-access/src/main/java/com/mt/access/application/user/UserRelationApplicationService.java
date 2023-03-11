package com.mt.access.application.user;

import static com.mt.access.domain.model.permission.Permission.ADMIN_MGMT;
import static com.mt.access.domain.model.permission.Permission.EDIT_TENANT_USER;
import static com.mt.access.domain.model.permission.Permission.VIEW_TENANT_USER;
import static com.mt.access.domain.model.role.Role.PROJECT_USER;
import static com.mt.access.infrastructure.AppConstant.MT_AUTH_PROJECT_ID;

import com.mt.access.application.user.command.UpdateUserRelationCommand;
import com.mt.access.application.user.representation.ProjectAdminRepresentation;
import com.mt.access.application.user.representation.UserTenantRepresentation;
import com.mt.access.domain.DomainRegistry;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserRelationApplicationService {
    private static final String USER_RELATION = "UserRelation";

    public Optional<UserRelation> getUserRelation(UserId userId, ProjectId projectId) {
        return DomainRegistry.getUserRelationRepository()
            .getByUserIdAndProjectId(userId, projectId);
    }

    /**
     * create user relation to mt-auth and target project as well.
     *
     * @param event new project role created event
     */

    @Transactional
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


    public UserRelation internalOnboardUserToTenant(UserId userId, ProjectId projectId) {
        AtomicReference<UserRelation> userRelation = new AtomicReference<>();
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(projectId.getDomainId() + "_onboard_user", (ignored) -> {
                userRelation.set(
                    CommonDomainRegistry.getTransactionService().returnedTransactional(() -> {
                        Optional<Role> first =
                            DomainRegistry.getRoleRepository()
                                .getByQuery(new RoleQuery(projectId, PROJECT_USER))
                                .findFirst();
                        if (first.isEmpty()) {
                            throw new DefinedRuntimeException(
                                "unable to find default user role for project",
                                "0024",
                                HttpResponseCode.BAD_REQUEST,
                                ExceptionCatalog.ILLEGAL_ARGUMENT);
                        }
                        return UserRelation.initNewUser(first.get().getRoleId(), userId, projectId);
                    }));
                return null;
            }, USER_RELATION);
        return userRelation.get();
    }

    public SumPagedRep<User> tenantUsers(String queryParam, String pageParam, String config) {
        UserRelationQuery userRelationQuery = new UserRelationQuery(queryParam, pageParam, config);
        DomainRegistry.getPermissionCheckService()
            .canAccess(userRelationQuery.getProjectIds(), VIEW_TENANT_USER);
        SumPagedRep<UserRelation> byQuery =
            DomainRegistry.getUserRelationRepository().getByQuery(userRelationQuery);
        if (byQuery.getData().isEmpty()) {
            SumPagedRep<User> empty = SumPagedRep.empty();
            empty.setTotalItemCount(byQuery.getTotalItemCount());
            return empty;
        }
        Set<UserId> collect =
            byQuery.getData().stream().map(UserRelation::getUserId).collect(Collectors.toSet());
        UserQuery userQuery = new UserQuery(collect);
        SumPagedRep<User> userSumPagedRep =
            DomainRegistry.getUserRepository().usersOfQuery(userQuery);
        userSumPagedRep.setTotalItemCount(byQuery.getTotalItemCount());
        return userSumPagedRep;
    }


    @Transactional
    public void update(String projectId, String userId, UpdateUserRelationCommand command,
                       String changeId) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                ProjectId projectId1 = new ProjectId(projectId);
                DomainRegistry.getPermissionCheckService().canAccess(projectId1, EDIT_TENANT_USER);
                Optional<UserRelation> byUserIdAndProjectId =
                    DomainRegistry.getUserRelationRepository()
                        .getByUserIdAndProjectId(new UserId(userId), projectId1);
                Set<RoleId> collect =
                    command.getRoles().stream().map(RoleId::new).collect(Collectors.toSet());
                if (collect.size() > 0) {
                    Set<Role> allByQuery = QueryUtility
                        .getAllByQuery(e -> DomainRegistry.getRoleRepository().getByQuery(e),
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
                    byUserIdAndProjectId.ifPresent(e -> {
                        e.setStandaloneRoles(
                            command.getRoles().stream().map(RoleId::new)
                                .collect(Collectors.toSet()));
                        e.setTenantIds(collect1);
                    });

                } else {
                    byUserIdAndProjectId.ifPresent(e -> {
                        e.setStandaloneRoles(Collections.emptySet());
                        e.setTenantIds(Collections.emptySet());
                    });
                }
                return null;
            }, USER_RELATION);
    }

    public Optional<UserTenantRepresentation> getTenantUserDetail(String projectId, String id) {
        UserRelationQuery userRelationQuery =
            new UserRelationQuery(new UserId(id), new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService()
            .canAccess(userRelationQuery.getProjectIds(), VIEW_TENANT_USER);
        return DomainRegistry.getUserRelationRepository().getByQuery(userRelationQuery).findFirst()
            .map(userRelation -> {
                UserQuery userQuery = new UserQuery(userRelation.getUserId());
                return DomainRegistry.getUserRepository().usersOfQuery(userQuery).findFirst()
                    .map(e -> new UserTenantRepresentation(userRelation, e));
            }).orElseGet(Optional::empty);
    }

    /**
     * find user relation for project id.
     *
     * @param id project id
     * @return boolean if relation can be found or not
     */
    public boolean projectRelationExist(String id) {
        ProjectId projectId = new ProjectId(id);
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        Optional<UserRelation> userRelation = getUserRelation(userId, projectId);
        return userRelation.isPresent();
    }

    public SumPagedRep<ProjectAdminRepresentation> adminsForProject(String pageConfig,
                                                                    String projectId) {
        ProjectId tenantProjectId = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(tenantProjectId, ADMIN_MGMT);
        RoleId tenantAdminRoleId = getTenantAdminRoleId(tenantProjectId);
        SumPagedRep<UserRelation> byQuery = DomainRegistry.getUserRelationRepository()
            .getByQuery(UserRelationQuery.findTenantAdmin(tenantAdminRoleId, pageConfig));
        Set<UserId> collect =
            byQuery.getData().stream().map(UserRelation::getUserId).collect(Collectors.toSet());
        SumPagedRep<User> userSumPagedRep =
            DomainRegistry.getUserRepository().usersOfQuery(new UserQuery(collect));
        SumPagedRep<ProjectAdminRepresentation> rep =
            new SumPagedRep<>(userSumPagedRep, ProjectAdminRepresentation::new);
        rep.setTotalItemCount(byQuery.getTotalItemCount());
        return rep;
    }

    @Transactional
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

    @Transactional
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
        Optional<User> targetUser = DomainRegistry.getUserRepository()
            .userOfId(userId);
        if (targetUser.isEmpty()) {
            throw new DefinedRuntimeException("unable to find user", "0078",
                HttpResponseCode.BAD_REQUEST, ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        RoleId tenantAdminRoleId = getTenantAdminRoleId(tenantProjectId);
        Optional<UserRelation> byUserIdAndProjectId = DomainRegistry.getUserRelationRepository()
            .getByUserIdAndProjectId(userId,
                new ProjectId(MT_AUTH_PROJECT_ID));
        UserRelation userRelation = byUserIdAndProjectId.get();
        if (isAdd) {
            if (userRelation.getStandaloneRoles().contains(tenantAdminRoleId)) {
                throw new DefinedRuntimeException("already admin", "0080",
                    HttpResponseCode.BAD_REQUEST, ExceptionCatalog.ILLEGAL_ARGUMENT);
            }
        } else {
            if (!userRelation.getStandaloneRoles().contains(tenantAdminRoleId)) {
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
        return userRelation;
    }

    private RoleId getTenantAdminRoleId(ProjectId tenantProjectId) {
        Optional<Role> first =
            DomainRegistry.getRoleRepository().getByQuery(RoleQuery.tenantAdmin(tenantProjectId))
                .findFirst();
        return first.get().getRoleId();
    }

    /**
     * remove all deleted user related user relation
     *
     * @param event user deleted event
     */
    @Transactional
    public void handleChange(UserDeleted event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (ignored) -> {
                log.debug("handle user deleted event");
                UserId userId = new UserId(event.getDomainId().getDomainId());
                Set<UserRelation> allByQuery = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getUserRelationRepository().getByQuery(query),
                    new UserRelationQuery(userId));
                DomainRegistry.getUserRelationRepository().removeAll(allByQuery);
                return null;
            }, USER_RELATION);
    }
}
