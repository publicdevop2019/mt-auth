package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.pojo.Cors;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.AppConstant;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class CorsUtility {
    public static Cors createRandomCorsObj() {
        Cors cors = new Cors();
        cors.setName(RandomUtility.randomStringWithNum());
        cors.setDescription(RandomUtility.randomStringWithNumNullable());
        cors.setAllowCredentials(RandomUtility.randomBoolean());
        String s = RandomUtility.randomLocalHostUrl();
        String s2 = RandomUtility.randomLocalHostUrl();
        HashSet<String> strings = new HashSet<>();
        strings.add(s);
        strings.add(s2);
        cors.setAllowOrigin(strings);
        cors.setMaxAge(RandomUtility.randomLong());
        cors.setAllowedHeaders(Collections.singleton(RandomUtility.randomStringWithNum()));
        cors.setExposedHeaders(Collections.singleton(RandomUtility.randomStringWithNum()));
        return cors;
    }

    public static ResponseEntity<Void> createTenantCors(TenantUtility.TenantContext tenantContext,
                                                        Cors cors) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Cors> request =
            new HttpEntity<>(cors, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(AppConstant.TENANT_PROJECTS_PREFIX,
                        tenantContext.getProject().getId(),
                        "/cors")),
                HttpMethod.POST, request,
                Void.class);
    }

    public static ResponseEntity<Void> updateTenantCors(TenantUtility.TenantContext tenantContext,
                                                        Cors cors) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Cors> request =
            new HttpEntity<>(cors, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(AppConstant.TENANT_PROJECTS_PREFIX,
                        tenantContext.getProject().getId(),
                        "/cors/" + cors.getId())),
                HttpMethod.PUT, request,
                Void.class);
    }

    public static ResponseEntity<Void> deleteTenantCors(TenantUtility.TenantContext tenantContext,
                                                        Cors cors) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Void> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(AppConstant.TENANT_PROJECTS_PREFIX,
                        tenantContext.getProject().getId(),
                        "/cors/" + cors.getId())),
                HttpMethod.DELETE, request,
                Void.class);
    }

    public static ResponseEntity<SumTotal<Cors>> readTenantCors(
        TenantUtility.TenantContext tenantContext) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/cors")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
    }
}
