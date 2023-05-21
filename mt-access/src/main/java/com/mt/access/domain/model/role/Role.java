package com.mt.access.domain.model.role;

import com.mt.access.application.role.command.RoleUpdateCommand;
import com.mt.access.application.role.command.UpdateType;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.permission.PermissionType;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.event.ExternalPermissionUpdated;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.port.adapter.persistence.PermissionIdConverter;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Slf4j
@Table
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "roleRegion")
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
    private Set<PermissionId> commonPermissionIds;

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "role_api_permission_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "permission")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "roleApiPermissionRegion")
    @Convert(converter = PermissionIdConverter.class)
    private Set<PermissionId> apiPermissionIds;

    @Setter
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "role_external_permission_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "permission")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "roleExternalPermissionRegion")
    @Convert(converter = PermissionIdConverter.class)
    private Set<PermissionId> externalPermissionIds;
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
    private boolean systemCreate = false;

    private Role(ProjectId projectId, RoleId roleId, String name, String description,
                 Set<PermissionId> commonPermission, Set<PermissionId> apiPermission, RoleType type,
                 @Nullable RoleId parentId,
                 @Nullable ProjectId tenantId, Set<PermissionId> externalPermissionIds) {
        super();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.roleId = roleId;
        this.type = type;
        this.name = name;
        this.parentId = parentId;
        this.commonPermissionIds = commonPermission;
        this.apiPermissionIds = apiPermission;
        this.externalPermissionIds = externalPermissionIds;
        this.projectId = projectId;
        this.tenantId = tenantId;
        this.description = description;
    }

    public static Role newProjectRole(ProjectId projectId, RoleId roleId, String name,
                                      Set<PermissionId> permissionIds,
                                      RoleType type, @Nullable RoleId parentId,
                                      @Nullable ProjectId tenantId) {
        Role role;
        if (!permissionIds.isEmpty()) {
            Set<Permission> allByQuery = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                    new PermissionQuery(permissionIds));
            Set<PermissionId> apiPermissionIds =
                allByQuery.stream().filter(e -> e.getType().equals(PermissionType.API)).map(
                    Permission::getPermissionId).collect(Collectors.toSet());
            Set<PermissionId> commonPermissionIds =
                allByQuery.stream().filter(e -> e.getType().equals(PermissionType.COMMON)).map(
                    Permission::getPermissionId).collect(Collectors.toSet());
            role =
                new Role(projectId, roleId, name, null, commonPermissionIds, apiPermissionIds, type,
                    parentId, tenantId,
                    null);

        } else {
            role =
                new Role(projectId, roleId, name, null, Collections.emptySet(),
                    Collections.emptySet(), type,
                    parentId, tenantId,
                    null);
        }
        role.systemCreate = true;
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        return role;
    }

    public static Role newClient(ProjectId projectId, RoleId roleId, String name,
                                 @Nullable RoleId parentId
    ) {
        Role role =
            new Role(projectId, roleId, name, null, Collections.emptySet(), Collections.emptySet(),
                RoleType.CLIENT,
                parentId, null,
                null);
        role.systemCreate = true;
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        return role;
    }

    public static Role createRoleForTenant(ProjectId projectId, RoleId roleId, String name,
                                           String description, Set<PermissionId> commonPermission,
                                           Set<PermissionId> apiPermission,
                                           RoleType user, RoleId roleId1,
                                           Set<PermissionId> externalPermissionIds) {
        Role role;
        if (commonPermission.size() > 0) {
            Set<Permission> allByQuery = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                    new PermissionQuery(commonPermission));
            //add linked api permission
            Set<PermissionId> linkedApiPermission =
                allByQuery.stream().flatMap(e -> e.getLinkedApiPermissionIds().stream())
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            AtomicReference<ProjectId> tenantId = new AtomicReference<>();
            allByQuery.stream().findFirst().ifPresent(e -> {
                tenantId.set(e.getTenantId());
            });
            boolean b = allByQuery.stream().map(Permission::getTenantId).collect(Collectors.toSet())
                .size() > 1;
            if (b) {
                throw new DefinedRuntimeException(
                    "permissions added to role must belong to same tenant project", "1053",
                    HttpResponseCode.BAD_REQUEST);
            }
            if (apiPermission != null) {
                linkedApiPermission.addAll(apiPermission);
            }
            role = new Role(projectId, roleId, name, description, commonPermission,
                linkedApiPermission, user, roleId1,
                tenantId.get(), externalPermissionIds);
        } else {
            role =
                new Role(projectId, roleId, name, description, commonPermission,
                    apiPermission, user, roleId1,
                    null,
                    externalPermissionIds);
        }
        if (externalPermissionIds != null && externalPermissionIds.size() > 0) {
            CommonDomainRegistry.getDomainEventRepository()
                .append(new ExternalPermissionUpdated(projectId));
        }
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        return role;
    }

    public static void onboardNewProject(ProjectId authPId, ProjectId tenantProjectId,
                                         Set<PermissionId> permissionIdSet, UserId creator) {
        RoleId roleId = new RoleId();
        RoleId roleId1 = new RoleId();
        Role rootRole =
            Role.newProjectRole(authPId, roleId, tenantProjectId.getDomainId(),
                Collections.emptySet(),
                RoleType.PROJECT, null, tenantProjectId);
        Role adminRole =
            Role.newProjectRole(authPId, new RoleId(), PROJECT_ADMIN, permissionIdSet,
                RoleType.USER, roleId, tenantProjectId);

        Role userRole = Role.newProjectRole(tenantProjectId, new RoleId(), PROJECT_USER,
            Collections.emptySet(), RoleType.USER, roleId1, null);
        Role tenantClientRoot = Role.newProjectRole(tenantProjectId, new RoleId(), CLIENT_ROOT,
            Collections.emptySet(), RoleType.CLIENT_ROOT, null, null);
        Role tenantUserRoot =
            Role.newProjectRole(tenantProjectId, roleId1, tenantProjectId.getDomainId(),
                Collections.emptySet(), RoleType.PROJECT, null, null);

        DomainRegistry.getRoleRepository().add(adminRole);
        DomainRegistry.getRoleRepository().add(userRole);
        DomainRegistry.getRoleRepository().add(rootRole);
        DomainRegistry.getRoleRepository().add(tenantClientRoot);
        DomainRegistry.getRoleRepository().add(tenantUserRoot);
        CommonDomainRegistry.getDomainEventRepository()
            .append(new NewProjectRoleCreated(adminRole.getRoleId(),
                userRole.getRoleId(), tenantProjectId, permissionIdSet, creator));
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
    public void update(RoleUpdateCommand command) {
        if (command.getType().equals(UpdateType.BASIC)) {
            setName(command.getName());
            this.description = command.getDescription();
            if (!this.systemCreate && command.getParentId() != null) {
                this.parentId = new RoleId(command.getParentId());
            }
        } else if (command.getType().equals(UpdateType.API_PERMISSION)) {
            Optional.ofNullable(command.getApiPermissionIds())
                .ifPresentOrElse(e -> this.apiPermissionIds =
                    command.getApiPermissionIds().stream().map(PermissionId::new)
                        .collect(Collectors.toSet()), () -> this.apiPermissionIds = null);
            Set<PermissionId> externalPermissionIds =
                command.getExternalPermissionIds() == null ? null :
                    command.getExternalPermissionIds().stream().map(PermissionId::new)
                        .collect(Collectors.toSet());
            if (this.externalPermissionIds == null) {
                if (externalPermissionIds != null && externalPermissionIds.size() > 0) {
                    this.externalPermissionIds = externalPermissionIds;
                    CommonDomainRegistry.getDomainEventRepository()
                        .append(new ExternalPermissionUpdated(projectId));
                }
            } else {
                if (!this.externalPermissionIds.equals(externalPermissionIds)) {
                    this.externalPermissionIds = externalPermissionIds;
                    CommonDomainRegistry.getDomainEventRepository()
                        .append(new ExternalPermissionUpdated(projectId));
                }
            }
        } else if (command.getType().equals(UpdateType.COMMON_PERMISSION)) {
            Optional.ofNullable(command.getCommonPermissionIds())
                .ifPresent(e -> this.commonPermissionIds = e.stream().map(PermissionId::new)
                    .collect(Collectors.toSet()));

        }
        new RoleValidator(new HttpValidationNotificationHandler(), this).validate();
    }

    private void setName(String name) {
        if (name == null && this.name == null) {
            //do nothing
        } else if (name != null && this.name != null) {
            if (!name.equals(this.name)) {
                updateName(name);
            }
        } else if (name == null) {
            updateName(name);
        } else {
            updateName(name);
        }
    }

    private void updateName(String name) {
        if (List.of(RoleType.CLIENT, RoleType.PROJECT, RoleType.CLIENT_ROOT).contains(this.type)) {
            throw new DefinedRuntimeException(
                "client project client root type's name cannot be changed", "1054",
                HttpResponseCode.BAD_REQUEST);
        }
        if (isSystemCreate()) {
            throw new DefinedRuntimeException("system created role cannot be changed", "1055",
                HttpResponseCode.BAD_REQUEST);
        }
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Role that = (Role) o;
        return Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roleId);
    }

    public void remove() {
        if (this.systemCreate) {
            throw new DefinedRuntimeException("cannot delete system created role", "1056",
                HttpResponseCode.BAD_REQUEST);
        }
        DomainRegistry.getRoleRepository().remove(this);
    }

    public void patch(String name) {
        updateName(name);
    }

    public void removePermission(PermissionId permissionId) {
        if (this.externalPermissionIds != null) {
            this.externalPermissionIds.remove(permissionId);
        }
        if(this.commonPermissionIds!=null) {
            this.commonPermissionIds.remove(permissionId);
        }
        if(this.apiPermissionIds!=null) {
            this.apiPermissionIds.remove(permissionId);
        }
    }
}
