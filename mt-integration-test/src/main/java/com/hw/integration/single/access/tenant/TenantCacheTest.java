package com.hw.integration.single.access.tenant;

import com.hw.helper.Cache;
import com.hw.helper.Client;
import com.hw.helper.ClientType;
import com.hw.helper.Endpoint;
import com.hw.helper.GrantType;
import com.hw.helper.SumTotal;
import com.hw.helper.utility.CacheUtility;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.RandomUtility;
import com.hw.helper.utility.TenantUtility;
import com.hw.helper.utility.TestContext;
import com.hw.integration.single.access.CommonTest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
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
        Assert.assertEquals(1, cache1.getVersion());
    }

    @Test
    public void tenant_update_cache_version_should_not_change() {
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
        Assert.assertEquals(0, cache1.getVersion());
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
    }

    @Test
    public void tenant_can_delete_assigned_cache() throws InterruptedException {
        //create cache
        Cache cacheObj = CacheUtility.createRandomCacheObj();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = Objects.requireNonNull(cache.getHeaders().getLocation()).toString();
        cacheObj.setId(cacheId);
        //create backend client
        Client randomClient = ClientUtility.createRandomClientObj();
        randomClient.setTypes(Collections.singleton(ClientType.BACKEND_APP));
        randomClient.setGrantTypeEnums(Collections.singleton(GrantType.CLIENT_CREDENTIALS));
        randomClient.setAccessTokenValiditySeconds(180);
        randomClient.setRefreshTokenValiditySeconds(null);
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext.getCreator(), randomClient,
                tenantContext.getProject().getId());
        String clientId = client.getHeaders().getLocation().toString();
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createRandomEndpointObj(clientId);
        randomEndpointObj.setWebsocket(false);
        randomEndpointObj.setCacheProfileId(cacheId);
        randomEndpointObj.setMethod("GET");
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
