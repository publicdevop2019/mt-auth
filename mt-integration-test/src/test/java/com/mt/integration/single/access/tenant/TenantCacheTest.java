package com.mt.integration.single.access.tenant;

import com.mt.helper.TenantTest;
import com.mt.helper.pojo.Cache;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.utility.CacheUtility;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.UrlUtility;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith(SpringExtension.class)
@Slf4j
public class TenantCacheTest extends TenantTest {

    @Test
    public void tenant_can_create_cache() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
        Assertions.assertNotNull(UrlUtility.getId(cache));
    }

    @Test
    public void tenant_can_update_cache() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = UrlUtility.getId(cache);
        String newName = RandomUtility.randomStringWithNum();
        cacheObj.setName(newName);
        cacheObj.setId(cacheId);
        ResponseEntity<Void> cache2 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache2.getStatusCode());

        ResponseEntity<SumTotal<Cache>> read =
            CacheUtility.readTenantCacheById(tenantContext, cacheId);
        Cache cache3 = Objects.requireNonNull(read.getBody()).getData().get(0);
        log.debug("body {}", read.getBody());
        Assertions.assertEquals(1, cache3.getVersion().intValue());
        Assertions.assertEquals(newName, cache3.getName());
    }


    @Test
    public void tenant_can_view_cache() {
        ResponseEntity<SumTotal<Cache>> exchange4 =
            CacheUtility.readTenantCache(tenantContext);
        Assertions.assertEquals(HttpStatus.OK, exchange4.getStatusCode());
    }

    @Test
    public void tenant_can_delete_cache() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = UrlUtility.getId(cache);
        cacheObj.setId(cacheId);
        ResponseEntity<Void> cache2 = CacheUtility.deleteTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache2.getStatusCode());
        ResponseEntity<SumTotal<Cache>> cache3 =
            CacheUtility.readTenantCache(tenantContext);
        Optional<Cache> first =
            cache3.getBody().getData().stream().filter(e -> e.getId().equalsIgnoreCase(cacheId))
                .findFirst();
        Assertions.assertEquals(HttpStatus.OK, cache3.getStatusCode());
        Assertions.assertTrue(first.isEmpty());
    }

    @Test
    public void tenant_can_delete_assigned_cache() throws InterruptedException {
        //create cache
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = UrlUtility.getId(cache);
        cacheObj.setId(cacheId);
        //create backend client
        Client randomClient = ClientUtility.createValidBackendClient();
        randomClient.setResourceIndicator(false);
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, randomClient);
        String clientId = UrlUtility.getId(client);
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createValidGetEndpoint(clientId);
        randomEndpointObj.setCacheProfileId(cacheId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, randomEndpointObj);
        String endpointId = UrlUtility.getId(tenantEndpoint);
        randomEndpointObj.setId(endpointId);
        //delete cache
        ResponseEntity<Void> cache2 = CacheUtility.deleteTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache2.getStatusCode());
        Thread.sleep(5*1000);
        //read endpoint to verify cache id remove
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, randomEndpointObj);
        Assertions.assertNull(endpointResponseEntity.getBody().getCacheProfileId());

    }
}
