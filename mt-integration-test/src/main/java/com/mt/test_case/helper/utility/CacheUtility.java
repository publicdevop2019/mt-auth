package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.pojo.Cache;
import com.mt.test_case.helper.pojo.CacheControlValue;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.AppConstant;
import java.util.HashSet;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class CacheUtility {
    public static ResponseEntity<SumTotal<Cache>> readTenantCache(
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
                        "/cache")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
    }

    public static ResponseEntity<Void> createTenantCache(
        TenantUtility.TenantContext tenantContext, Cache cacheObj) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Cache> request =
            new HttpEntity<>(cacheObj, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/cache")),
                HttpMethod.POST, request,
                Void.class);
    }

    public static ResponseEntity<Void> updateTenantCache(
        TenantUtility.TenantContext tenantContext, Cache cacheObj) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Cache> request =
            new HttpEntity<>(cacheObj, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/cache/" + cacheObj.getId())),
                HttpMethod.PUT, request,
                Void.class);
    }

    public static ResponseEntity<Void> deleteTenantCache(
        TenantUtility.TenantContext tenantContext, Cache cacheObj) {
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Cache> request =
            new HttpEntity<>(cacheObj, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/cache/" + cacheObj.getId())),
                HttpMethod.DELETE, request,
                Void.class);
    }

    public static Cache createRandomCacheObj() {
        Cache cache = new Cache();
        cache.setName(RandomUtility.randomStringWithNum());
        cache.setAllowCache(RandomUtility.randomBoolean());

        HashSet<String> objects = new HashSet<>();
        objects.add(RandomUtility.randomEnum(CacheControlValue.values()).label);
        objects.add(RandomUtility.randomEnum(CacheControlValue.values()).label);
        objects.add(RandomUtility.randomEnum(CacheControlValue.values()).label);
        cache.setCacheControl(objects);
        cache.setEtag(RandomUtility.randomBoolean());
        cache.setExpires(RandomUtility.randomLong());
        return cache;
    }
}
