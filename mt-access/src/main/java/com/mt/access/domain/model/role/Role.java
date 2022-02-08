package com.mt.access.domain.model.role;

import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.port.adapter.persistence.PermissionIdSetConverter;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Set;

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

    public void replace(String name,Set<PermissionId> permissionIds) {
        this.name = name;
        this.permissionIds = permissionIds;
    }
}
