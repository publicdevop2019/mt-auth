package com.mt.access.domain.model.permission;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.EndpointId;
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
import java.util.*;
import java.util.stream.Collectors;

@Table
@Entity
@NoArgsConstructor
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "permissionRegion")
public class Permission extends Auditable {
    public static final String API_ACCESS = "API_ACCESS";
    public static final String VIEW_PROJECT_INFO = "VIEW_PROJECT_INFO";
    public static final String VIEW_CLIENT = "VIEW_CLIENT";
    public static final String VIEW_CLIENT_SUMMARY = "VIEW_CLIENT_SUMMARY";
    public static final String EDIT_CLIENT = "EDIT_CLIENT";
    public static final String DELETE_CLIENT = "DELETE_CLIENT";
    public static final String PATCH_CLIENT = "PATCH_CLIENT";
    public static final String CREATE_CLIENT = "CREATE_CLIENT";
    public static final String VIEW_API_SUMMARY = "VIEW_API_SUMMARY";
    public static final String VIEW_API = "VIEW_API";
    public static final String EDIT_API = "EDIT_API";
    public static final String DELETE_API = "DELETE_API";
    public static final String CREATE_API = "CREATE_API";
    public static final String PATCH_API = "PATCH_API";
    public static final String BATCH_DELETE_API = "BATCH_DELETE_API";
    public static final String CREATE_PERMISSION = "CREATE_PERMISSION";
    public static final String VIEW_PERMISSION_SUMMARY = "VIEW_PERMISSION_SUMMARY";
    public static final String VIEW_PERMISSION = "VIEW_PERMISSION";
    public static final String EDIT_PERMISSION = "EDIT_PERMISSION";
    public static final String DELETE_PERMISSION = "DELETE_PERMISSION";
    public static final String PATCH_PERMISSION = "PATCH_PERMISSION";
    public static final String VIEW_TENANT_USER_SUMMARY = "VIEW_TENANT_USER_SUMMARY";
    public static final String VIEW_TENANT_USER = "VIEW_TENANT_USER";
    public static final String EDIT_TENANT_USER = "EDIT_TENANT_USER";
    public static final String DELETE_ROLE = "DELETE_ROLE";
    public static final String EDIT_ROLE = "EDIT_ROLE";
    public static final String CREATE_ROLE = "CREATE_ROLE";
    public static final String VIEW_ROLE = "VIEW_ROLE";
    public static final String VIEW_ROLE_SUMMARY = "VIEW_ROLE_SUMMARY";
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
            @AttributeOverride(name = "domainId", column = @Column(name = "linkedPermissionId"))
    })
    private PermissionId linkedApiPermissionId;

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
    private boolean systemCreate = false;

    public Permission(ProjectId projectId, PermissionId permissionId, String name, PermissionType type, @Nullable PermissionId parentId, @Nullable ProjectId tenantId, @Nullable PermissionId linkedApiPermissionId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.permissionId = permissionId;
        this.linkedApiPermissionId = linkedApiPermissionId;
        this.parentId = parentId;
        this.projectId = projectId;
        this.tenantId = tenantId;
        this.name = name;
        this.type = type;
    }

    private static Permission autoCreate(ProjectId projectId, PermissionId permissionId, String name, PermissionType type, @Nullable PermissionId parentId, @Nullable ProjectId tenantId, @Nullable PermissionId linkedApiPermissionId) {
        Permission permission = new Permission(projectId, permissionId, name, type, parentId, tenantId, linkedApiPermissionId);
        permission.systemCreate = true;
        return permission;
    }

    public static void onboardNewProject(ProjectId projectId, ProjectId tenantId, UserId creatorId) {
        Set<Permission> createdPermissions = new HashSet<>();
        PermissionId rootId = new PermissionId();
        Permission p0 = Permission.autoCreate(projectId, rootId, tenantId.getDomainId(), PermissionType.PROJECT, null, tenantId, null);
        PermissionId projectMgntId = new PermissionId();
        Permission p1 = Permission.autoCreate(projectId, projectMgntId, "PROJECT_INFO_MNGMT", PermissionType.COMMON, rootId, tenantId, null);
        Permission p2 = Permission.autoCreate(projectId, new PermissionId(), VIEW_PROJECT_INFO, PermissionType.COMMON, projectMgntId, tenantId, new PermissionId("0Y8HHJ47NBEU"));
        Permission p3 = Permission.autoCreate(projectId, new PermissionId(), "EDIT_PROJECT_INFO", PermissionType.COMMON, projectMgntId, tenantId, null);
        PermissionId clientMgntId = new PermissionId();
        Permission p4 = Permission.autoCreate(projectId, clientMgntId, "CLIENT_MNGMT", PermissionType.COMMON, rootId, tenantId, null);
        Permission p5 = Permission.autoCreate(projectId, new PermissionId(), CREATE_CLIENT, PermissionType.COMMON, clientMgntId, tenantId, new PermissionId("0Y8HHJ47NBD6"));
        Permission p6 = Permission.autoCreate(projectId, new PermissionId(), VIEW_CLIENT, PermissionType.COMMON, clientMgntId, tenantId, new PermissionId("0Y8HHJ47NBDP"));
        Permission p7 = Permission.autoCreate(projectId, new PermissionId(), EDIT_CLIENT, PermissionType.COMMON, clientMgntId, tenantId, new PermissionId("0Y8HHJ47NBD7"));
        Permission p8 = Permission.autoCreate(projectId, new PermissionId(), DELETE_CLIENT, PermissionType.COMMON, clientMgntId, tenantId, new PermissionId("0Y8HHJ47NBD8"));
        Permission p9 = Permission.autoCreate(projectId, new PermissionId(), VIEW_CLIENT_SUMMARY, PermissionType.COMMON, clientMgntId, tenantId, new PermissionId("0Y8HHJ47NBD4"));
        Permission p10 = Permission.autoCreate(projectId, new PermissionId(), PATCH_CLIENT, PermissionType.COMMON, clientMgntId, tenantId, new PermissionId("0Y8HHJ47NBDQ"));

        PermissionId apiMgntId = new PermissionId();
        Permission p11 = Permission.autoCreate(projectId, apiMgntId, "API_MNGMT", PermissionType.COMMON, rootId, tenantId, null);
        Permission p12 = Permission.autoCreate(projectId, new PermissionId(), VIEW_API_SUMMARY, PermissionType.COMMON, apiMgntId, tenantId, new PermissionId("0Y8HHJ47NBDM"));
        Permission p13 = Permission.autoCreate(projectId, new PermissionId(), VIEW_API, PermissionType.COMMON, apiMgntId, tenantId, new PermissionId("0Y8HHJ47NBDS"));
        Permission p14 = Permission.autoCreate(projectId, new PermissionId(), EDIT_API, PermissionType.COMMON, apiMgntId, tenantId, new PermissionId("0Y8HHJ47NBDN"));
        Permission p15 = Permission.autoCreate(projectId, new PermissionId(), DELETE_API, PermissionType.COMMON, apiMgntId, tenantId, new PermissionId("0Y8HHJ47NBDO"));
        Permission p16 = Permission.autoCreate(projectId, new PermissionId(), CREATE_API, PermissionType.COMMON, apiMgntId, tenantId, new PermissionId("0Y8HHJ47NBDL"));
        Permission p17 = Permission.autoCreate(projectId, new PermissionId(), PATCH_API, PermissionType.COMMON, apiMgntId, tenantId, new PermissionId("0Y8HHJ47NBDW"));
        Permission p18 = Permission.autoCreate(projectId, new PermissionId(), BATCH_DELETE_API, PermissionType.COMMON, apiMgntId, tenantId, new PermissionId("0Y8HHJ47NBDV"));
        Permission p36 = Permission.autoCreate(projectId, new PermissionId(), "VIEW_CORS_SUMMARY", PermissionType.COMMON, apiMgntId, tenantId, new PermissionId("0Y8HHJ47NBEH"));
        Permission p37 = Permission.autoCreate(projectId, new PermissionId(), "VIEW_CACHE_SUMMARY", PermissionType.COMMON, apiMgntId, tenantId, new PermissionId("0Y8HHJ47NBEM"));

        PermissionId roleMgntId = new PermissionId();
        Permission p19 = Permission.autoCreate(projectId, roleMgntId, "ROLE_MNGMT", PermissionType.COMMON, rootId, tenantId, null);
        Permission p20 = Permission.autoCreate(projectId, new PermissionId(), DELETE_ROLE, PermissionType.COMMON, roleMgntId, tenantId, new PermissionId("0Y8HKE2QAIVF"));
        Permission p21 = Permission.autoCreate(projectId, new PermissionId(), EDIT_ROLE, PermissionType.COMMON, roleMgntId, tenantId, new PermissionId("0Y8HKE24FWUI"));
        Permission p22 = Permission.autoCreate(projectId, new PermissionId(), CREATE_ROLE, PermissionType.COMMON, roleMgntId, tenantId, new PermissionId("0Y8HHJ47NBEY"));
        Permission p23 = Permission.autoCreate(projectId, new PermissionId(), VIEW_ROLE, PermissionType.COMMON, roleMgntId, tenantId, new PermissionId("0Y8HKACDVMDL"));
        Permission p24 = Permission.autoCreate(projectId, new PermissionId(), VIEW_ROLE_SUMMARY, PermissionType.COMMON, roleMgntId, tenantId, new PermissionId("0Y8HHJ47NBEX"));


        PermissionId permissionMgntId = new PermissionId();
        Permission p25 = Permission.autoCreate(projectId, permissionMgntId, "PERMISSION_MNGMT", PermissionType.COMMON, rootId, tenantId, null);
        Permission p26 = Permission.autoCreate(projectId, new PermissionId(), CREATE_PERMISSION, PermissionType.COMMON, permissionMgntId, tenantId, new PermissionId("0Y8HHJ47NBEW"));
        Permission p27 = Permission.autoCreate(projectId, new PermissionId(), VIEW_PERMISSION_SUMMARY, PermissionType.COMMON, permissionMgntId, tenantId, new PermissionId("0Y8HHJ47NBEV"));
        Permission p28 = Permission.autoCreate(projectId, new PermissionId(), VIEW_PERMISSION, PermissionType.COMMON, permissionMgntId, tenantId, new PermissionId("0Y8HLUWG1UJ8"));
        Permission p29 = Permission.autoCreate(projectId, new PermissionId(), EDIT_PERMISSION, PermissionType.COMMON, permissionMgntId, tenantId, new PermissionId("0Y8HLUWKQEJ1"));
        Permission p30 = Permission.autoCreate(projectId, new PermissionId(), DELETE_PERMISSION, PermissionType.COMMON, permissionMgntId, tenantId, new PermissionId("0Y8HLUWOH91P"));
        Permission p31 = Permission.autoCreate(projectId, new PermissionId(), PATCH_PERMISSION, PermissionType.COMMON, permissionMgntId, tenantId, new PermissionId("0Y8HLUWMX2BX"));

        PermissionId positionMgntId = new PermissionId();
        Permission p32 = Permission.autoCreate(projectId, positionMgntId, "USER_MNGMT", PermissionType.COMMON, rootId, tenantId, null);
        Permission p33 = Permission.autoCreate(projectId, new PermissionId(), VIEW_TENANT_USER_SUMMARY, PermissionType.COMMON, positionMgntId, tenantId, new PermissionId("0Y8HK4ZLA03Q"));
        Permission p34 = Permission.autoCreate(projectId, new PermissionId(), VIEW_TENANT_USER, PermissionType.COMMON, positionMgntId, tenantId, new PermissionId("0Y8HKEMUH34B"));
        Permission p35 = Permission.autoCreate(projectId, new PermissionId(), EDIT_TENANT_USER, PermissionType.COMMON, positionMgntId, tenantId, new PermissionId("0Y8HKEMWNQX7"));

        Permission apiPermission = Permission.autoCreate(tenantId, new PermissionId(), API_ACCESS, PermissionType.API_ROOT, null, null, null);

        DomainRegistry.getPermissionRepository().add(apiPermission);
        DomainRegistry.getPermissionRepository().add(p0);
        DomainRegistry.getPermissionRepository().add(p1);
        DomainRegistry.getPermissionRepository().add(p2);
        DomainRegistry.getPermissionRepository().add(p3);
        DomainRegistry.getPermissionRepository().add(p4);
        DomainRegistry.getPermissionRepository().add(p5);
        DomainRegistry.getPermissionRepository().add(p6);
        DomainRegistry.getPermissionRepository().add(p7);
        DomainRegistry.getPermissionRepository().add(p8);
        DomainRegistry.getPermissionRepository().add(p9);
        DomainRegistry.getPermissionRepository().add(p10);
        DomainRegistry.getPermissionRepository().add(p11);
        DomainRegistry.getPermissionRepository().add(p12);
        DomainRegistry.getPermissionRepository().add(p13);
        DomainRegistry.getPermissionRepository().add(p14);
        DomainRegistry.getPermissionRepository().add(p15);
        DomainRegistry.getPermissionRepository().add(p16);
        DomainRegistry.getPermissionRepository().add(p17);
        DomainRegistry.getPermissionRepository().add(p18);
        DomainRegistry.getPermissionRepository().add(p19);
        DomainRegistry.getPermissionRepository().add(p20);
        DomainRegistry.getPermissionRepository().add(p21);
        DomainRegistry.getPermissionRepository().add(p22);
        DomainRegistry.getPermissionRepository().add(p23);
        DomainRegistry.getPermissionRepository().add(p24);
        DomainRegistry.getPermissionRepository().add(p25);
        DomainRegistry.getPermissionRepository().add(p26);
        DomainRegistry.getPermissionRepository().add(p27);
        DomainRegistry.getPermissionRepository().add(p28);
        DomainRegistry.getPermissionRepository().add(p29);
        DomainRegistry.getPermissionRepository().add(p30);
        DomainRegistry.getPermissionRepository().add(p31);
        DomainRegistry.getPermissionRepository().add(p32);
        DomainRegistry.getPermissionRepository().add(p33);
        DomainRegistry.getPermissionRepository().add(p34);
        DomainRegistry.getPermissionRepository().add(p35);
        DomainRegistry.getPermissionRepository().add(p36);
        DomainRegistry.getPermissionRepository().add(p37);
        createdPermissions.add(p0);
        createdPermissions.add(p1);
        createdPermissions.add(p2);
        createdPermissions.add(p3);
        createdPermissions.add(p4);
        createdPermissions.add(p5);
        createdPermissions.add(p6);
        createdPermissions.add(p7);
        createdPermissions.add(p8);
        createdPermissions.add(p9);
        createdPermissions.add(p10);
        createdPermissions.add(p11);
        createdPermissions.add(p12);
        createdPermissions.add(p13);
        createdPermissions.add(p14);
        createdPermissions.add(p15);
        createdPermissions.add(p16);
        createdPermissions.add(p17);
        createdPermissions.add(p18);
        createdPermissions.add(p19);
        createdPermissions.add(p20);
        createdPermissions.add(p21);
        createdPermissions.add(p22);
        createdPermissions.add(p23);
        createdPermissions.add(p24);
        createdPermissions.add(p25);
        createdPermissions.add(p26);
        createdPermissions.add(p27);
        createdPermissions.add(p28);
        createdPermissions.add(p29);
        createdPermissions.add(p30);
        createdPermissions.add(p31);
        createdPermissions.add(p32);
        createdPermissions.add(p33);
        createdPermissions.add(p34);
        createdPermissions.add(p35);
        createdPermissions.add(p36);
        createdPermissions.add(p37);
        Set<PermissionId> collect = createdPermissions.stream().flatMap(e -> {
            if (e.getLinkedApiPermissionId() != null) {
                return List.of(e.getPermissionId(), e.getLinkedApiPermissionId()).stream();
            } else {
                return List.of(e.getPermissionId()).stream();
            }
        }).collect(Collectors.toSet());
        DomainEventPublisher.instance().publish(new ProjectPermissionCreated(collect, tenantId, creatorId));
    }

    public static void addNewEndpoint(ProjectId projectId, EndpointId endpointId, PermissionId permissionId) {
        Optional<Permission> apiRoot = DomainRegistry.getPermissionRepository().getByQuery(new PermissionQuery(projectId, API_ACCESS)).findFirst();
        apiRoot.ifPresent(e -> {
            Permission apiPermission = Permission.autoCreate(projectId, permissionId, endpointId.getDomainId(), PermissionType.API, apiRoot.get().getPermissionId(), null, null);
            DomainRegistry.getPermissionRepository().add(apiPermission);
        });
    }

    public void replace(String name) {
        if (List.of(PermissionType.API, PermissionType.API_ROOT, PermissionType.PROJECT).contains(this.type)) {
            throw new IllegalStateException("api, api root and project type's cannot be changed");
        }
        this.name = name;
    }

    public void remove() {
        if (List.of(PermissionType.API, PermissionType.API_ROOT, PermissionType.PROJECT).contains(this.type)) {
            throw new IllegalStateException("api, api root and project type's cannot be changed");
        }
        DomainRegistry.getPermissionRepository().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Permission that = (Permission) o;
        return Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), permissionId);
    }
}
