package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.args.CacheControlArgs;
import com.mt.test_case.helper.args.CacheMaxAge;
import com.mt.test_case.helper.args.CacheSMaxAge;
import com.mt.test_case.helper.args.CacheVary;
import com.mt.test_case.helper.args.DescriptionArgs;
import com.mt.test_case.helper.args.ExpireArgs;
import com.mt.test_case.helper.args.NameArgs;
import com.mt.test_case.helper.args.ProjectIdArgs;
import com.mt.test_case.helper.pojo.Cache;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
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
        Thread.sleep(10*1000);
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

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_create_name(String name, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setName(name);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_create_description(String description, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setDescription(description);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CacheControlArgs.class)
    public void validation_create_cache_control(Boolean allow, Set<String> cacheControl,
                                                HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setAllowCache(allow);
        cacheObj.setExpires(1L);
        cacheObj.setCacheControl(cacheControl);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ExpireArgs.class)
    public void validation_create_expires(Long expire, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        cacheObj.setExpires(expire);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
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

    @ParameterizedTest
    @ArgumentsSource(CacheMaxAge.class)
    public void validation_create_max_age(Long maxAge, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        cacheObj.setMaxAge(maxAge);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
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
        //present but allow cache false
        cacheObj.setMaxAge(1L);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CacheSMaxAge.class)
    public void validation_create_s_max_age(Long maxAge, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        cacheObj.setSmaxAge(maxAge);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
    }

    @Test
    public void validation_create_s_max_age_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        //present but allow cache false
        cacheObj.setSmaxAge(1L);
        ResponseEntity<Void> cache5 = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache5.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CacheVary.class)
    public void validation_create_vary(String vary, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        cacheObj.setVary(vary);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
    }

    @Test
    public void validation_create_vary_present_but_allow_cache_false() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        //present but allow cache false
        cacheObj.setVary("123");
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

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_create_project_id(String projectId, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        Project project1 = new Project();
        project1.setId(projectId);
        String url = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, cacheObj);
        Assertions.assertEquals(status, resource.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_update_name(String name, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        cacheObj.setName(name);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_update_description(String description, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        cacheObj.setDescription(description);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(CacheControlArgs.class)
    public void validation_update_cache_control(Boolean allow, Set<String> cacheControl,
                                                HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        cacheObj.setAllowCache(allow);
        cacheObj.setExpires(1L);
        cacheObj.setCacheControl(cacheControl);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ExpireArgs.class)
    public void validation_update_expires(Long expire, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setMaxAge(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setExpires(expire);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
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

    @ParameterizedTest
    @ArgumentsSource(CacheMaxAge.class)
    public void validation_update_max_age(Long maxAge, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        cacheObj.setMaxAge(maxAge);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
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

    @ParameterizedTest
    @ArgumentsSource(CacheSMaxAge.class)
    public void validation_update_s_max_age(Long maxAge, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setSmaxAge(maxAge);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
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

    @ParameterizedTest
    @ArgumentsSource(CacheVary.class)
    public void validation_update_vary(String vary, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        cacheObj.setExpires(1L);
        cacheObj.setAllowCache(true);
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        //null
        cacheObj.setVary(vary);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
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

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_patch_name(String name, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        //null
        patchCommand.setValue(name);
        ResponseEntity<Void> cache =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(status, cache.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_patch_description(String description, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        //null
        patchCommand.setValue(description);
        ResponseEntity<Void> cache =
            CacheUtility.patchTenantCache(tenantContext, cacheObj, patchCommand);
        Assertions.assertEquals(status, cache.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_update_project_id(String projectId, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(UrlUtility.getId(tenantCache));
        Project project1 = new Project();
        project1.setId(projectId);
        String url = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.updateResource(tenantContext.getCreator(), url, cacheObj, cacheObj.getId());
        Assertions.assertEquals(status, resource.getStatusCode());
    }
}
