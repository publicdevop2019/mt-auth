package com.mt.access.domain.model.role;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.port.adapter.persistence.PermissionIdSetConverter;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Long id;

    private String name;

    private String description;

    @Embedded
    private RoleId roleId;
    @Lob
    @Convert(converter = PermissionIdSetConverter.class)
    private Set<PermissionId> permissionIds;

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
    @Convert(converter = RoleType.DBConverter.class)
    private RoleType type;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "parentId"))
    })
    private RoleId parentId;
    private boolean systemCreate = false;

    private Role(ProjectId projectId, RoleId roleId, String name, String description, Set<PermissionId> permissionIds, RoleType type, @Nullable RoleId parentId, @Nullable ProjectId tenantId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.roleId = roleId;
        this.type = type;
        this.name = name;
        this.parentId = parentId;
        this.permissionIds = permissionIds;
        this.projectId = projectId;
        this.tenantId = tenantId;
        this.description = description;
    }

    public static Role autoCreate(ProjectId projectId, RoleId roleId, String name, String description, Set<PermissionId> permissionIds, RoleType type, @Nullable RoleId parentId, @Nullable ProjectId tenantId) {
        Role role = new Role(projectId, roleId, name, description, permissionIds, type, parentId, tenantId);
        role.systemCreate = true;
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        return role;
    }

    public static Role createRoleForTenant(ProjectId projectId, RoleId roleId, String name, String description, Set<PermissionId> collect, RoleType user, RoleId roleId1) {
        Role role;
        if (collect.size() > 0) {
            Set<Permission> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository().getByQuery((PermissionQuery) e), new PermissionQuery(collect));
            //add linked api permission
            Set<PermissionId> collect1 = allByQuery.stream().map(Permission::getLinkedApiPermissionId).filter(Objects::nonNull).collect(Collectors.toSet());
            collect.addAll(collect1);
            AtomicReference<ProjectId> tenantId = new AtomicReference<>();
            allByQuery.stream().findFirst().ifPresent(e -> {
                tenantId.set(e.getTenantId());
            });
            boolean b = allByQuery.stream().map(Permission::getTenantId).collect(Collectors.toSet()).size() > 1;
            if (b) {
                throw new IllegalArgumentException("permissions added to role must belong to same tenant project");
            }
            role = new Role(projectId, roleId, name, description, collect, user, roleId1, tenantId.get());
        } else {
            role = new Role(projectId, roleId, name, description, collect, user, roleId1, null);
        }
        new RoleValidator(new HttpValidationNotificationHandler(), role).validate();
        return role;
    }

    public static void onboardNewProject(ProjectId authPId, ProjectId tenantProjectId, Set<PermissionId> permissionIdSet, UserId creator) {
        RoleId roleId = new RoleId();
        RoleId roleId1 = new RoleId();
        Role rootRole = Role.autoCreate(authPId, roleId, tenantProjectId.getDomainId(), null, permissionIdSet, RoleType.PROJECT, null, tenantProjectId);
        Role adminRole = Role.autoCreate(authPId, new RoleId(), PROJECT_ADMIN, null, permissionIdSet, RoleType.USER, roleId, tenantProjectId);

        Role userRole = Role.autoCreate(tenantProjectId, new RoleId(), PROJECT_USER, null, Collections.emptySet(), RoleType.USER, roleId1, null);
        Role tenantClientRoot = Role.autoCreate(tenantProjectId, new RoleId(), CLIENT_ROOT, null, Collections.emptySet(), RoleType.CLIENT_ROOT, null, null);
        Role tenantUserRoot = Role.autoCreate(tenantProjectId, roleId1, tenantProjectId.getDomainId(), null, Collections.emptySet(), RoleType.PROJECT, null, null);

        DomainRegistry.getRoleRepository().add(adminRole);
        DomainRegistry.getRoleRepository().add(userRole);
        DomainRegistry.getRoleRepository().add(rootRole);
        DomainRegistry.getRoleRepository().add(tenantClientRoot);
        DomainRegistry.getRoleRepository().add(tenantUserRoot);
        DomainEventPublisher.instance().publish(new NewProjectRoleCreated(adminRole.getRoleId(),
                userRole.getRoleId(), tenantProjectId, permissionIdSet, creator));
    }

    public void replace(String name, String description, Set<PermissionId> permissionIds) {
        setName(name);
        this.description = description;
        this.permissionIds = permissionIds;
    }

    private void setName(String name) {
        if (name == null && this.name == null) {
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
            throw new IllegalStateException("client project client root type's name cannot be changed");
        }
        if (isSystemCreate()) {
            throw new IllegalStateException("system created role cannot be changed");
        }
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Role that = (Role) o;
        return Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roleId);
    }
}
