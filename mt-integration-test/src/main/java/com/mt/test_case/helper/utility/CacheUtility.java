package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.Cache;
import com.mt.test_case.helper.pojo.CacheControlValue;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import java.util.HashSet;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class CacheUtility {
    private static final ParameterizedTypeReference<SumTotal<Cache>> reference =
        new ParameterizedTypeReference<>() {
        };

    private static String getUrl(Project project) {
        return UrlUtility.appendPath(TenantUtility.getTenantUrl(project), "cache");
    }

    public static ResponseEntity<SumTotal<Cache>> readTenantCache(
        TenantContext tenantContext) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<Void> createTenantCache(TenantContext tenantContext, Cache cache) {
        String url = getUrl(tenantContext.getProject());
        return Utility.createResource(tenantContext.getCreator(), url, cache);
    }

    public static ResponseEntity<Void> updateTenantCache(TenantContext tenantContext,
                                                         Cache cache) {
        String url = getUrl(tenantContext.getProject());
        return Utility.updateResource(tenantContext.getCreator(), url, cache, cache.getId());
    }

    public static ResponseEntity<Void> deleteTenantCache(
        TenantContext tenantContext,
        Cache cache
    ) {
        String url = getUrl(tenantContext.getProject());
        return Utility.deleteResource(tenantContext.getCreator(), url, cache.getId());
    }

    public static Cache createRandomCache() {
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
