package com.mt.access.domain.model.role;

import com.mt.access.application.role.command.RoleUpdateCommand;
import com.mt.access.application.role.command.UpdateType;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.event.ExternalPermissionUpdated;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@EqualsAndHashCode(callSuper = true)
public class Role extends Auditable {
    private boolean isCreate = false;
    public static final String PROJECT_USER = "PROJECT_USER";
    public static final String PROJECT_ADMIN = "PROJECT_ADMIN";
    public static final String CLIENT_ROOT = "CLIENT_ROOT";
    public static final Set<String> reservedName = new HashSet<>();

    static {
        reservedName.add(PROJECT_ADMIN);
        reservedName.add(PROJECT_USER);
        reservedName.add(CLIENT_ROOT);
    }

    private String name;

    private String description;

    private RoleId roleId;

    private Set<PermissionId> commonPermissionIds = new LinkedHashSet<>();
    private boolean commonPermissionIdsLoaded = false;
    private Set<PermissionId> apiPermissionIds = new LinkedHashSet<>();
    private boolean apiPermissionIdsLoaded = false;
    private Set<PermissionId> externalPermissionIds = new LinkedHashSet<>();
    private boolean extPermissionIdsLoaded = false;
    private ProjectId projectId;
    private ProjectId tenantId;
    private RoleType type;
    private RoleId parentId;
    private Boolean systemCreate;

    private Role() {
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
    }

    private static Role newProjectRole(ProjectId projectId, RoleId roleId, String name,
                                       RoleType type, @Nullable RoleId parentId,
                                       @Nullable ProjectId tenantId) {
        Role role = new Role();
        role.isCreate = true;
        role.setProjectId(projectId);
        role.setRoleId(roleId);
        role.setName(name);
        role.setType(type);
        role.setSystemCreate(true);
        role.setParentId(parentId);
        role.setTenantId(tenantId);
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        DomainRegistry.getRoleValidationService()
            .validate(true, role, new HttpValidationNotificationHandler());
        long milli = Instant.now().toEpochMilli();
        role.setCreatedAt(milli);
        role.setCreatedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        role.setModifiedAt(milli);
        role.setModifiedBy(AppConstant.DEFAULT_AUTO_ACTOR);

        return role;
    }

    private static Role newProjectRoleAdmin(ProjectId projectId, RoleId roleId,
                                            Set<PermissionId> commonPermissionIds,
                                            Set<PermissionId> linkedPermissionIds, RoleId parentId,
                                            ProjectId tenantId) {
        Role role = new Role();
        role.isCreate = true;
        role.setProjectId(projectId);
        role.setRoleId(roleId);
        role.setName(Role.PROJECT_ADMIN);
        role.setDescription(tenantId.getDomainId());
        role.setType(RoleType.USER);
        role.setSystemCreate(true);
        role.setParentId(parentId);
        role.setTenantId(tenantId);
        role.setCommonPermissionIds(true, commonPermissionIds);
        role.setApiPermissionIds(true, linkedPermissionIds);
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        DomainRegistry.getRoleValidationService()
            .validate(true, role, new HttpValidationNotificationHandler());
        long milli = Instant.now().toEpochMilli();
        role.setCreatedAt(milli);
        role.setCreatedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        role.setModifiedAt(milli);
        role.setModifiedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        return role;
    }

    public static Role newClient(ProjectId projectId, RoleId roleId, String name, RoleId parentId
    ) {
        Role role = new Role();
        role.isCreate = true;
        role.setProjectId(projectId);
        role.setRoleId(roleId);
        role.setName(name);
        role.setClientParentId(parentId);
        role.setType(RoleType.CLIENT);
        role.setSystemCreate(true);
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        DomainRegistry.getRoleValidationService()
            .validate(false, role, new HttpValidationNotificationHandler());
        long milli = Instant.now().toEpochMilli();
        role.setCreatedAt(milli);
        role.setCreatedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        role.setModifiedAt(milli);
        role.setModifiedBy(AppConstant.DEFAULT_AUTO_ACTOR);

        return role;
    }

    public static Role createRoleForTenant(ProjectId projectId,
                                           RoleId roleId,
                                           String name,
                                           String description,
                                           Set<PermissionId> commonPermissionIds,
                                           Set<PermissionId> apiPermissionIds,
                                           RoleId parentId,
                                           Set<PermissionId> externalPermissionIds,
                                           TransactionContext context) {
        Role role = new Role();
        role.isCreate = true;
        role.setSystemCreate(false);
        role.setProjectId(projectId);
        role.setRoleId(roleId);
        role.setName(name);
        role.setDescription(description);
        role.setType(RoleType.USER);
        role.setParentId(parentId);
        role.setExternalPermissionIds(externalPermissionIds, context);
        Set<PermissionId> linkedApiPermission = null;
        if (commonPermissionIds != null && commonPermissionIds.size() > 0) {
            Set<Permission> permissions = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                    PermissionQuery.internalQuery(commonPermissionIds));
            //add linked api permission
            linkedApiPermission =
                permissions.stream().flatMap(e -> e.getLinkedApiPermissionIds().stream())
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            AtomicReference<ProjectId> tenantId = new AtomicReference<>();
            permissions.stream().findFirst().ifPresent(e -> {
                tenantId.set(e.getTenantId());
            });
            role.setTenantId(tenantId.get());
            boolean b =
                permissions.stream().map(Permission::getTenantId).collect(Collectors.toSet())
                    .size() > 1;
            if (b) {
                throw new DefinedRuntimeException(
                    "permissions added to role must belong to same tenant project", "1053",
                    HttpResponseCode.BAD_REQUEST);
            }
            role.setCommonPermissionIds(false, commonPermissionIds);
        }
        if (Checker.notNull(apiPermissionIds)) {
            Validator.lessThanOrEqualTo(apiPermissionIds, 10);
            if (Checker.notNull(linkedApiPermission)) {
                apiPermissionIds.addAll(linkedApiPermission);
            }
        } else {
            if (Checker.notNull(linkedApiPermission)) {
                apiPermissionIds = linkedApiPermission;
            }
        }
        role.setApiPermissionIds(false, apiPermissionIds);
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        DomainRegistry.getRoleValidationService()
            .validate(false, role, new HttpValidationNotificationHandler());
        long milli = Instant.now().toEpochMilli();
        role.setCreatedAt(milli);
        role.setCreatedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        role.setModifiedAt(milli);
        role.setModifiedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        return role;
    }

    public static void onboardNewProject(ProjectId authPId, ProjectId tenantProjectId,
                                         Set<PermissionId> commonPermissionIds,
                                         Set<PermissionId> linkedPermissionIds, UserId creator,
                                         TransactionContext context) {
        log.debug("start of creating new project roles");
        RoleId roleId = new RoleId();
        RoleId projectRoleRoot = new RoleId();
        Role rootRole =
            Role.newProjectRole(authPId, roleId, tenantProjectId.getDomainId(),
                RoleType.PROJECT, null, tenantProjectId);
        Role adminRole =
            Role.newProjectRoleAdmin(authPId, new RoleId(), commonPermissionIds,
                linkedPermissionIds,
                roleId, tenantProjectId);
        Role userRole = Role.newProjectRole(tenantProjectId, new RoleId(), PROJECT_USER,
            RoleType.USER, projectRoleRoot, null);
        Role tenantClientRoot = Role.newProjectRole(tenantProjectId, new RoleId(), CLIENT_ROOT,
            RoleType.CLIENT_ROOT, null, null);
        Role tenantProjectRoot =
            Role.newProjectRole(tenantProjectId, projectRoleRoot, tenantProjectId.getDomainId(),
                RoleType.PROJECT, null, null);
        Set<Role> tobeStored = new HashSet<>();
        tobeStored.add(adminRole);
        log.debug("admin role created {}",
            CommonDomainRegistry.getCustomObjectSerializer().serialize(adminRole));
        tobeStored.add(userRole);
        tobeStored.add(rootRole);
        tobeStored.add(tenantClientRoot);
        tobeStored.add(tenantProjectRoot);
        DomainRegistry.getRoleRepository().addAll(tobeStored);
        context
            .append(new NewProjectRoleCreated(adminRole.getRoleId(),
                userRole.getRoleId(), tenantProjectId, creator));
        log.debug("end of creating new project roles");
    }

    public static Role fromDatabaseRow(Long id, Long createdAt, String createdBy, Long modifiedAt,
                                       String modifiedBy, Integer version,
                                       String name, String description, RoleId domainId,
                                       RoleId parentId, ProjectId projectId,
                                       Boolean systemCreate,
                                       ProjectId tenantId, RoleType type) {
        Role role = new Role();
        role.setId(id);
        role.setCreatedAt(createdAt);
        role.setCreatedBy(createdBy);
        role.setModifiedAt(modifiedAt);
        role.setModifiedBy(modifiedBy);
        role.setVersion(version);
        role.setName(name);
        role.setDescription(description);
        role.setRoleId(domainId);
        role.setParentId(parentId);
        role.setProjectId(projectId);
        role.setSystemCreate(systemCreate);
        role.setTenantId(tenantId);
        role.setType(type);
        return role;
    }

    public Set<PermissionId> getTotalPermissionIds() {
        Set<PermissionId> objects = new HashSet<>();
        if (getApiPermissionIds() != null) {
            objects.addAll(getApiPermissionIds());
        }
        if (getCommonPermissionIds() != null) {
            objects.addAll(getCommonPermissionIds());
        }
        if (getExternalPermissionIds() != null) {
            objects.addAll(getExternalPermissionIds());
        }
        return objects;
    }

    /**
     * update role permission or basic information based on command
     * 1. basic information update like description and parent id
     * 2. explicitly update api permission (not recommend to use)
     * 3. explicitly update shared permission
     * 4. update common permission
     *
     * @param command update command
     */
    public Role replace(RoleUpdateCommand command, TransactionContext context) {
        Validator.notNull(command.getType());
        Role update = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, Role.class);
        if (command.getType().equals(UpdateType.BASIC)) {
            update.updateName(command.getName());
            update.setDescription(command.getDescription());
            if (Checker.isFalse(update.getSystemCreate()) && command.getParentId() != null) {
                update.parentId = new RoleId(command.getParentId());
            }
        } else if (command.getType().equals(UpdateType.API_PERMISSION)) {
            update.setApiPermissionIds(true,
                CommonUtility.map(command.getApiPermissionIds(), PermissionId::new));
            update.setExternalPermissionIds(
                CommonUtility.map(command.getExternalPermissionIds(), PermissionId::new), context);
        } else if (command.getType().equals(UpdateType.COMMON_PERMISSION)) {
            update.setCommonPermissionIds(false,
                CommonUtility.map(command.getCommonPermissionIds(), PermissionId::new));
        }
        new RoleValidator(new HttpValidationNotificationHandler(), update).validate();
        DomainRegistry.getRoleValidationService()
            .validate(false, update, new HttpValidationNotificationHandler());
        update.setModifiedAt(Instant.now().toEpochMilli());
        update.setModifiedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        return update;
    }

    private void setApiPermissionIds(boolean isNewProjectOnboarding,
                                     Set<PermissionId> permissionIds) {
        if (Checker.notNull(permissionIds) && Checker.notEmpty(permissionIds)) {
            Validator.noNullMember(permissionIds);
            if (!isNewProjectOnboarding) {
                Validator.lessThanOrEqualTo(permissionIds, 10);
            }
        }
        CommonUtility.updateCollection(this.apiPermissionIds, permissionIds,
            () -> this.apiPermissionIds = permissionIds);
    }

    private void setTenantId(ProjectId tenantId) {
        this.tenantId = tenantId;
    }

    private void setExternalPermissionIds(Set<PermissionId> permissionIds,
                                          TransactionContext context) {
        Validator.validOptionalCollection(10, permissionIds);
        if (CommonUtility.collectionWillChange(this.externalPermissionIds, permissionIds)) {
            CommonUtility.updateCollection(this.externalPermissionIds, permissionIds,
                () -> this.externalPermissionIds = permissionIds);
            context
                .append(new ExternalPermissionUpdated(projectId));
        }
    }

    private void setCommonPermissionIds(boolean newProject, Set<PermissionId> permissionIds) {
        if (Checker.notNull(permissionIds) && Checker.notEmpty(permissionIds)) {
            Validator.noNullMember(permissionIds);
            if (!newProject) {
                Validator.lessThanOrEqualTo(permissionIds, 10);
            }
        }
        CommonUtility.updateCollection(this.commonPermissionIds, permissionIds,
            () -> this.commonPermissionIds = permissionIds);
    }

    private void setName(String name) {
        Validator.validRequiredString(1, 50, name);
        this.name = name;
    }

    private void updateName(String name) {
        if (List.of(RoleType.CLIENT, RoleType.PROJECT, RoleType.CLIENT_ROOT).contains(this.type)) {
            throw new DefinedRuntimeException(
                "client project client root type's name cannot be changed", "1054",
                HttpResponseCode.BAD_REQUEST);
        }
        if (Checker.isTrue(getSystemCreate())) {
            throw new DefinedRuntimeException("system created role cannot be changed", "1055",
                HttpResponseCode.BAD_REQUEST);
        }
        setName(name);
    }

    private void setProjectId(ProjectId projectId) {
        Validator.notNull(projectId);
        this.projectId = projectId;
    }

    private void setDescription(String description) {
        Validator.validOptionalString(100, description);
        this.description = description;
    }

    private void setRoleId(RoleId roleId) {
        Validator.notNull(roleId);
        this.roleId = roleId;
    }

    private void setParentId(RoleId parentId) {
        this.parentId = parentId;
    }

    private void setClientParentId(RoleId parentId) {
        Validator.notNull(parentId);
        this.parentId = parentId;
    }

    private void setType(RoleType type) {
        this.type = type;
    }

    public void setSystemCreate(boolean systemCreate) {
        this.systemCreate = systemCreate;
    }


    public void remove() {
        if (this.systemCreate) {
            throw new DefinedRuntimeException("cannot delete system created role", "1056",
                HttpResponseCode.BAD_REQUEST);
        }
        DomainRegistry.getRoleRepository().remove(this);
    }

    public boolean sameAs(Role updated) {
        return Objects.equals(name, updated.name) &&
            Objects.equals(description, updated.description) &&
            Objects.equals(roleId, updated.roleId) &&
            Objects.equals(commonPermissionIds, updated.commonPermissionIds) &&
            Objects.equals(apiPermissionIds, updated.apiPermissionIds) &&
            Objects.equals(externalPermissionIds, updated.externalPermissionIds) &&
            Objects.equals(projectId, updated.projectId) &&
            Objects.equals(tenantId, updated.tenantId) && type == updated.type &&
            Objects.equals(parentId, updated.parentId) &&
            Objects.equals(systemCreate, updated.systemCreate);
    }

    public Set<PermissionId> getApiPermissionIds() {
        if(isCreate){
            return apiPermissionIds;
        }
        if (Checker.isFalse(this.apiPermissionIdsLoaded)) {
            this.apiPermissionIds = DomainRegistry.getRoleRepository().findApiPermission(this);
            this.apiPermissionIdsLoaded = true;
        }
        return apiPermissionIds;
    }

    public Set<PermissionId> getExternalPermissionIds() {
        if(isCreate){
            return externalPermissionIds;
        }
        if (Checker.isFalse(this.extPermissionIdsLoaded)) {
            this.externalPermissionIds = DomainRegistry.getRoleRepository().findExtPermission(this);
            this.extPermissionIdsLoaded = true;
        }
        return externalPermissionIds;
    }

    public Set<PermissionId> getCommonPermissionIds() {
        if(isCreate){
            return commonPermissionIds;
        }
        if (Checker.isFalse(this.commonPermissionIdsLoaded)) {
            this.commonPermissionIds =
                DomainRegistry.getRoleRepository().findCommonPermission(this);
            this.commonPermissionIdsLoaded = true;
        }
        return commonPermissionIds;
    }
}
