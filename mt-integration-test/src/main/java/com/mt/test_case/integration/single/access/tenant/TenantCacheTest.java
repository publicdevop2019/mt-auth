package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.pojo.Cache;
import com.mt.test_case.helper.pojo.CacheControlValue;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.utility.CacheUtility;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.Utility;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache.getStatusCode());
        Assert.assertNotNull(UrlUtility.getId(cache));
    }

    @Test
    public void tenant_can_update_cache() {
        Cache cacheObj = CacheUtility.getValidNoCache();
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
        Cache cacheObj = CacheUtility.getValidNoCache();
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
        Assert.assertEquals(HttpStatus.OK, cache2.getStatusCode());
        Thread.sleep(10000);
        //read endpoint to verify cache id remove
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, randomEndpointObj);
        Assert.assertNull(endpointResponseEntity.getBody().getCacheProfileId());

    }

    @Test
    public void validation_create_valid() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> cache0 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache0.getStatusCode());
    }

    @Test
    public void validation_create_name() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        //null
        cacheObj.setName(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache.getStatusCode());
        //blank
        cacheObj.setName(" ");
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
        //empty
        cacheObj.setName("");
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
        //max length
        cacheObj.setName("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
        //invalid char
        cacheObj.setName("<>!,");
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_description() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        //null
        cacheObj.setDescription(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache.getStatusCode());
        //blank
        cacheObj.setDescription(" ");
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
        //empty
        cacheObj.setDescription("");
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
        //max length
        cacheObj.setDescription(
            "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
        //invalid char
        cacheObj.setDescription("<>!，");
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_cache_control() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(1L);
        //null
        cacheObj.setCacheControl(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache.getStatusCode());
        //empty
        cacheObj.setCacheControl(Collections.emptySet());
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
        //max length
        Set<String> collect =
            Arrays.stream(CacheControlValue.values()).map(e -> e.label).collect(Collectors.toSet());
        collect.add(CacheControlValue.NO_CACHE.label);
        cacheObj.setCacheControl(collect);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache3.getStatusCode());//200 due to set and enum
        //invalid value
        cacheObj.setCacheControl(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setCacheControl(collect);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_expires() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        //null
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache.getStatusCode());
        //min value
        cacheObj.setExpires(0L);
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
        //max value
        cacheObj.setExpires((long) Integer.MAX_VALUE);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setExpires(1L);
        cacheObj.setMaxAge(null);
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_create_max_age() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //null
        cacheObj.setMaxAge(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache.getStatusCode());
        //min value
        cacheObj.setMaxAge(0L);
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
        //max value
        cacheObj.setMaxAge((long) Integer.MAX_VALUE);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
        //if max age then no s max age
        cacheObj.setMaxAge(1L);
        cacheObj.setSmaxAge(1L);
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setMaxAge(1L);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_s_max_age() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //null
        cacheObj.setSmaxAge(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache.getStatusCode());
        //min value
        cacheObj.setSmaxAge(0L);
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
        //max value
        cacheObj.setSmaxAge((long) Integer.MAX_VALUE);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setSmaxAge(1L);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_vary() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //null
        cacheObj.setVary(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache.getStatusCode());
        //blank
        cacheObj.setVary(" ");
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
        //empty
        cacheObj.setVary("");
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
        //invalid value
        cacheObj.setVary(
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
        //present but allow cache false
        cacheObj.setVary("123");
        cacheObj.setAllowCache(false);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_allow_cache() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setAllowCache(null);
        //null
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache.getStatusCode());
        //true but no config
        cacheObj.setAllowCache(true);
        cacheObj.setEtag(null);
        cacheObj.setExpires(null);
        cacheObj.setSmaxAge(null);
        cacheObj.setMaxAge(null);
        cacheObj.setWeakValidation(null);
        cacheObj.setVary(null);
        cacheObj.setCacheControl(null);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_weak_validation() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //null
        cacheObj.setWeakValidation(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.OK, cache.getStatusCode());
        //true but etag null
        cacheObj.setWeakValidation(true);
        cacheObj.setEtag(null);
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
        //true but etag not true
        cacheObj.setWeakValidation(true);
        cacheObj.setEtag(false);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
        //present but allow cache false
        cacheObj.setWeakValidation(true);
        cacheObj.setEtag(true);
        cacheObj.setAllowCache(false);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_project_id() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        //null
        Project project1 = new Project();
        project1.setId("null");
        String url = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, cacheObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, resource.getStatusCode());
        //empty
        project1.setId("");
        String url3 = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource3 =
            Utility.createResource(tenantContext.getCreator(), url3, cacheObj);
        Assert.assertEquals(HttpStatus.FORBIDDEN, resource3.getStatusCode());
        //blank
        project1.setId(" ");
        String url4 = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource4 =
            Utility.createResource(tenantContext.getCreator(), url4, cacheObj);
        Assert.assertEquals(HttpStatus.FORBIDDEN, resource4.getStatusCode());
        //other tenant's project id
        project1.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url2 = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource2 =
            Utility.createResource(tenantContext.getCreator(), url2, cacheObj);
        Assert.assertEquals(HttpStatus.FORBIDDEN, resource2.getStatusCode());
    }

    @Test
    public void validation_update_name() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }

    @Test
    public void validation_update_description() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }

    @Test
    public void validation_update_cache_control() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid value
    }

    @Test
    public void validation_update_expires() {
        //null
        //min value
        //max value
    }

    @Test
    public void validation_update_max_age() {
        //null
        //min value
        //max value
    }

    @Test
    public void validation_update_s_max_age() {
        //null
        //min value
        //max value
    }

    @Test
    public void validation_update_vary() {
        //null
        //blank
        //empty
        //invalid value
    }

    @Test
    public void validation_update_allow_cache() {
        //null
        //false but other config present
    }

    @Test
    public void validation_update_weak_validation() {
        //null
        //true but etag not true
    }

    @Test
    public void validation_patch_name() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }

    @Test
    public void validation_patch_description() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
}
