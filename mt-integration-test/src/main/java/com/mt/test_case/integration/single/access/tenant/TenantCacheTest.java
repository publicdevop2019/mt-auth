package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.pojo.Cache;
import com.mt.test_case.helper.pojo.CacheControlValue;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.PatchCommand;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
        Thread.sleep(10000);
        //read endpoint to verify cache id remove
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, randomEndpointObj);
        Assertions.assertNull(endpointResponseEntity.getBody().getCacheProfileId());

    }

    @Test
    public void validation_create_valid() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> cache0 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache0.getStatusCode());
    }

    @Test
    public void validation_create_name_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setName(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache.getStatusCode());
    }

    @Test
    public void validation_create_name_blank() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setName(" ");
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_create_name_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setName("");
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_create_name_max_length() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setName("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_create_name_invalid_char() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setName("<>!,");
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_description_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setDescription(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_create_description_blank() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setDescription(" ");
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_create_description_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setDescription("");
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_create_description_max_length() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setDescription(
            "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_create_description_invalid_char() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setDescription("<>!，");
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_cache_control_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(1L);
        //null
        cacheObj.setCacheControl(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_create_cache_control_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(1L);
        cacheObj.setCacheControl(Collections.emptySet());
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache2.getStatusCode());
    }

    @Test
    public void validation_create_cache_control_max_length() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(1L);
        Set<String> collect =
            Arrays.stream(CacheControlValue.values()).map(e -> e.label).collect(Collectors.toSet());
        collect.add(CacheControlValue.NO_CACHE.label);
        cacheObj.setCacheControl(collect);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache3.getStatusCode());//200 due to set and enum
    }

    @Test
    public void validation_create_cache_control_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        Set<String> collect =
            Arrays.stream(CacheControlValue.values()).map(e -> e.label).collect(Collectors.toSet());
        collect.add(CacheControlValue.NO_CACHE.label);
        cacheObj.setCacheControl(collect);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_cache_control_invalid_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(1L);
        cacheObj.setCacheControl(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_create_expires_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        //null
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_create_expires_min_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        //min value
        cacheObj.setExpires(0L);
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_create_expires_max_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        //max value
        cacheObj.setExpires((long) Integer.MAX_VALUE);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_create_expires_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setExpires(1L);
        cacheObj.setMaxAge(null);
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_create_max_age_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //null
        cacheObj.setMaxAge(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_create_max_age_min_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //min value
        cacheObj.setMaxAge(0L);
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_create_max_age_max_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //max value
        cacheObj.setMaxAge((long) Integer.MAX_VALUE);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_create_max_age_if_max_age_then_no_s_max_age() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //if max age then no s max age
        cacheObj.setMaxAge(1L);
        cacheObj.setSmaxAge(1L);
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_create_max_age_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setMaxAge(1L);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_s_max_age_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //null
        cacheObj.setSmaxAge(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_create_s_max_age_min_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //min value
        cacheObj.setSmaxAge(0L);
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_create_s_max_age_max_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //max value
        cacheObj.setSmaxAge((long) Integer.MAX_VALUE);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_create_s_max_age_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setSmaxAge(1L);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_vary_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //null
        cacheObj.setVary(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_create_vary_blank() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //blank
        cacheObj.setVary(" ");
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_create_vary_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //empty
        cacheObj.setVary("");
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_create_vary_invalid_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        cacheObj.setVary(
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_create_vary_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //present but allow cache false
        cacheObj.setVary("123");
        cacheObj.setAllowCache(false);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_allow_cache_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        //null
        cacheObj.setAllowCache(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache.getStatusCode());
    }

    @Test
    public void validation_create_allow_cache_true_but_no_config() {
        Cache cacheObj = CacheUtility.getValidNoCache();
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_weak_validation_value_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //null
        cacheObj.setWeakValidation(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_create_weak_validation_true_but_etag_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //true but etag null
        cacheObj.setWeakValidation(true);
        cacheObj.setEtag(null);
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_create_weak_validation_true_but_etag_not_true() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        //true but etag not true
        cacheObj.setWeakValidation(true);
        cacheObj.setEtag(false);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_create_weak_validation_present_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setWeakValidation(true);
        cacheObj.setEtag(true);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_create_project_id_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        Project project1 = new Project();
        //null
        project1.setId("null");
        String url = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resource.getStatusCode());
    }

    @Test
    public void validation_create_project_id_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        Project project1 = new Project();
        //empty
        project1.setId("");
        String url3 = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource3 =
            Utility.createResource(tenantContext.getCreator(), url3, cacheObj);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource3.getStatusCode());
    }

    @Test
    public void validation_create_project_id_blank() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        Project project1 = new Project();
        //blank
        project1.setId(" ");
        String url4 = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource4 =
            Utility.createResource(tenantContext.getCreator(), url4, cacheObj);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource4.getStatusCode());
    }

    @Test
    public void validation_create_project_id_other_tenant_id() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        Project project1 = new Project();
        //other tenant's project id
        project1.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url2 = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource2 =
            Utility.createResource(tenantContext.getCreator(), url2, cacheObj);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource2.getStatusCode());
    }

    @Test
    public void validation_update_name_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setName(null);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache.getStatusCode());
    }

    @Test
    public void validation_update_name_blank() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //blank
        cacheObj.setName(" ");
        ResponseEntity<Void> cache2 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_update_name_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //empty
        cacheObj.setName("");
        ResponseEntity<Void> cache3 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_update_name_max_length() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //max length
        cacheObj.setName("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_update_name_invalid_char() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //invalid char
        cacheObj.setName("<>!,");
        ResponseEntity<Void> cache5 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_update_description_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setDescription(null);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_update_description_blank() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //blank
        cacheObj.setDescription(" ");
        ResponseEntity<Void> cache2 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_update_description_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //empty
        cacheObj.setDescription("");
        ResponseEntity<Void> cache3 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_update_description_max_length() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //max length
        cacheObj.setDescription(
            "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_update_description_invalid_char() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //invalid char
        cacheObj.setDescription("<>!，");
        ResponseEntity<Void> cache5 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_update_cache_control_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(1L);
        //null
        cacheObj.setCacheControl(null);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_update_cache_control_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(1L);
        //empty
        cacheObj.setCacheControl(Collections.emptySet());
        ResponseEntity<Void> cache2 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache2.getStatusCode());
    }

    @Test
    public void validation_update_cache_control_max_length() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(1L);
        //max length
        Set<String> collect =
            Arrays.stream(CacheControlValue.values()).map(e -> e.label).collect(Collectors.toSet());
        collect.add(CacheControlValue.NO_CACHE.label);
        cacheObj.setCacheControl(collect);
        ResponseEntity<Void> cache3 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache3.getStatusCode());//200 due to set and enum
    }

    @Test
    public void validation_update_cache_control_invalid_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(1L);
        //invalid value
        cacheObj.setCacheControl(Collections.singleton(RandomUtility.randomStringNoNum()));
        ResponseEntity<Void> cache4 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_update_cache_control_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(1L);
        Set<String> collect =
            Arrays.stream(CacheControlValue.values()).map(e -> e.label).collect(Collectors.toSet());
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setCacheControl(collect);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_update_expires_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_update_expires_min_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //min value
        cacheObj.setExpires(0L);
        ResponseEntity<Void> cache2 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_update_expires_max_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //max value
        cacheObj.setExpires((long) Integer.MAX_VALUE);
        ResponseEntity<Void> cache3 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_update_expires_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setExpires(1L);
        cacheObj.setMaxAge(null);
        ResponseEntity<Void> cache4 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_update_max_age_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setMaxAge(null);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_update_max_age_min_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //min value
        cacheObj.setMaxAge(0L);
        ResponseEntity<Void> cache2 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_update_max_age_max_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //max value
        cacheObj.setMaxAge((long) Integer.MAX_VALUE);
        ResponseEntity<Void> cache3 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_update_max_age_if_max_age_then_no_s_max_age() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //if max age then no s max age
        cacheObj.setMaxAge(1L);
        cacheObj.setSmaxAge(1L);
        ResponseEntity<Void> cache4 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_update_max_age_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setMaxAge(1L);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_update_s_max_age_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setSmaxAge(null);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_update_s_max_age_min_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //min value
        cacheObj.setSmaxAge(0L);
        ResponseEntity<Void> cache2 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_update_s_max_age_max_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //max value
        cacheObj.setSmaxAge((long) Integer.MAX_VALUE);
        ResponseEntity<Void> cache3 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_update_s_max_age_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //present but allow cache false
        cacheObj.setAllowCache(false);
        cacheObj.setSmaxAge(1L);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_update_vary_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setVary(null);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_update_vary_blank() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //blank
        cacheObj.setVary(" ");
        ResponseEntity<Void> cache2 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_update_vary_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //empty
        cacheObj.setVary("");
        ResponseEntity<Void> cache3 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_update_vary_invalid_value() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //invalid value
        cacheObj.setVary(
            "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_update_vary_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //present but allow cache false
        cacheObj.setVary("123");
        cacheObj.setAllowCache(false);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_update_allow_cache_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setAllowCache(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache.getStatusCode());
    }

    @Test
    public void validation_update_allow_cache_true_but_no_config() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_update_weak_validation_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setWeakValidation(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_update_weak_validation_true_but_etag_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //true but etag null
        cacheObj.setWeakValidation(true);
        cacheObj.setEtag(null);
        ResponseEntity<Void> cache2 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_update_weak_validation_true_but_etag_not_true() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //true but etag not true
        cacheObj.setWeakValidation(true);
        cacheObj.setEtag(false);
        ResponseEntity<Void> cache3 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_update_weak_validation_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //present but allow cache false
        cacheObj.setWeakValidation(true);
        cacheObj.setEtag(true);
        cacheObj.setAllowCache(false);
        cacheObj.setExpires(null);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_patch_name_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        //null
        patchCommand.setValue(null);
        ResponseEntity<Void> cache =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache.getStatusCode());
    }

    @Test
    public void validation_patch_name_blank() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        //blank
        patchCommand.setValue(" ");
        ResponseEntity<Void> cache2 =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_patch_name_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        //empty
        patchCommand.setValue("");
        ResponseEntity<Void> cache3 =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_patch_name_max_length() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        //max length
        patchCommand.setValue("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_patch_name_invalid_char() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        //invalid char
        patchCommand.setValue("<>!,");
        ResponseEntity<Void> cache5 =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_patch_description_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        //null
        patchCommand.setValue(null);
        ResponseEntity<Void> cache =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(HttpStatus.OK, cache.getStatusCode());
    }

    @Test
    public void validation_patch_description_blank() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        //blank
        patchCommand.setValue(" ");
        ResponseEntity<Void> cache2 =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache2.getStatusCode());
    }

    @Test
    public void validation_patch_description_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        //empty
        patchCommand.setValue("");
        ResponseEntity<Void> cache3 =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache3.getStatusCode());
    }

    @Test
    public void validation_patch_description_max_length() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        //max length
        patchCommand.setValue(
            "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> cache4 =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache4.getStatusCode());
    }

    @Test
    public void validation_patch_description_invalid_char() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        //invalid char
        patchCommand.setValue("<>!，");
        ResponseEntity<Void> cache5 =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @Test
    public void validation_update_project_id_null() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        Project project1 = new Project();
        //null
        project1.setId("null");
        String url = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.updateResource(tenantContext.getCreator(), url, cacheObj, cacheObj.getId());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resource.getStatusCode());
    }

    @Test
    public void validation_update_project_id_empty() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        Project project1 = new Project();
        //empty
        project1.setId("");
        String url3 = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource3 =
            Utility.updateResource(tenantContext.getCreator(), url3, cacheObj, cacheObj.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource3.getStatusCode());
    }

    @Test
    public void validation_update_project_id_blank() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        Project project1 = new Project();
        //blank
        project1.setId(" ");
        String url4 = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource4 =
            Utility.updateResource(tenantContext.getCreator(), url4, cacheObj, cacheObj.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource4.getStatusCode());
    }

    @Test
    public void validation_update_project_id_other_tenant_project_id() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        Project project1 = new Project();
        //other tenant's project id
        project1.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url2 = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource2 =
            Utility.updateResource(tenantContext.getCreator(), url2, cacheObj, cacheObj.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource2.getStatusCode());
    }
}
