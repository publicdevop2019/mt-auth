package com.hw.helper.utility;

import static com.hw.helper.AppConstant.TENANT_PROJECTS_PREFIX;

import com.hw.helper.Permission;
import com.hw.helper.SumTotal;
import java.util.Collections;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class PermissionUtility {
    public static Permission createRandomPermissionObj() {
        Permission permission = new Permission();
        permission.setName(RandomUtility.randomStringWithNum());
        permission.setLinkedApiIds(Collections.emptyList());
        return permission;
    }

    public static ResponseEntity<SumTotal<Permission>> readTenantPermission(
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
                        "/permissions")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
    }

    public static ResponseEntity<Permission> readTenantPermissionById(
        TenantUtility.TenantContext tenantContext, Permission permission) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/permissions/" + permission.getId())),
                HttpMethod.GET, request,
                Permission.class);
    }

    public static ResponseEntity<SumTotal<Permission>> readTenantPermissionWithQuery(
        TenantUtility.TenantContext tenantContext, String query) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        String accessUrl = UrlUtility.getAccessUrl(
            UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                "/permissions"));
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.appendQuery(accessUrl, query),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
    }
    public static ResponseEntity<SumTotal<Permission>> readTenantPermissionShared(
        TenantUtility.TenantContext tenantContext) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        String accessUrl = UrlUtility.getAccessUrl("/permissions/shared");
        return TestContext.getRestTemplate()
            .exchange(accessUrl,
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
    }

    public static ResponseEntity<Void> createTenantPermission(
        TenantUtility.TenantContext tenantContext, Permission permission) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Permission> request =
            new HttpEntity<>(permission, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/permissions")),
                HttpMethod.POST, request,
                Void.class);
    }

    public static ResponseEntity<Void> updateTenantPermission(
        TenantUtility.TenantContext tenantContext, Permission permission) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Permission> request =
            new HttpEntity<>(permission, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/permissions/" + permission.getId())),
                HttpMethod.PUT, request,
                Void.class);
    }

    public static ResponseEntity<Void> deleteTenantPermission(
        TenantUtility.TenantContext tenantContext, Permission permission) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Permission> request =
            new HttpEntity<>(permission, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/permissions/" + permission.getId())),
                HttpMethod.DELETE, request,
                Void.class);
    }

}
