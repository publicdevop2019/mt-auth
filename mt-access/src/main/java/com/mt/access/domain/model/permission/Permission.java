package com.mt.access.domain.model.permission;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.permission.event.PermissionRemoved;
import com.mt.access.domain.model.permission.event.ProjectPermissionCreated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@EqualsAndHashCode(callSuper = true)
public class Permission extends Auditable {
    public static final String USER_MGMT = "USER_MGMT";
    public static final String PERMISSION_MGMT = "PERMISSION_MGMT";
    public static final String ROLE_MGMT = "ROLE_MGMT";
    public static final String API_MGMT = "API_MGMT";
    public static final String CLIENT_MGMT = "CLIENT_MGMT";
    public static final String PROJECT_INFO_MGMT = "PROJECT_INFO_MGMT";
    public static final String SUB_REQ_MGMT = "SUB_REQ_MGMT";
    public static final String ADMIN_MGMT = "ADMIN_MGMT";

    public static final Set<String> reservedName = new HashSet<>();
    public static final Set<String> reservedUIPermissionName = new HashSet<>();

    static {
        reservedUIPermissionName.add(USER_MGMT);
        reservedUIPermissionName.add(PERMISSION_MGMT);
        reservedUIPermissionName.add(ROLE_MGMT);
        reservedUIPermissionName.add(API_MGMT);
        reservedUIPermissionName.add(CLIENT_MGMT);
        reservedUIPermissionName.add(PROJECT_INFO_MGMT);
        reservedUIPermissionName.add(SUB_REQ_MGMT);
        reservedUIPermissionName.add(ADMIN_MGMT);
        reservedName.addAll(reservedUIPermissionName);

    }

    private String name;

    private String description;

    private PermissionId permissionId;

    private Set<PermissionId> linkedApiPermissionIds = new LinkedHashSet<>();

    private ProjectId projectId;
    private ProjectId tenantId;
    private Boolean shared;
    private PermissionType type;
    private Boolean systemCreate;

    private Permission() {
    }

    public static Permission fromDatabaseRow(Long id, Long createdAt, String createdBy,
                                             Long modifiedAt, String modifiedBy,
                                             Integer version,
                                             String name, String description, PermissionId domainId,
                                             ProjectId projectId,
                                             Boolean shared, Boolean systemCreate,
                                             ProjectId tenantId, PermissionType type) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setCreatedAt(createdAt);
        permission.setCreatedBy(createdBy);
        permission.setModifiedAt(modifiedAt);
        permission.setModifiedBy(modifiedBy);
        permission.setVersion(version);
        permission.setName(name);
        permission.setDescription(description);
        permission.setPermissionId(domainId);
        permission.setProjectId(projectId);
        permission.setShared(shared);
        permission.setSystemCreate(systemCreate);
        permission.setTenantId(tenantId);
        permission.setType(type);
        return permission;
    }

    /**
     * create api with one linked api permission
     *
     * @param projectId             project id
     * @param permissionId          new permission id
     * @param name                  permission name
     * @param type                  permission type
     * @param tenantId              tenant project id if exist
     * @param linkedApiPermissionId linked api permission id
     * @return permission
     */
    private static Permission autoCreateForProject(ProjectId projectId, PermissionId permissionId,
                                                   String name, PermissionType type,
                                                   @Nullable ProjectId tenantId,
                                                   @Nullable PermissionId linkedApiPermissionId) {
        Permission permission = new Permission();
        permission.setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        permission.setProjectId(projectId);
        permission.setPermissionId(permissionId);
        permission.setName(name);
        permission.setType(type);
        permission.setTenantId(tenantId);
        permission.setLinkedApiPermissionIds(
            linkedApiPermissionId == null ? null : Collections.singleton(linkedApiPermissionId));
        permission.setShared(false);
        permission.setSystemCreate(true);
        long milli = Instant.now().toEpochMilli();
        permission.setCreatedAt(milli);
        permission.setCreatedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        permission.setModifiedAt(milli);
        permission.setModifiedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        new PermissionValidator(new HttpValidationNotificationHandler(), permission).validate();
        return permission;
    }

    /**
     * create api with multiple linked api permission
     *
     * @param projectId             project id
     * @param permissionId          new permission id
     * @param name                  permission name
     * @param tenantId              tenant project id if exist
     * @param linkedApiPermissionId linked api permission id
     * @return permission
     */
    private static Permission autoCreateForProjectMulti(
        ProjectId projectId,
        PermissionId permissionId, String name,
        @Nullable ProjectId tenantId,
        @Nullable Set<PermissionId> linkedApiPermissionId
    ) {
        Permission permission = new Permission();
        permission.setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        permission.setPermissionId(permissionId);
        permission.setLinkedApiPermissionIds(linkedApiPermissionId);
        permission.setProjectId(projectId);
        permission.setTenantId(tenantId);
        permission.setName(name);
        permission.setType(PermissionType.COMMON);
        permission.setShared(false);
        permission.setSystemCreate(true);
        long milli = Instant.now().toEpochMilli();
        permission.setCreatedAt(milli);
        permission.setCreatedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        permission.setModifiedAt(milli);
        permission.setModifiedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        new PermissionValidator(new HttpValidationNotificationHandler(), permission).validate();
        return permission;
    }

    /**
     * used for auto create permission for new endpoint
     *
     * @param projectId    project id
     * @param permissionId permission id
     * @param endpointId   endpoint id
     * @param shared       if api is shared
     * @return permission aggregate
     */
    private static Permission autoCreateForEndpoint(ProjectId projectId, PermissionId permissionId,
                                                    String endpointId,
                                                    boolean shared) {
        Permission permission = new Permission();
        permission.setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        permission.setProjectId(projectId);
        permission.setPermissionId(permissionId);
        permission.setName(endpointId);
        permission.setType(PermissionType.API);
        permission.setShared(shared);
        permission.setSystemCreate(true);
        long milli = Instant.now().toEpochMilli();
        permission.setCreatedAt(milli);
        permission.setCreatedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        permission.setModifiedAt(milli);
        permission.setModifiedBy(AppConstant.DEFAULT_AUTO_ACTOR);
        new PermissionValidator(new HttpValidationNotificationHandler(), permission).validate();
        return permission;
    }

    public static Permission manualCreate(ProjectId projectId, PermissionId permissionId,
                                          String name, String description, PermissionType type,
                                          @Nullable ProjectId tenantId,
                                          @Nullable Set<PermissionId> linkedApiPermissionId) {
        Permission permission = new Permission();
        permission.setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        long milli = Instant.now().toEpochMilli();
        permission.setCreatedAt(milli);
        permission.setCreatedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        permission.setModifiedAt(milli);
        permission.setModifiedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        permission.setPermissionId(permissionId);
        permission.setLinkedApiPermissionIds(linkedApiPermissionId);
        permission.setProjectId(projectId);
        permission.setTenantId(tenantId);
        permission.setName(name);
        permission.setDescription(description);
        permission.setType(type);
        permission.setShared(false);
        permission.setSystemCreate(false);
        new PermissionValidator(new HttpValidationNotificationHandler(), permission).validate();
        return permission;
    }

    /**
     * create permissions for new project
     *
     * @param tenantId  tenant project that is creating
     * @param creatorId user id who create project
     */
    public static void onboardNewProject(ProjectId tenantId,
                                         UserId creatorId, TransactionContext context) {
        log.debug("start of creating new permissions");
        ProjectId projectId = new ProjectId(AppConstant.MT_AUTH_PROJECT_ID);
        //project
        Permission p0 = Permission
            .autoCreateForProject(projectId, new PermissionId(), PROJECT_INFO_MGMT,
                PermissionType.COMMON, tenantId, new PermissionId("0Y8HSHJC34BW"));
        //client
        Permission p1 = Permission
            .autoCreateForProjectMulti(projectId, new PermissionId(), CLIENT_MGMT,
                tenantId,
                Stream.of(
                        new PermissionId("0Y8HHJ47NBDP"), new PermissionId("0Y8HHJ47NBD4"),
                        new PermissionId("0Y8OYY45NEVK"), new PermissionId("0Y8HHJ47NBD6"),
                        new PermissionId("0Y8HHJ47NBD8"), new PermissionId("0Y8HHJ47NBD7")
                    )
                    .collect(Collectors.toSet()));
        //api
        Permission p2 = Permission
            .autoCreateForProjectMulti(projectId, new PermissionId(), API_MGMT,
                tenantId, Stream
                    .of(new PermissionId("0Y8MLZDBR4T3"), new PermissionId("0Y8U1QEXOG06"),
                        new PermissionId("0Y8HHJ47NBDS"), new PermissionId("0Y8HHJ47NBDM"),
                        new PermissionId("0Y8HHJ47NBDV"), new PermissionId("0Y8HHJ47NBDN"),
                        new PermissionId("0Y8HHJ47NBDO"), new PermissionId("0Y8M4UTZLTLI"),
                        new PermissionId("0Y8HHJ47NBDL"),
                        //cors apis
                        new PermissionId("0Y8OK4YM1LAW"), new PermissionId("0Y8OK4YXLDEC"),
                        new PermissionId("0Y8OK4YFSUFS"), new PermissionId("0Y8OK4YRZ3MC"),
                        //cache apis
                        new PermissionId("0Y8OKQGFNG2C"), new PermissionId("0Y8OKQGD5JPW"),
                        new PermissionId("0Y8OKQG3SFF7"), new PermissionId("0Y8OKQG9PXQM")
                    )
                    .collect(Collectors.toSet()));
        //role
        Permission p3 = Permission
            .autoCreateForProjectMulti(projectId, new PermissionId(), ROLE_MGMT,
                tenantId,
                Stream.of(
                        new PermissionId("0Y8HKE2QAIVF"), new PermissionId("0Y8HKE24FWUI"),
                        new PermissionId("0Y8HHJ47NBEY"), new PermissionId("0Y8HHJ47NBEX"),
                        new PermissionId("0Y8HKACDVMDL")
                    )
                    .collect(Collectors.toSet()));

        //permission
        Permission p4 = Permission
            .autoCreateForProjectMulti(projectId, new PermissionId(), PERMISSION_MGMT,
                tenantId,
                Stream.of(
                        new PermissionId("0Y8HHJ47NBEV"), new PermissionId("0Y8HHJ47NBEW"),
                        new PermissionId("0Y8HLUWMX2BX")
                    )
                    .collect(Collectors.toSet()));
        //user
        Permission p5 = Permission
            .autoCreateForProjectMulti(projectId, new PermissionId(), USER_MGMT,
                tenantId,
                Stream.of(
                        new PermissionId("0Y8HK4ZLA03Q"),
                        new PermissionId("0Y8HKEMUH34B"),
                        new PermissionId("0Y8X961RUK20"),
                        new PermissionId("0Y8X9628PDDY")
                    )
                    .collect(Collectors.toSet()));
        //sub request
        Permission p6 = Permission
            .autoCreateForProjectMulti(projectId, new PermissionId(), SUB_REQ_MGMT,
                tenantId, Stream.of(
                        new PermissionId("0Y8M0IG8RITC"),
                        new PermissionId("0Y8M0IQAUSZ8"),
                        new PermissionId("0Y8M0IQQ5FK0"),
                        new PermissionId("0Y8M0IR20GBI"),
                        new PermissionId("0Y8M0IRD8ZSN"),
                        new PermissionId("0Y8M4M3J9HJ4"),
                        new PermissionId("0Y8M0IRN8L4W")
                    )
                    .collect(Collectors.toSet()));
        //admin
        Permission p7 = Permission
            .autoCreateForProjectMulti(projectId, new PermissionId(), ADMIN_MGMT,
                tenantId, Stream.of(
                        new PermissionId("0Y8NY6E4KKD3"),
                        new PermissionId("0Y8NY6EHDATT"),
                        new PermissionId("0Y8NY6ESALN0")
                    )
                    .collect(Collectors.toSet()));

        Set<Permission> tobeStoredPermissions = new HashSet<>();
        tobeStoredPermissions.add(p0);
        tobeStoredPermissions.add(p1);
        tobeStoredPermissions.add(p2);
        tobeStoredPermissions.add(p3);
        tobeStoredPermissions.add(p4);
        tobeStoredPermissions.add(p5);
        tobeStoredPermissions.add(p6);
        tobeStoredPermissions.add(p7);
        DomainRegistry.getPermissionRepository().addAll(tobeStoredPermissions);

        //add new permission to event so role can link to it
        Set<Permission> createdPermissions = new HashSet<>();
        createdPermissions.add(p0);
        createdPermissions.add(p1);
        createdPermissions.add(p2);
        createdPermissions.add(p3);
        createdPermissions.add(p4);
        createdPermissions.add(p5);
        createdPermissions.add(p6);
        createdPermissions.add(p7);
        Set<PermissionId> linkedPermissionIds = createdPermissions.stream().flatMap(e -> {
            if (e.getLinkedApiPermissionIds() != null && !e.getLinkedApiPermissionIds().isEmpty()) {
                return e.linkedApiPermissionIds.stream();
            } else {
                return Stream.empty();
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<PermissionId> commonPermissionIds =
            createdPermissions.stream().map(Permission::getPermissionId)
                .collect(Collectors.toSet());
        context
            .append(new ProjectPermissionCreated(commonPermissionIds, linkedPermissionIds, tenantId,
                creatorId));
        log.debug("end of creating new permissions");
    }

    public static void addNewEndpoint(ProjectId projectId, EndpointId endpointId,
                                      PermissionId permissionId, boolean shared) {
        Permission apiPermission = Permission
            .autoCreateForEndpoint(projectId, permissionId, endpointId.getDomainId(), shared);
        DomainRegistry.getPermissionRepository().add(apiPermission);
    }

    private void setPermissionId(PermissionId permissionId) {
        Validator.notNull(permissionId);
        this.permissionId = permissionId;
    }

    private void setProjectId(ProjectId projectId) {
        Validator.notNull(projectId);
        this.projectId = projectId;
    }

    private void setTenantId(ProjectId tenantId) {
        this.tenantId = tenantId;
    }

    private void setShared(Boolean shared) {
        Validator.notNull(shared);
        this.shared = shared;
    }

    private void setType(PermissionType type) {
        Validator.notNull(type);
        this.type = type;
    }

    private void setSystemCreate(Boolean systemCreate) {
        Validator.notNull(systemCreate);
        this.systemCreate = systemCreate;
    }

    private void setName(String name) {
        Validator.validRequiredString(1, 50, name);
        this.name = name;
    }

    private void setDescription(String description) {
        Validator.validOptionalString(50, description);
        if (Checker.notNull(description)) {
            description = description.trim();
        }
        this.description = description;
    }

    private void setLinkedApiPermissionIds(Set<PermissionId> permissionIds) {
        Validator.validOptionalCollection(20, permissionIds);
        CommonUtility.updateCollection(this.linkedApiPermissionIds, permissionIds,
            () -> this.linkedApiPermissionIds = permissionIds);
    }

    private void updateName(String name) {
        if (Objects.equals(PermissionType.API, this.type)) {
            throw new DefinedRuntimeException("api, api root and project type's cannot be changed",
                "1049",
                HttpResponseCode.BAD_REQUEST);
        }
        if (Checker.isTrue(getSystemCreate())) {
            throw new DefinedRuntimeException("system created permission cannot be changed", "1050",
                HttpResponseCode.BAD_REQUEST);
        }
        setName(name);
    }

    public void remove(TransactionContext context) {
        if (Objects.equals(PermissionType.API, this.type)) {
            throw new DefinedRuntimeException("api type cannot be changed",
                "1051",
                HttpResponseCode.BAD_REQUEST);
        }
        DomainRegistry.getPermissionRepository().remove(this);
        context.append(new PermissionRemoved(this));
    }


    public void secureEndpointRemoveCleanUp(TransactionContext context) {
        DomainRegistry.getPermissionRepository().remove(this);
        context.append(new PermissionRemoved(this));
    }

    public boolean sameAs(Permission that) {
        return Objects.equals(name, that.name) &&
            Objects.equals(permissionId, that.permissionId) &&
            Objects.equals(linkedApiPermissionIds, that.linkedApiPermissionIds) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(shared, that.shared) && type == that.type &&
            Objects.equals(systemCreate, that.systemCreate);
    }

}
