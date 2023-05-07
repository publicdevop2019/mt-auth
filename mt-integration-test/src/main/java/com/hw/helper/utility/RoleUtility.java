package com.hw.helper.utility;

import static com.hw.helper.AppConstant.TENANT_PROJECTS_PREFIX;

import com.hw.helper.Role;
import com.hw.helper.SumTotal;
import java.util.Collections;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class RoleUtility {
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
        TenantUtility.TenantContext tenantContext) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/roles")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
    }

    public static ResponseEntity<Role> readTenantRoleById(
        TenantUtility.TenantContext tenantContext, Role role) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/roles/" + role.getId())),
                HttpMethod.GET, request,
                Role.class);
    }

    public static ResponseEntity<SumTotal<Role>> readTenantRoleWithQuery(
        TenantUtility.TenantContext tenantContext, String query) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        String accessUrl = UrlUtility.getAccessUrl(
            UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                "/roles"));
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.appendQuery(accessUrl, query),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
    }

    public static ResponseEntity<Void> createTenantRole(
        TenantUtility.TenantContext tenantContext, Role role) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Role> request =
            new HttpEntity<>(role, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/roles")),
                HttpMethod.POST, request,
                Void.class);
    }

    public static ResponseEntity<Void> updateTenantRole(
        TenantUtility.TenantContext tenantContext, Role role) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Role> request =
            new HttpEntity<>(role, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/roles/" + role.getId())),
                HttpMethod.PUT, request,
                Void.class);
    }

    public static ResponseEntity<Void> deleteTenantRole(
        TenantUtility.TenantContext tenantContext, Role role) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Role> request =
            new HttpEntity<>(role, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/roles/" + role.getId())),
                HttpMethod.DELETE, request,
                Void.class);
    }

}
