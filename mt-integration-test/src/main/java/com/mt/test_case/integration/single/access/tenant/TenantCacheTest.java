package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.pojo.Cache;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.utility.CacheUtility;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.UrlUtility;
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
        Cache cacheObj = CacheUtility.createRandomCache();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache.getStatusCode());
        Assert.assertNotNull(UrlUtility.getId(cache));
    }

    @Test
    public void tenant_can_update_cache() {
        Cache cacheObj = CacheUtility.createRandomCache();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = UrlUtility.getId(cache);

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
    public void tenant_can_view_cache() {
        ResponseEntity<SumTotal<Cache>> exchange4 =
            CacheUtility.readTenantCache(tenantContext);
        Assert.assertEquals(HttpStatus.OK, exchange4.getStatusCode());
    }

    @Test
    public void tenant_can_delete_cache() {
        Cache cacheObj = CacheUtility.createRandomCache();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = UrlUtility.getId(cache);
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
        Cache cacheObj = CacheUtility.createRandomCache();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        String cacheId = UrlUtility.getId(cache);
        cacheObj.setId(cacheId);
        //create backend client
        Client randomClient = ClientUtility.createRandomBackendClientObj();
        ResponseEntity<Void> client =
            ClientUtility.createTenantClient(tenantContext, randomClient);
        String clientId = UrlUtility.getId(client);
        randomClient.setId(clientId);
        //create client's endpoint
        Endpoint randomEndpointObj = EndpointUtility.createRandomGetEndpointObj(clientId);
        randomEndpointObj.setCacheProfileId(cacheId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, randomEndpointObj);
        String endpointId = UrlUtility.getId(tenantEndpoint);
        randomEndpointObj.setId(endpointId);
        //delete cache
        ResponseEntity<Void> cache2 = CacheUtility.deleteTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache2.getStatusCode());
        Thread.sleep(10000);
        //read endpoint to verify cache id remove
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, randomEndpointObj);
        Assert.assertNull(endpointResponseEntity.getBody().getCacheProfileId());

    }

    @Test
    public void cache_validation_should_work() {
        //invalid cache control value
        Cache cacheObj = CacheUtility.createRandomCache();
        cacheObj.setCacheControl(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache.getStatusCode());
    }

    @Test
    public void validation_create_name(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
    @Test
    public void validation_create_description(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
    @Test
    public void validation_create_cache_control(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid value
    }
    @Test
    public void validation_create_expires(){
        //null
        //min value
        //max value
    }
    @Test
    public void validation_create_max_age(){
        //null
        //min value
        //max value
    }
    @Test
    public void validation_create_s_max_age(){
        //null
        //min value
        //max value
    }
    @Test
    public void validation_create_vary(){
        //null
        //blank
        //empty
        //invalid value
    }
    @Test
    public void validation_create_allow_cache(){
        //null
        //false but other config present
    }
    @Test
    public void validation_create_weak_validation(){
        //null
        //true but etag not true
    }

    @Test
    public void validation_update_name(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
    @Test
    public void validation_update_description(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
    @Test
    public void validation_update_cache_control(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid value
    }
    @Test
    public void validation_update_expires(){
        //null
        //min value
        //max value
    }
    @Test
    public void validation_update_max_age(){
        //null
        //min value
        //max value
    }
    @Test
    public void validation_update_s_max_age(){
        //null
        //min value
        //max value
    }
    @Test
    public void validation_update_vary(){
        //null
        //blank
        //empty
        //invalid value
    }
    @Test
    public void validation_update_allow_cache(){
        //null
        //false but other config present
    }
    @Test
    public void validation_update_weak_validation(){
        //null
        //true but etag not true
    }
    @Test
    public void validation_patch_name(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
    @Test
    public void validation_patch_description(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
}
