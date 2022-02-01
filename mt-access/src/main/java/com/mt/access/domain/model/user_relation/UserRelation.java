package com.mt.access.domain.model.user_relation;

import com.mt.access.domain.model.organization.OrganizationId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.position.PositionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.port.adapter.persistence.PermissionIdSetConverter;
import com.mt.access.port.adapter.persistence.RoleIdSetConverter;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "projectId"}))
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "userRelationRegion")
public class UserRelation extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Long id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "userId"))
    })
    private UserId userId;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "projectId"))
    })
    private ProjectId projectId;
    @Lob
    @Convert(converter = RoleIdSetConverter.class)
    private Set<RoleId> standaloneRoles;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "organizationId"))
    })
    private OrganizationId organizationId;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "positionId"))
    })
    private PositionId positionId;
    @Lob
    @Convert(converter = PermissionIdSetConverter.class)
    private Set<PermissionId> permissionSnapshot;

    public UserRelation(RoleId adminRoleId, RoleId userRoleId, UserId creator, Set<PermissionId> permissionIds, ProjectId projectId) {
        this.id= CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.standaloneRoles=new HashSet<>();
        this.standaloneRoles.add(adminRoleId);
        this.standaloneRoles.add(userRoleId);
        this.userId=creator;
        this.projectId = projectId;
        this.permissionSnapshot = permissionIds;
    }
}
