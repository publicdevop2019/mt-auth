package com.mt.access.domain.model.permission;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.event.ProjectPermissionCreated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Table
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "permissionRegion")
public class Permission extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Long id;

    private String name;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "parentId"))
    })
    private PermissionId parentId;

    @Embedded
    private PermissionId permissionId;

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

    public Permission(ProjectId projectId, PermissionId permissionId, String name, @Nullable PermissionId parentId, @Nullable ProjectId tenantId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.permissionId = permissionId;
        this.parentId = parentId;
        this.projectId = projectId;
        this.name = name;
    }

    public Permission(ProjectId projectId, PermissionId permissionId, String name, @Nullable ProjectId tenantId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.permissionId = permissionId;
        this.projectId = projectId;
        this.name = name;
        this.tenantId = tenantId;
    }

    public static void onboardNewProject(ProjectId projectId, ProjectId tenantId, UserId creatorId) {
        Set<PermissionId> createdPermissionIds = new HashSet<>();
        PermissionId rootId = new PermissionId();
        Permission rootPermission = new Permission(projectId, rootId, projectId.getDomainId(), tenantId);
        PermissionId projectMgntId = new PermissionId();
        Permission permission0 = new Permission(projectId, projectMgntId, "PROJECT_INFO_MANAGEMENT", rootId, tenantId);
        Permission permission1 = new Permission(projectId, new PermissionId(), "VIEW_PROJECT_INFO", projectMgntId, tenantId);
        Permission permission2 = new Permission(projectId, new PermissionId(), "EDIT_PROJECT_INFO", projectMgntId, tenantId);
        PermissionId clientMgntId = new PermissionId();
        Permission permission27 = new Permission(projectId, clientMgntId, "CLIENT_MANAGEMENT", rootId, tenantId);
        Permission permission3 = new Permission(projectId, new PermissionId(), "CREATE_CLIENT", clientMgntId, tenantId);
        Permission permission4 = new Permission(projectId, new PermissionId(), "VIEW_CLIENT", clientMgntId, tenantId);
        Permission permission5 = new Permission(projectId, new PermissionId(), "EDIT_CLIENT", clientMgntId, tenantId);
        Permission permission6 = new Permission(projectId, new PermissionId(), "DELETE_CLIENT", clientMgntId, tenantId);

        PermissionId apiMgntId = new PermissionId();
        Permission permission28 = new Permission(projectId, apiMgntId, "API_MANAGEMENT", rootId, tenantId);
        Permission permission7 = new Permission(projectId, new PermissionId(), "CREATE_API", apiMgntId, tenantId);
        Permission permission8 = new Permission(projectId, new PermissionId(), "VIEW_API", apiMgntId, tenantId);
        Permission permission9 = new Permission(projectId, new PermissionId(), "EDIT_API", apiMgntId, tenantId);
        Permission permission10 = new Permission(projectId, new PermissionId(), "DELETE_API", apiMgntId, tenantId);

        PermissionId roleMgntId = new PermissionId();
        Permission permission29 = new Permission(projectId, roleMgntId, "ROLE_MANAGEMENT", rootId, tenantId);
        Permission permission11 = new Permission(projectId, new PermissionId(), "CREATE_ROLE", roleMgntId, tenantId);
        Permission permission12 = new Permission(projectId, new PermissionId(), "VIEW_ROLE", roleMgntId, tenantId);
        Permission permission13 = new Permission(projectId, new PermissionId(), "EDIT_ROLE", roleMgntId, tenantId);
        Permission permission14 = new Permission(projectId, new PermissionId(), "DELETE_ROLE", roleMgntId, tenantId);

        PermissionId orgMgntId = new PermissionId();
        Permission permission30 = new Permission(projectId, orgMgntId, "ORG_MANAGEMENT", rootId, tenantId);
        Permission permission15 = new Permission(projectId, new PermissionId(), "CREATE_ORG", orgMgntId, tenantId);
        Permission permission16 = new Permission(projectId, new PermissionId(), "VIEW_ORG", orgMgntId, tenantId);
        Permission permission17 = new Permission(projectId, new PermissionId(), "EDIT_ORG", orgMgntId, tenantId);
        Permission permission18 = new Permission(projectId, new PermissionId(), "DELETE_ORG", orgMgntId, tenantId);

        PermissionId permissionMgntId = new PermissionId();
        Permission permission31 = new Permission(projectId, permissionMgntId, "PERMISSION_MANAGEMENT", rootId, tenantId);
        Permission permission19 = new Permission(projectId, new PermissionId(), "CREATE_PERMISSION", permissionMgntId, tenantId);
        Permission permission20 = new Permission(projectId, new PermissionId(), "VIEW_PERMISSION", permissionMgntId, tenantId);
        Permission permission21 = new Permission(projectId, new PermissionId(), "EDIT_PERMISSION", permissionMgntId, tenantId);
        Permission permission22 = new Permission(projectId, new PermissionId(), "DELETE_PERMISSION", permissionMgntId, tenantId);

        PermissionId positionMgntId = new PermissionId();
        Permission permission32 = new Permission(projectId, positionMgntId, "POSITION_MANAGEMENT", rootId, tenantId);
        Permission permission23 = new Permission(projectId, new PermissionId(), "CREATE_POSITION", positionMgntId, tenantId);
        Permission permission24 = new Permission(projectId, new PermissionId(), "VIEW_POSITION", positionMgntId, tenantId);
        Permission permission25 = new Permission(projectId, new PermissionId(), "EDIT_POSITION", positionMgntId, tenantId);
        Permission permission26 = new Permission(projectId, new PermissionId(), "DELETE_POSITION", positionMgntId, tenantId);
        DomainRegistry.getPermissionRepository().add(rootPermission);
        DomainRegistry.getPermissionRepository().add(permission0);
        DomainRegistry.getPermissionRepository().add(permission1);
        DomainRegistry.getPermissionRepository().add(permission2);
        DomainRegistry.getPermissionRepository().add(permission3);
        DomainRegistry.getPermissionRepository().add(permission4);
        DomainRegistry.getPermissionRepository().add(permission5);
        DomainRegistry.getPermissionRepository().add(permission6);
        DomainRegistry.getPermissionRepository().add(permission7);
        DomainRegistry.getPermissionRepository().add(permission8);
        DomainRegistry.getPermissionRepository().add(permission9);
        DomainRegistry.getPermissionRepository().add(permission10);
        DomainRegistry.getPermissionRepository().add(permission11);
        DomainRegistry.getPermissionRepository().add(permission12);
        DomainRegistry.getPermissionRepository().add(permission13);
        DomainRegistry.getPermissionRepository().add(permission14);
        DomainRegistry.getPermissionRepository().add(permission15);
        DomainRegistry.getPermissionRepository().add(permission16);
        DomainRegistry.getPermissionRepository().add(permission17);
        DomainRegistry.getPermissionRepository().add(permission18);
        DomainRegistry.getPermissionRepository().add(permission19);
        DomainRegistry.getPermissionRepository().add(permission20);
        DomainRegistry.getPermissionRepository().add(permission21);
        DomainRegistry.getPermissionRepository().add(permission22);
        DomainRegistry.getPermissionRepository().add(permission23);
        DomainRegistry.getPermissionRepository().add(permission24);
        DomainRegistry.getPermissionRepository().add(permission25);
        DomainRegistry.getPermissionRepository().add(permission26);
        DomainRegistry.getPermissionRepository().add(permission27);
        DomainRegistry.getPermissionRepository().add(permission28);
        DomainRegistry.getPermissionRepository().add(permission29);
        DomainRegistry.getPermissionRepository().add(permission30);
        DomainRegistry.getPermissionRepository().add(permission31);
        DomainRegistry.getPermissionRepository().add(permission32);
        createdPermissionIds.add(rootPermission.getPermissionId());
        createdPermissionIds.add(permission0.getPermissionId());
        createdPermissionIds.add(permission1.getPermissionId());
        createdPermissionIds.add(permission2.getPermissionId());
        createdPermissionIds.add(permission3.getPermissionId());
        createdPermissionIds.add(permission4.getPermissionId());
        createdPermissionIds.add(permission5.getPermissionId());
        createdPermissionIds.add(permission6.getPermissionId());
        createdPermissionIds.add(permission7.getPermissionId());
        createdPermissionIds.add(permission8.getPermissionId());
        createdPermissionIds.add(permission9.getPermissionId());
        createdPermissionIds.add(permission10.getPermissionId());
        createdPermissionIds.add(permission11.getPermissionId());
        createdPermissionIds.add(permission12.getPermissionId());
        createdPermissionIds.add(permission13.getPermissionId());
        createdPermissionIds.add(permission14.getPermissionId());
        createdPermissionIds.add(permission15.getPermissionId());
        createdPermissionIds.add(permission16.getPermissionId());
        createdPermissionIds.add(permission17.getPermissionId());
        createdPermissionIds.add(permission18.getPermissionId());
        createdPermissionIds.add(permission19.getPermissionId());
        createdPermissionIds.add(permission20.getPermissionId());
        createdPermissionIds.add(permission21.getPermissionId());
        createdPermissionIds.add(permission22.getPermissionId());
        createdPermissionIds.add(permission23.getPermissionId());
        createdPermissionIds.add(permission24.getPermissionId());
        createdPermissionIds.add(permission25.getPermissionId());
        createdPermissionIds.add(permission26.getPermissionId());
        createdPermissionIds.add(permission27.getPermissionId());
        createdPermissionIds.add(permission28.getPermissionId());
        createdPermissionIds.add(permission29.getPermissionId());
        createdPermissionIds.add(permission30.getPermissionId());
        createdPermissionIds.add(permission31.getPermissionId());
        createdPermissionIds.add(permission32.getPermissionId());
        DomainEventPublisher.instance().publish(new ProjectPermissionCreated(createdPermissionIds, projectId, creatorId));
    }

    public void replace(String name) {
        this.name = name;
    }
}
