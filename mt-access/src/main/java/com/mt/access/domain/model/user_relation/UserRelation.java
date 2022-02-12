package com.mt.access.domain.model.user_relation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.organization.OrganizationId;
import com.mt.access.domain.model.position.PositionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.port.adapter.persistence.ProjectIdSetConverter;
import com.mt.access.port.adapter.persistence.RoleIdSetConverter;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "projectId"}))
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "userRelationRegion")
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

    public UserRelation(RoleId roleId, UserId creator, ProjectId projectId, ProjectId tenantId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.standaloneRoles = new HashSet<>();
        this.standaloneRoles.add(roleId);
        this.tenantIds = new HashSet<>();
        this.tenantIds.add(tenantId);
        this.userId = creator;
        this.projectId = projectId;
    }

    public UserRelation(RoleId roleId, UserId creator, ProjectId projectId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.standaloneRoles = new HashSet<>();
        this.standaloneRoles.add(roleId);
        this.userId = creator;
        this.projectId = projectId;
    }

    public static void onboardNewProject(RoleId adminRoleId, RoleId userRoleId, UserId creator, ProjectId tenantId, ProjectId authProjectId) {
        //to mt-auth
        Optional<UserRelation> byUserIdAndProjectId = DomainRegistry.getUserRelationRepository().getByUserIdAndProjectId(creator, authProjectId);
        UserRelation userRelation;
        if (byUserIdAndProjectId.isPresent()) {
            userRelation = byUserIdAndProjectId.get();
            if (userRelation.tenantIds == null) {
                userRelation.tenantIds = new HashSet<>();
            }
            userRelation.tenantIds.add(tenantId);
            userRelation.standaloneRoles.add(adminRoleId);
        } else {
            userRelation = new UserRelation(adminRoleId, creator, authProjectId, tenantId);
        }
        DomainRegistry.getUserRelationRepository().add(userRelation);
        //to target project
        UserRelation userRelation2 = new UserRelation(userRoleId, creator, tenantId);
        DomainRegistry.getUserRelationRepository().add(userRelation2);
    }

    public static void initNewUser(RoleId userRoleId, UserId creator, ProjectId authProjectId) {
        UserRelation userRelation2 = new UserRelation(userRoleId, creator, authProjectId);
        DomainRegistry.getUserRelationRepository().add(userRelation2);
    }

    public void setStandaloneRoles(Set<RoleId> collect) {
        this.standaloneRoles = collect;
        Set<Role> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getRoleRepository().getByQuery((RoleQuery) e), new RoleQuery(collect));
        if (collect.size() != allByQuery.size()) {
            HttpValidationNotificationHandler handler = new HttpValidationNotificationHandler();
            handler.handleError("not able to find all roles");
        }
    }

    public void setTenantIds(Set<ProjectId> tenantIds) {
        this.tenantIds = tenantIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
