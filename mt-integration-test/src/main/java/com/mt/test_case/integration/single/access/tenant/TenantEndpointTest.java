package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.args.CacheIdArgs;
import com.mt.test_case.helper.args.CorsIdArgs;
import com.mt.test_case.helper.args.DescriptionArgs;
import com.mt.test_case.helper.args.EndpointBurstCapacityArgs;
import com.mt.test_case.helper.args.EndpointExpireReasonArgs;
import com.mt.test_case.helper.args.EndpointMethodArgs;
import com.mt.test_case.helper.args.EndpointPathArgs;
import com.mt.test_case.helper.args.EndpointReplenishRateArgs;
import com.mt.test_case.helper.args.NameArgs;
import com.mt.test_case.helper.args.ProjectIdArgs;
import com.mt.test_case.helper.args.ResourceIdArgs;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.PatchCommand;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.TenantUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.Utility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
public class TenantEndpointTest extends TenantTest {
    public static final String ENDPOINTS = "/projects/0P8HE307W6IO/endpoints";
    private static Client client;

    @BeforeAll
    public static void initTenant() {
        log.info("init tenant in progress");
        TestContext.init();
        tenantContext = TenantUtility.initTenant();
        client = ClientUtility.createValidBackendClient();
        client.setResourceIndicator(true);
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(tenantClient));
        log.info("init tenant complete");
    }

    @Test
    public void tenant_can_delete_endpoint() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        //delete endpoint
        ResponseEntity<Void> voidResponseEntity =
            EndpointUtility.deleteTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }


    @Test
    public void tenant_can_update_endpoint() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        endpoint.setName(RandomUtility.randomStringWithNum());
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        //update endpoint
        ResponseEntity<Void> voidResponseEntity =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint
            );
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        //read endpoint
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(1, endpointResponseEntity.getBody().getVersion().intValue());
    }

    @Test
    public void tenant_can_read_endpoint() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //read endpoint
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, endpointResponseEntity.getStatusCode());
        Assertions.assertNotNull(endpointResponseEntity.getBody().getId());
    }

    @Test
    public void tenant_create_endpoint_for_share_then_expire_and_delete() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //try to delete endpoint
        ResponseEntity<Void> voidResponseEntity =
            EndpointUtility.deleteTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
        //expire endpoint
        ResponseEntity<Void> expireTenantEndpoint =
            EndpointUtility.expireTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, expireTenantEndpoint.getStatusCode());
        //try to delete endpoint again
        ResponseEntity<Void> voidResponseEntity2 =
            EndpointUtility.deleteTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity2.getStatusCode());
    }

    @Test
    public void validation_create_valid() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_create_name(String name, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        endpoint.setName(name);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_create_description(String description, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        endpoint.setDescription(description);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @Test
    public void validation_create_secured() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        endpoint.setSecured(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void validation_create_is_websocket() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        endpoint.setWebsocket(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //true with wrong config
        endpoint.setWebsocket(true);
        ResponseEntity<Void> response1 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
    }

    @Test
    public void validation_create_csrf_enabled() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        endpoint.setCsrfEnabled(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //true but is websocket
        endpoint.setWebsocket(true);
        endpoint.setCsrfEnabled(true);
        ResponseEntity<Void> response1 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
    }

    @Test
    public void validation_create_shared() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        endpoint.setShared(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //true but is websocket
        endpoint.setWebsocket(true);
        endpoint.setShared(true);
        ResponseEntity<Void> response1 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //invalid share endpoint with not accessible client
        Client client = ClientUtility.createValidBackendClient();
        client.setResourceIndicator(false);
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(tenantClient));
        Endpoint endpoint2 =
            EndpointUtility.createRandomEndpointObj(client.getId());
        endpoint2.setShared(true);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint2);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, tenantEndpoint2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CorsIdArgs.class)
    public void validation_create_cors_id(String corsId, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        endpoint.setCorsProfileId(corsId);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CacheIdArgs.class)
    public void validation_create_cache_profile_id(String cacheId, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        endpoint.setCacheProfileId(cacheId);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ResourceIdArgs.class)
    public void validation_create_resource_id(String resourceId, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        endpoint.setResourceId(resourceId);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(EndpointPathArgs.class)
    public void validation_create_path(String path, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        endpoint.setPath(path);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(EndpointReplenishRateArgs.class)
    public void validation_create_replenish_rate(Integer replenishRate, Integer burstCapacity,
                                                 HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        endpoint.setReplenishRate(replenishRate);
        endpoint.setBurstCapacity(burstCapacity);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(EndpointBurstCapacityArgs.class)
    public void validation_create_burst_capacity(Integer replenishRate, Integer burstCapacity,
                                                 HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        endpoint.setReplenishRate(replenishRate);
        endpoint.setBurstCapacity(burstCapacity);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(EndpointMethodArgs.class)
    public void validation_create_method(String method, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        endpoint.setMethod(method);
        ResponseEntity<Void> response2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_create_project_id(String projectId, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        Project project1 = new Project();
        project1.setId(projectId);
        String url = EndpointUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, endpoint);
        Assertions.assertEquals(status, resource.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_update_name(String name, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        endpoint.setName(name);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_update_description(String description, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        endpoint.setDescription(description);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @Test
    public void validation_update_is_websocket() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //websocket update will ignore
        endpoint.setWebsocket(null);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void validation_update_csrf_enabled() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null
        endpoint.setCsrfEnabled(null);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //websocket will get ignored
        endpoint.setWebsocket(true);
        endpoint.setCsrfEnabled(true);
        ResponseEntity<Void> response1 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CorsIdArgs.class)
    public void validation_update_cors_id(String corsId, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        endpoint.setCorsProfileId(corsId);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(CacheIdArgs.class)
    public void validation_update_cache_profile_id(String cacheId, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        endpoint.setCacheProfileId(cacheId);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_update_project_id(String projectId, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        Project project1 = new Project();
        project1.setId(projectId);
        String url = EndpointUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.updateResource(tenantContext.getCreator(), url, endpoint, endpoint.getId());
        Assertions.assertEquals(status, resource.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(EndpointPathArgs.class)
    public void validation_update_path(String path, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        endpoint.setPath(path);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(EndpointReplenishRateArgs.class)
    public void validation_update_replenish_rate(Integer replenishRate, Integer burstCapacity,
                                                 HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null but burst capacity not null
        endpoint.setReplenishRate(replenishRate);
        endpoint.setBurstCapacity(burstCapacity);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(EndpointBurstCapacityArgs.class)
    public void validation_update_burst_capacity(Integer replenishRate, Integer burstCapacity,
                                                 HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        endpoint.setReplenishRate(replenishRate);
        endpoint.setBurstCapacity(burstCapacity);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(EndpointMethodArgs.class)
    public void validation_update_method(String method, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        endpoint.setMethod(method);
        ResponseEntity<Void> response2 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(status, response2.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_patch_name(String name, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        patchCommand.setValue(name);
        ResponseEntity<Void> response =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_patch_description(String description, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        patchCommand.setValue(description);
        ResponseEntity<Void> response =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(EndpointPathArgs.class)
    public void validation_patch_path(String path, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/path");
        patchCommand.setValue(path);
        ResponseEntity<Void> response =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(EndpointMethodArgs.class)
    public void validation_patch_method(String method, HttpStatus status) {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/method");
        patchCommand.setValue(method);
        ResponseEntity<Void> response2 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(status, response2.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(EndpointExpireReasonArgs.class)
    public void validation_expire_reason(String reason, HttpStatus status) {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        ResponseEntity<Void> response1 =
            EndpointUtility.expireTenantEndpoint(tenantContext, endpoint, reason);
        Assertions.assertEquals(status, response1.getStatusCode());
    }
}
