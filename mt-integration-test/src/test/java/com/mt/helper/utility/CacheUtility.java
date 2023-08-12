package com.mt.helper.utility;

import com.mt.helper.TenantContext;
import com.mt.helper.pojo.Cache;
import com.mt.helper.pojo.CacheControlValue;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.SumTotal;
import java.util.HashSet;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class CacheUtility {
    private static final ParameterizedTypeReference<SumTotal<Cache>> reference =
        new ParameterizedTypeReference<>() {
        };

    public static String getUrl(Project project) {
        return HttpUtility.appendPath(TenantUtility.getTenantUrl(project), "cache");
    }

    public static ResponseEntity<SumTotal<Cache>> readTenantCache(
        TenantContext tenantContext) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<SumTotal<Cache>> readTenantCacheById(
        TenantContext tenantContext, String id) {
        String url = HttpUtility.appendQuery(getUrl(tenantContext.getProject()), "query=id:" + id);
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

    public static ResponseEntity<Void> patchTenantCache(TenantContext tenantContext,
                                                        Cache cache, PatchCommand command) {
        String url = getUrl(tenantContext.getProject());
        return Utility.patchResource(tenantContext.getCreator(), url, command, cache.getId());
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

    public static Cache getValidNoCache() {
        Cache randomCache = createRandomCache();
        randomCache.setCacheControl(null);
        randomCache.setEtag(null);
        randomCache.setExpires(null);
        randomCache.setAllowCache(false);
        return randomCache;
    }
}
