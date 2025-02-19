package com.mt.helper.utility;

import com.mt.helper.TenantContext;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.Role;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

@Slf4j
public class RoleUtility {
    private static final ParameterizedTypeReference<SumTotal<Role>> reference =
        new ParameterizedTypeReference<>() {
        };

    public static String getUrl(Project project) {
        return HttpUtility.appendPath(TenantUtility.getTenantUrl(project), "roles");
    }

    public static Role createRandomValidRoleObj() {
        Role role = new Role();
        role.setName(RandomUtility.randomStringWithNum(25));
        log.info("role name is {}", role.getName());
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
        String url = HttpUtility.appendQuery(accessUrl, query);
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<Void> createTenantRole(
        TenantContext tenantContext, Role role) {
        return createTenantRole(tenantContext.getProject(), tenantContext.getCreator(), role);
    }

    public static ResponseEntity<Void> createTenantRole(
        Project project, User user, Role role) {
        String url = getUrl(project);
        return Utility.createResource(user, url, role);
    }

    public static ResponseEntity<Void> updateTenantRole(
        TenantContext tenantContext, Role role) {
        return updateTenantRole(tenantContext.getProject(), tenantContext.getCreator(), role);
    }

    public static ResponseEntity<Void> updateTenantRole(
        Project project, User user, Role role) {
        String url = getUrl(project);
        return Utility.updateResource(user, url, role, role.getId());
    }

    public static ResponseEntity<Void> patchTenantRole(
        TenantContext tenantContext, Role role, PatchCommand command) {
        String url = getUrl(tenantContext.getProject());
        return Utility.patchResource(tenantContext.getCreator(), url, command, role.getId());
    }

    public static ResponseEntity<Void> deleteTenantRole(
        TenantContext tenantContext, Role role) {
        String url = getUrl(tenantContext.getProject());
        return Utility.deleteResource(tenantContext.getCreator(), url, role.getId());
    }

}
