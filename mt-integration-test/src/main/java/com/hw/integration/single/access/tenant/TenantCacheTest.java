package com.hw.integration.single.access.tenant;

import com.hw.helper.Cache;
import com.hw.helper.Client;
import com.hw.helper.Endpoint;
import com.hw.helper.SumTotal;
import com.hw.helper.utility.CacheUtility;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.RandomUtility;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantCacheTest extends TenantTest {

    @Test
    public void tenant_can_create_cache() {
        Cache cacheObj = CacheUtility.createRandomCacheObj();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache.getStatusCode());
        Assert.assertNotNull(Objects.requireNonNull(cache.getHeaders().getLocation()).toString());
    }

    @Test
    public void tenant_can_update_cache() {
        Cache cacheObj = CacheUtility.createRandomCacheObj();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = Objects.requireNonNull(cache.getHeaders().getLocation()).toString();

        cacheObj.setName(RandomUtility.randomStringWithNum());
        cacheObj.setId(cacheId);
        ResponseEntity<Void> cache2 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        ResponseEntity<SumTotal<Cache>> read =
            CacheUtility.readTenantCache(tenantContext);
        List<Cache> collect = Objects.requireNonNull(read.getBody()).getData().stream()
            .filter(e -> e.getId().equalsIgnoreCase(cacheId)).collect(
                Collectors.toList());
        Cache cache1 = collect.get(0);
        Assert.assertEquals(HttpStatus.OK, cache2.getStatusCode());
        Assert.assertEquals(1, cache1.getVersion().intValue());
    }

    @Test
    public void tenant_update_cache_no_change_version_should_not_change() {
        Cache cacheObj = CacheUtility.createRandomCacheObj();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = Objects.requireNonNull(cache.getHeaders().getLocation()).toString();
        cacheObj.setId(cacheId);
        CacheUtility.updateTenantCache(tenantContext, cacheObj);
        CacheUtility.updateTenantCache(tenantContext, cacheObj);
        ResponseEntity<SumTotal<Cache>> read =
            CacheUtility.readTenantCache(tenantContext);
        List<Cache> collect = Objects.requireNonNull(read.getBody()).getData().stream()
            .filter(e -> e.getId().equalsIgnoreCase(cacheId)).collect(
                Collectors.toList());
        Cache cache1 = collect.get(0);
        Assert.assertEquals(0, cache1.getVersion().intValue());
    }

    @Test
    public void tenant_can_view_cache() {
        ResponseEntity<SumTotal<Cache>> exchange4 =
            CacheUtility.readTenantCache(tenantContext);
        Assert.assertEquals(HttpStatus.OK, exchange4.getStatusCode());
    }

    @Test
    public void tenant_can_delete_cache() {
        Cache cacheObj = CacheUtility.createRandomCacheObj();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = Objects.requireNonNull(cache.getHeaders().getLocation()).toString();
        cacheObj.setId(cacheId);
        ResponseEntity<Void> cache2 = CacheUtility.deleteTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache2.getStatusCode());
        ResponseEntity<SumTotal<Cache>> cache3 =
            CacheUtility.readTenantCache(tenantContext);
        Optional<Cache> first =
            cache3.getBody().getData().stream().filter(e -> e.getId().equalsIgnoreCase(cacheId))
                .findFirst();
        Assert.assertEquals(HttpStatus.OK, cache3.getStatusCode());
        Assert.assertTrue(first.isEmpty());
    }

    @Test
    public void tenant_can_delete_assigned_cache() throws InterruptedException {
        //create cache
        Cache cacheObj = CacheUtility.createRandomCacheObj();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = Objects.requireNonNull(cache.getHeaders().getLocation()).toString();
        cacheObj.setId(cacheId);
        //create backend client
        Client randomClient = ClientUtility.createRandomBackendClientObj();
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext.getCreator(), randomClient,
                tenantContext.getProject().getId());
        String clientId = client.getHeaders().getLocation().toString();
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createRandomGetEndpointObj(clientId);
        randomEndpointObj.setCacheProfileId(cacheId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext.getCreator(), randomEndpointObj,
                tenantContext.getProject().getId());
        String endpointId = tenantEndpoint.getHeaders().getLocation().toString();
        //delete cache
        ResponseEntity<Void> cache2 = CacheUtility.deleteTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache2.getStatusCode());
        Thread.sleep(10000);
        //read endpoint to verify cache id remove
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext.getCreator(), endpointId,
                tenantContext.getProject().getId());
        Assert.assertNull(endpointResponseEntity.getBody().getCacheProfileId());

    }

    @Test
    public void cache_validation_should_work() {
        //invalid cache control value
        Cache cacheObj = CacheUtility.createRandomCacheObj();
        cacheObj.setCacheControl(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache.getStatusCode());
    }
}
