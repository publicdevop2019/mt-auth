package com.mt.integration.single.access.validation;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.args.CacheIdArgs;
import com.mt.helper.args.CorsIdArgs;
import com.mt.helper.args.DescriptionArgs;
import com.mt.helper.args.EndpointBurstCapacityArgs;
import com.mt.helper.args.EndpointExpireReasonArgs;
import com.mt.helper.args.EndpointMethodArgs;
import com.mt.helper.args.EndpointPathArgs;
import com.mt.helper.args.EndpointReplenishRateArgs;
import com.mt.helper.args.NameArgs;
import com.mt.helper.args.ProjectIdArgs;
import com.mt.helper.args.ResourceIdArgs;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Project;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.TenantUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.Utility;
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
public class TenantEndpointValidationTest{
    private static Client client;
    private static TenantContext tenantContext;

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }
    @BeforeAll
    public static void initTenant() {
        TestHelper.beforeAll(log);
        log.info("init tenant in progress");
        tenantContext = TenantUtility.initTenant();
        client = ClientUtility.createValidBackendClient();
        client.setResourceIndicator(true);
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(HttpUtility.getId(tenantClient));
        log.info("init tenant complete");
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
        client.setId(HttpUtility.getId(tenantClient));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(response0));
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
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        ResponseEntity<Void> response1 =
            EndpointUtility.expireTenantEndpoint(tenantContext, endpoint, reason);
        Assertions.assertEquals(status, response1.getStatusCode());
    }
}
