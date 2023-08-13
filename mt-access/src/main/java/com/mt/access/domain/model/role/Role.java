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
import com.mt.access.port.adapter.persistence.PermissionIdConverter;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Slf4j
@Table
@Entity
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "roleRegion")
@EqualsAndHashCode(callSuper = true)
public class Role extends Auditable {
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

    @Embedded
    private RoleId roleId;

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "role_common_permission_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "permission")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "roleCommonPermissionRegion")
    @Convert(converter = PermissionIdConverter.class)
    private Set<PermissionId> commonPermissionIds = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "role_api_permission_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "permission")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "roleApiPermissionRegion")
    @Convert(converter = PermissionIdConverter.class)
    private Set<PermissionId> apiPermissionIds = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "role_external_permission_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "permission")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "roleExternalPermissionRegion")
    @Convert(converter = PermissionIdConverter.class)
    private Set<PermissionId> externalPermissionIds = new LinkedHashSet<>();
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "projectId"))
    })
    private ProjectId projectId;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "tenantId"))
    })
    private ProjectId tenantId;
    @Enumerated(EnumType.STRING)
    private RoleType type;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "parentId"))
    })
    private RoleId parentId;
    private Boolean systemCreate;

    private Role() {
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
    }

    private static Role newProjectRole(ProjectId projectId, RoleId roleId, String name,
                                       RoleType type, @Nullable RoleId parentId,
                                       @Nullable ProjectId tenantId) {
        Role role = new Role();
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
        return role;
    }

    private static Role newProjectRoleAdmin(ProjectId projectId, RoleId roleId,
                                            Set<PermissionId> commonPermissionIds,
                                            Set<PermissionId> linkedPermissionIds, RoleId parentId,
                                            ProjectId tenantId) {
        Role role = new Role();
        role.setProjectId(projectId);
        role.setRoleId(roleId);
        role.setName(Role.PROJECT_ADMIN);
        role.setType(RoleType.USER);
        role.setSystemCreate(true);
        role.setParentId(parentId);
        role.setTenantId(tenantId);
        role.setCommonPermissionIds(true, commonPermissionIds);
        role.setApiPermissionIds(true, linkedPermissionIds);
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        DomainRegistry.getRoleValidationService()
            .validate(true, role, new HttpValidationNotificationHandler());
        return role;
    }

    public static Role newClient(ProjectId projectId, RoleId roleId, String name, RoleId parentId
    ) {
        Role role = new Role();
        role.setProjectId(projectId);
        role.setRoleId(roleId);
        role.setName(name);
        role.setClientParentId(parentId);
        role.setType(RoleType.CLIENT);
        role.setSystemCreate(true);
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        DomainRegistry.getRoleValidationService()
            .validate(false, role, new HttpValidationNotificationHandler());
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
                    new PermissionQuery(commonPermissionIds));
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

    public Set<PermissionId> getCommonPermissionIds() {
        return commonPermissionIds == null ? Collections.emptySet() : commonPermissionIds;
    }

    public Set<PermissionId> getTotalPermissionIds() {
        Set<PermissionId> objects = new HashSet<>();
        if (apiPermissionIds != null) {
            objects.addAll(apiPermissionIds);
        }
        if (commonPermissionIds != null) {
            objects.addAll(commonPermissionIds);
        }
        if (externalPermissionIds != null) {
            objects.addAll(externalPermissionIds);
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
    public void replace(RoleUpdateCommand command, TransactionContext context) {
        Validator.notNull(command.getType());
        if (command.getType().equals(UpdateType.BASIC)) {
            updateName(command.getName());
            setDescription(command.getDescription());
            if (Checker.isFalse(getSystemCreate()) && command.getParentId() != null) {
                this.parentId = new RoleId(command.getParentId());
            }
        } else if (command.getType().equals(UpdateType.API_PERMISSION)) {
            setApiPermissionIds(true,
                CommonUtility.map(command.getApiPermissionIds(), PermissionId::new));
            setExternalPermissionIds(
                CommonUtility.map(command.getExternalPermissionIds(), PermissionId::new), context);
        } else if (command.getType().equals(UpdateType.COMMON_PERMISSION)) {
            setCommonPermissionIds(false,
                CommonUtility.map(command.getCommonPermissionIds(), PermissionId::new));
        }
        new RoleValidator(new HttpValidationNotificationHandler(), this).validate();
        DomainRegistry.getRoleValidationService()
            .validate(false, this, new HttpValidationNotificationHandler());
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

    public void patch(String name, String description) {
        updateName(name);
        setDescription(description);
    }

    public void removePermission(PermissionId permissionId) {
        if (this.externalPermissionIds != null) {
            this.externalPermissionIds.remove(permissionId);
        }
        if (this.commonPermissionIds != null) {
            this.commonPermissionIds.remove(permissionId);
        }
        if (this.apiPermissionIds != null) {
            this.apiPermissionIds.remove(permissionId);
        }
    }
}
