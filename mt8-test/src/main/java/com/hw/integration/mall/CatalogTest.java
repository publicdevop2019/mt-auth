package com.hw.integration.mall;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.Catalog;
import com.hw.helper.CategorySummaryCustomerRepresentation;
import com.hw.helper.OutgoingReqInterceptor;
import com.hw.helper.UserAction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CatalogTest {
    @Autowired
    UserAction action;
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    UUID uuid;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            action.saveResult(description, uuid);
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    @Test
    public void shop_create_front_category() {
        Catalog catalog = action.generateRandomFrontendCatalog();
        String s1 = action.getDefaultAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<Catalog> request = new HttpEntity<>(catalog, headers);

        String url = UserAction.proxyUrl + UserAction.SVC_NAME_PRODUCT + "/catalogs/admin";
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotEquals(0, exchange.getHeaders().get("Location"));
    }

    @Test
    public void shop_get_all_category() {
        ResponseEntity<CategorySummaryCustomerRepresentation> categories = action.getCatalogs();
        Assert.assertEquals(HttpStatus.OK, categories.getStatusCode());
        Assert.assertNotNull( categories.getBody().getData());
    }

    @Test
    public void shop_create_then_update_then_delete_category() {
        Catalog randomCategory = action.generateRandomFrontendCatalog();
        String s = null;
        try {
            s = mapper.writeValueAsString(randomCategory);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String s1 = action.getDefaultAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<String> request = new HttpEntity<>(s, headers);

        String url = UserAction.proxyUrl + UserAction.SVC_NAME_PRODUCT + "/catalogs/admin";
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        Catalog randomCategory2 = action.generateRandomFrontendCatalog();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_JSON);
        headers2.setBearerAuth(s1);
        randomCategory2.setVersion(0);
        HttpEntity<Catalog> request2 = new HttpEntity<>(randomCategory2, headers2);
        ResponseEntity<String> exchange2 = action.restTemplate.exchange(url + "/" + exchange.getHeaders().getLocation().toString(), HttpMethod.PUT, request2, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

        HttpHeaders headers3 = new HttpHeaders();
        headers3.setContentType(MediaType.APPLICATION_JSON);
        headers3.setBearerAuth(s1);
        HttpEntity<String> request3 = new HttpEntity<>(headers3);
        ResponseEntity<String> exchange3 = action.restTemplate.exchange(url + "/" + exchange.getHeaders().getLocation().toString(), HttpMethod.DELETE, request3, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
    }

    @Test
    public void shop_create_catalog_which_has_invalid_parentId() {
        Catalog randomCategory = action.generateRandomFrontendCatalog();
        randomCategory.setParentId("invalidValue");
        String s1 = action.getDefaultAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<Catalog> request = new HttpEntity<>(randomCategory, headers);

        String url = UserAction.proxyUrl + UserAction.SVC_NAME_PRODUCT + "/catalogs/admin";
        ResponseEntity<String> exchange = action.restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }
}
