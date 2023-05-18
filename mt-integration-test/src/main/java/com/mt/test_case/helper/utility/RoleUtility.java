package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.Role;
import com.mt.test_case.helper.pojo.SumTotal;
import java.util.Collections;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class RoleUtility {
    private static final ParameterizedTypeReference<SumTotal<Role>> reference =
        new ParameterizedTypeReference<>() {
        };

    private static String getUrl(Project project) {
        return UrlUtility.appendPath(TenantUtility.getTenantUrl(project), "roles");
    }

    public static Role createRandomRoleObj() {
        Role role = new Role();
        role.setName(RandomUtility.randomStringWithNum());
        role.setDescription(RandomUtility.randomStringWithNum());
        role.setCommonPermissionIds(Collections.emptySet());
        role.setApiPermissionIds(Collections.emptySet());
        role.setExternalPermissionIds(Collections.emptySet());
        return role;
    }

    public static ResponseEntity<SumTotal<Role>> readTenantRole(
        TenantContext tenantContext) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<Role> readTenantRoleById(
        TenantContext tenantContext, Role role) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, role.getId(), Role.class);
    }

    public static ResponseEntity<SumTotal<Role>> readTenantRoleWithQuery(
        TenantContext tenantContext, String query) {
        String accessUrl = getUrl(tenantContext.getProject());
        String url=UrlUtility.appendQuery(accessUrl, query);
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<Void> createTenantRole(
        TenantContext tenantContext, Role role) {
        String url = getUrl(tenantContext.getProject());
        return Utility.createResource(tenantContext.getCreator(), url, role);
    }

    public static ResponseEntity<Void> updateTenantRole(
        TenantContext tenantContext, Role role) {
        String url = getUrl(tenantContext.getProject());
        return Utility.updateResource(tenantContext.getCreator(), url, role, role.getId());
    }

    public static ResponseEntity<Void> deleteTenantRole(
        TenantContext tenantContext, Role role) {
        String url = getUrl(tenantContext.getProject());
        return Utility.deleteResource(tenantContext.getCreator(), url, role.getId());
    }

}
