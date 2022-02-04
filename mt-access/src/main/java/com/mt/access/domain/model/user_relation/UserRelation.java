package com.mt.access.domain.model.user_relation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.organization.OrganizationId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.position.PositionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.port.adapter.persistence.PermissionIdSetConverter;
import com.mt.access.port.adapter.persistence.ProjectIdSetConverter;
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
import java.util.Optional;
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
    @Lob
    @Convert(converter = ProjectIdSetConverter.class)
    private Set<ProjectId> tenantIds;
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

    public UserRelation(RoleId roleId, UserId creator, Set<PermissionId> permissionIds, ProjectId projectId, ProjectId tenantId) {
        this.id= CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.standaloneRoles=new HashSet<>();
        this.standaloneRoles.add(roleId);
        this.tenantIds=new HashSet<>();
        this.tenantIds.add(tenantId);
        this.userId=creator;
        this.projectId = projectId;
        this.permissionSnapshot = permissionIds;
    }

    public UserRelation(RoleId roleId, UserId creator, ProjectId projectId) {
        this.id= CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.standaloneRoles=new HashSet<>();
        this.standaloneRoles.add(roleId);
        this.userId=creator;
        this.projectId = projectId;
    }

    public static void onboardNewProject(RoleId adminRoleId, RoleId userRoleId, UserId creator, Set<PermissionId> permissionIds, ProjectId tenantId, ProjectId projectId) {
        //to mt-auth
        Optional<UserRelation> byUserIdAndProjectId = DomainRegistry.getUserRelationRepository().getByUserIdAndProjectId(creator, projectId);
        UserRelation userRelation;
        if(byUserIdAndProjectId.isPresent()){
            userRelation = byUserIdAndProjectId.get();
            userRelation.tenantIds.add(tenantId);
            userRelation.standaloneRoles.add(adminRoleId);
        }else{
            userRelation = new UserRelation(adminRoleId,creator,permissionIds,projectId,tenantId);
        }
        DomainRegistry.getUserRelationRepository().add(userRelation);
        //to target project
        UserRelation userRelation2 = new UserRelation(userRoleId,creator,tenantId);
        DomainRegistry.getUserRelationRepository().add(userRelation2);
    }
}
