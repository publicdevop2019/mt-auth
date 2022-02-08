package com.mt.access.domain.model.permission;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.permission.event.ProjectPermissionCreated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleType;
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
import java.util.Optional;
import java.util.Set;

@Table
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "permissionRegion")
public class Permission extends Auditable {
    public static final String API_ACCESS = "API_ACCESS";
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
    @Convert(converter = PermissionType.DBConverter.class)
    private PermissionType type;

    public Permission(ProjectId projectId, PermissionId permissionId, String name, PermissionType type,@Nullable PermissionId parentId, @Nullable ProjectId tenantId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.permissionId = permissionId;
        this.parentId = parentId;
        this.projectId = projectId;
        this.tenantId = tenantId;
        this.name = name;
        this.type = type;
    }

    public Permission(ProjectId projectId, PermissionId permissionId, String name, PermissionType type, @Nullable ProjectId tenantId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.permissionId = permissionId;
        this.projectId = projectId;
        this.name = name;
        this.tenantId = tenantId;
        this.type = type;
    }

    public static void onboardNewProject(ProjectId projectId, ProjectId tenantId, UserId creatorId) {
        Set<PermissionId> createdPermissionIds = new HashSet<>();
        PermissionId rootId = new PermissionId();
        Permission rootPermission = new Permission(projectId, rootId, tenantId.getDomainId(),PermissionType.PROJECT, tenantId);
        PermissionId projectMgntId = new PermissionId();
        Permission permission0 = new Permission(projectId, projectMgntId, "PROJECT_INFO_MANAGEMENT",PermissionType.COMMON, rootId, tenantId);
        Permission permission1 = new Permission(projectId, new PermissionId(), "VIEW_PROJECT_INFO",PermissionType.COMMON, projectMgntId, tenantId);
        Permission permission2 = new Permission(projectId, new PermissionId(), "EDIT_PROJECT_INFO",PermissionType.COMMON, projectMgntId, tenantId);
        PermissionId clientMgntId = new PermissionId();
        Permission permission27 = new Permission(projectId, clientMgntId, "CLIENT_MANAGEMENT",PermissionType.COMMON, rootId, tenantId);
        Permission permission3 = new Permission(projectId, new PermissionId(), "CREATE_CLIENT",PermissionType.COMMON, clientMgntId, tenantId);
        Permission permission4 = new Permission(projectId, new PermissionId(), "VIEW_CLIENT",PermissionType.COMMON, clientMgntId, tenantId);
        Permission permission5 = new Permission(projectId, new PermissionId(), "EDIT_CLIENT",PermissionType.COMMON, clientMgntId, tenantId);
        Permission permission6 = new Permission(projectId, new PermissionId(), "DELETE_CLIENT",PermissionType.COMMON, clientMgntId, tenantId);

        PermissionId apiMgntId = new PermissionId();
        Permission permission28 = new Permission(projectId, apiMgntId, "API_MANAGEMENT",PermissionType.COMMON, rootId, tenantId);
        Permission permission7 = new Permission(projectId, new PermissionId(), "CREATE_API",PermissionType.COMMON, apiMgntId, tenantId);
        Permission permission8 = new Permission(projectId, new PermissionId(), "VIEW_API",PermissionType.COMMON, apiMgntId, tenantId);
        Permission permission9 = new Permission(projectId, new PermissionId(), "EDIT_API",PermissionType.COMMON, apiMgntId, tenantId);
        Permission permission10 = new Permission(projectId, new PermissionId(), "DELETE_API",PermissionType.COMMON, apiMgntId, tenantId);

        PermissionId roleMgntId = new PermissionId();
        Permission permission29 = new Permission(projectId, roleMgntId, "ROLE_MANAGEMENT",PermissionType.COMMON, rootId, tenantId);
        Permission permission11 = new Permission(projectId, new PermissionId(), "CREATE_ROLE",PermissionType.COMMON, roleMgntId, tenantId);
        Permission permission12 = new Permission(projectId, new PermissionId(), "VIEW_ROLE",PermissionType.COMMON, roleMgntId, tenantId);
        Permission permission13 = new Permission(projectId, new PermissionId(), "EDIT_ROLE",PermissionType.COMMON, roleMgntId, tenantId);
        Permission permission14 = new Permission(projectId, new PermissionId(), "DELETE_ROLE",PermissionType.COMMON, roleMgntId, tenantId);

        PermissionId orgMgntId = new PermissionId();
        Permission permission30 = new Permission(projectId, orgMgntId, "ORG_MANAGEMENT",PermissionType.COMMON, rootId, tenantId);
        Permission permission15 = new Permission(projectId, new PermissionId(), "CREATE_ORG",PermissionType.COMMON, orgMgntId, tenantId);
        Permission permission16 = new Permission(projectId, new PermissionId(), "VIEW_ORG",PermissionType.COMMON, orgMgntId, tenantId);
        Permission permission17 = new Permission(projectId, new PermissionId(), "EDIT_ORG",PermissionType.COMMON, orgMgntId, tenantId);
        Permission permission18 = new Permission(projectId, new PermissionId(), "DELETE_ORG",PermissionType.COMMON, orgMgntId, tenantId);

        PermissionId permissionMgntId = new PermissionId();
        Permission permission31 = new Permission(projectId, permissionMgntId, "PERMISSION_MANAGEMENT",PermissionType.COMMON, rootId, tenantId);
        Permission permission19 = new Permission(projectId, new PermissionId(), "CREATE_PERMISSION",PermissionType.COMMON, permissionMgntId, tenantId);
        Permission permission20 = new Permission(projectId, new PermissionId(), "VIEW_PERMISSION",PermissionType.COMMON, permissionMgntId, tenantId);
        Permission permission21 = new Permission(projectId, new PermissionId(), "EDIT_PERMISSION",PermissionType.COMMON, permissionMgntId, tenantId);
        Permission permission22 = new Permission(projectId, new PermissionId(), "DELETE_PERMISSION",PermissionType.COMMON, permissionMgntId, tenantId);

        PermissionId positionMgntId = new PermissionId();
        Permission permission32 = new Permission(projectId, positionMgntId, "POSITION_MANAGEMENT",PermissionType.COMMON, rootId, tenantId);
        Permission permission23 = new Permission(projectId, new PermissionId(), "CREATE_POSITION",PermissionType.COMMON, positionMgntId, tenantId);
        Permission permission24 = new Permission(projectId, new PermissionId(), "VIEW_POSITION",PermissionType.COMMON, positionMgntId, tenantId);
        Permission permission25 = new Permission(projectId, new PermissionId(), "EDIT_POSITION",PermissionType.COMMON, positionMgntId, tenantId);
        Permission permission26 = new Permission(projectId, new PermissionId(), "DELETE_POSITION",PermissionType.COMMON, positionMgntId, tenantId);

        Permission apiPermission = new Permission(tenantId, new PermissionId(), API_ACCESS,PermissionType.API, null);

        DomainRegistry.getPermissionRepository().add(apiPermission);
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
        DomainEventPublisher.instance().publish(new ProjectPermissionCreated(createdPermissionIds, tenantId, creatorId));
    }

    public static void addNewEndpoint(ProjectId projectId, EndpointId endpointId, PermissionId permissionId) {
        Optional<Permission> apiRoot = DomainRegistry.getPermissionRepository().getByQuery(new PermissionQuery(projectId, API_ACCESS)).findFirst();
        apiRoot.ifPresent(e->{
            Permission apiPermission = new Permission(projectId, permissionId, endpointId.getDomainId(),PermissionType.API,apiRoot.get().getPermissionId(), null);
            DomainRegistry.getPermissionRepository().add(apiPermission);
        });
    }

    public void replace(String name) {
        this.name = name;
    }
}
