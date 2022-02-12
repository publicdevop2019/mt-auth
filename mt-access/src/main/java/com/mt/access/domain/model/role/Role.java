package com.mt.access.domain.model.role;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.port.adapter.persistence.PermissionIdSetConverter;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Table
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "roleRegion")
public class Role extends Auditable {
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
    public Role(ProjectId projectId, RoleId roleId, String name, String description, Set<PermissionId> permissionIds, RoleType type, @Nullable RoleId parentId , @Nullable ProjectId tenantId) {
        this.id= CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.roleId = roleId;
        this.type = type;
        this.name = name;
        this.parentId = parentId;
        this.permissionIds = permissionIds;
        this.projectId = projectId;
        this.tenantId = tenantId;
        this.description = description;
    }

    public static Role createNewRoleForProject(ProjectId projectId, RoleId roleId, String name, String description, Set<PermissionId> collect, RoleType user, RoleId roleId1) {
        Set<Permission> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository().getByQuery((PermissionQuery) e), new PermissionQuery(collect));
        //add linked api permission
        Set<PermissionId> collect1 = allByQuery.stream().map(Permission::getLinkedApiPermissionId).filter(Objects::nonNull).collect(Collectors.toSet());
        collect.addAll(collect1);
        AtomicReference<ProjectId> tenantId = new AtomicReference<>();
        allByQuery.stream().findFirst().ifPresent(e -> {
            tenantId.set(e.getTenantId());
        });
        boolean b = allByQuery.stream().map(Permission::getTenantId).collect(Collectors.toSet()).size() > 1;
        if(b){
            throw new IllegalArgumentException("permissions added to role must belong to same tenant project");
        }
        return new Role(projectId,roleId,name,description,collect,user,roleId1,tenantId.get());
    }

    public void replace(String name,Set<PermissionId> permissionIds) {
        this.name = name;
        this.permissionIds = permissionIds;
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
