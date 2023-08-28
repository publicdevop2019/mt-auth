package com.mt.integration.single.access.validation;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.args.CacheControlArgs;
import com.mt.helper.args.CacheMaxAge;
import com.mt.helper.args.CacheSMaxAge;
import com.mt.helper.args.CacheVary;
import com.mt.helper.args.DescriptionArgs;
import com.mt.helper.args.ExpireArgs;
import com.mt.helper.args.NameArgs;
import com.mt.helper.args.ProjectIdArgs;
import com.mt.helper.pojo.Cache;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Project;
import com.mt.helper.utility.CacheUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.Utility;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@Tag("validation")

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantCacheValidationTest{
    protected static TenantContext tenantContext;

    @BeforeAll
    public static void beforeAll() {
        tenantContext = TestHelper.beforeAllTenant(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
        cacheObj.setName(name);
        ResponseEntity<Void> cache = CacheUtility.updateTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(status, cache.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_update_description(String description, HttpStatus status) {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
        //null
        cacheObj.setAllowCache(null);
        ResponseEntity<Void> cache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cache.getStatusCode());
    }

    @Test
    public void validation_update_allow_cache_true_but_no_config() {
        Cache cacheObj = CacheUtility.getValidNoCache();
        ResponseEntity<Void> tenantCache = CacheUtility.createTenantCache(tenantContext, cacheObj);
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
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
        cacheObj.setId(HttpUtility.getId(tenantCache));
        Project project1 = new Project();
        project1.setId(projectId);
        String url = CacheUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.updateResource(tenantContext.getCreator(), url, cacheObj, cacheObj.getId());
        Assertions.assertEquals(status, resource.getStatusCode());
    }
}
