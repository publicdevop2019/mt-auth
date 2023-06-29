package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.PatchCommand;
import com.mt.test_case.helper.pojo.Permission;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import java.util.Collections;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class PermissionUtility {
    private static final ParameterizedTypeReference<SumTotal<Permission>> reference =
        new ParameterizedTypeReference<>() {
        };

    public static String getUrl(Project project) {
        return UrlUtility.appendPath(
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
        String url = UrlUtility.appendQuery(accessUrl, query);
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<SumTotal<Permission>> readTenantPermissionShared(
        TenantContext tenantContext) {
        String url = UrlUtility.appendPath(TenantUtility.getTenantUrl(tenantContext.getProject()),
            "permissions/shared");
        return Utility.readResource(tenantContext.getCreator(), url, reference);
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
