package com.mt.helper.utility;

import com.mt.helper.TenantContext;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Permission;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import java.util.Collections;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class PermissionUtility {
    private static final ParameterizedTypeReference<SumTotal<Permission>> reference =
        new ParameterizedTypeReference<>() {
        };

    public static String getUrl(Project project) {
        return HttpUtility.appendPath(
            TenantUtility.getTenantUrl(project), "permissions");
    }

    public static Permission createRandomPermissionObj() {
        Permission permission = new Permission();
        permission.setName(RandomUtility.randomStringWithNum());
        permission.setLinkedApiIds(Collections.emptyList());
        return permission;
    }

    public static ResponseEntity<SumTotal<Permission>> readTenantPermission(
        TenantContext tenantContext) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<Permission> readTenantPermissionById(
        TenantContext tenantContext, Permission permission) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, permission.getId(),
            Permission.class);
    }

    public static ResponseEntity<SumTotal<Permission>> readTenantPermissionWithQuery(
        TenantContext tenantContext, String query) {
        String accessUrl = getUrl(tenantContext.getProject());
        String url = HttpUtility.appendQuery(accessUrl, query);
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<SumTotal<Permission>> readTenantPermissionShared(
        TenantContext tenantContext) {
        return readTenantPermissionShared(tenantContext.getProject(), tenantContext.getCreator());
    }

    public static ResponseEntity<SumTotal<Permission>> readTenantPermissionShared(
        Project project, User user) {
        String url = HttpUtility.appendPath(TenantUtility.getTenantUrl(project),
            "permissions/shared");
        return Utility.readResource(user, url, reference);
    }

    public static ResponseEntity<Void> createTenantPermission(
        TenantContext tenantContext, Permission permission) {
        String url = getUrl(tenantContext.getProject());
        return Utility.createResource(tenantContext.getCreator(), url, permission);
    }

    public static ResponseEntity<Void> updateTenantPermission(
        TenantContext tenantContext, Permission permission) {
        String url = getUrl(tenantContext.getProject());
        return Utility.updateResource(tenantContext.getCreator(), url, permission,
            permission.getId());
    }

    public static ResponseEntity<Void> deleteTenantPermission(
        TenantContext tenantContext, Permission permission) {
        String url = getUrl(tenantContext.getProject());
        return Utility.deleteResource(tenantContext.getCreator(), url, permission.getId());
    }

    public static ResponseEntity<Void> patchTenantPermission(
        TenantContext tenantContext, Permission permission, PatchCommand command) {
        String url = getUrl(tenantContext.getProject());
        return Utility.patchResource(tenantContext.getCreator(), url, command, permission.getId());
    }

}
