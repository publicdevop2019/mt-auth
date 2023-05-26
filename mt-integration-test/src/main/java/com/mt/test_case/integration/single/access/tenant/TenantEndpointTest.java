package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.TenantUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * this integration auth requires oauth2service to be running.
 */
@RunWith(SpringRunner.class)
@Slf4j
public class TenantEndpointTest extends TenantTest {
    public static final String ENDPOINTS = "/projects/0P8HE307W6IO/endpoints";
    private static Client client;

    @BeforeClass
    public static void initTenant() {
        log.info("init tenant in progress");
        TestContext.init();
        tenantContext = TenantUtility.initTenant();
        client = ClientUtility.createRandomBackendClientObj();
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
            EndpointUtility.createRandomEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        //delete endpoint
        ResponseEntity<Void> voidResponseEntity =
            EndpointUtility.deleteTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }


    @Test
    public void tenant_can_update_endpoint() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        endpoint.setName(RandomUtility.randomStringWithNum());
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        //update endpoint
        ResponseEntity<Void> voidResponseEntity =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint
                );
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        //read endpoint
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(1, endpointResponseEntity.getBody().getVersion().intValue());
    }

    @Test
    public void tenant_can_read_endpoint() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //read endpoint
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.OK, endpointResponseEntity.getStatusCode());
        Assert.assertNotNull(endpointResponseEntity.getBody().getId());
    }

    @Test
    public void create_endpoint_for_share_then_expire_and_delete() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomSharedEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //try to delete endpoint
        ResponseEntity<Void> voidResponseEntity =
            EndpointUtility.deleteTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
        //expire endpoint
        ResponseEntity<Void> expireTenantEndpoint =
            EndpointUtility.expireTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.OK, expireTenantEndpoint.getStatusCode());
        //try to delete endpoint again
        ResponseEntity<Void> voidResponseEntity2 =
            EndpointUtility.deleteTenantEndpoint(tenantContext, endpoint);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity2.getStatusCode());
    }

    @Test
    public void endpoint_validation_should_work() {
        //1. invalid method
        //@todo add this validation
//        Endpoint endpoint =
//            EndpointUtility.createRandomEndpointObj(client.getId());
//        endpoint.setMethod(RandomUtility.randomStringNoNum());
//        ResponseEntity<Void> tenantEndpoint =
//            EndpointUtility.createTenantEndpoint(tenantContext.getCreator(), endpoint,
//                tenantContext.getProject().getId());
//        Assert.assertEquals(HttpStatus.BAD_REQUEST, tenantEndpoint.getStatusCode());
        //2. invalid share endpoint with not accessible client
        Client client = ClientUtility.createRandomBackendClientObj();
        client.setResourceIndicator(false);
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(tenantClient));
        Endpoint endpoint2 =
            EndpointUtility.createRandomEndpointObj(client.getId());
        endpoint2.setShared(true);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint2);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tenantEndpoint2.getStatusCode());
    }

    @Test
    public void validation_create_name(){
        //min length
        //max length
        //invalid char
        //null
        //empty
        //blank
    }
    @Test
    public void validation_create_description(){
        //min length
        //max length
        //invalid char
        //null
        //empty
        //blank
    }
    @Test
    public void validation_create_secured(){
        //null
    }
    @Test
    public void validation_create_is_websocket(){
        //null
        //true with wrong config

    }
    @Test
    public void validation_create_csrf_enabled(){
        //null
        //true but is websocket
    }
    @Test
    public void validation_create_shared(){
        //null
        //websocket and shared

    }
    @Test
    public void validation_create_cors_id(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
        //other tenant's id
    }
    @Test
    public void validation_create_cache_profile_id(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
        //other tenant's id
    }
    @Test
    public void validation_create_resource_id(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
        //other tenant's id
    }
    @Test
    public void validation_create_path(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
    }
    @Test
    public void validation_create_external(){
        //null
    }
    @Test
    public void validation_create_replenish_rate(){
        //min value
        //max value
        //null
    }
    @Test
    public void validation_create_burst_capacity(){
        //min value
        //max value
        //null
    }
    @Test
    public void validation_create_method(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
        //invalid value

    }


    @Test
    public void validation_update_name(){
        //min length
        //max length
        //invalid char
        //null
        //empty
        //blank
    }
    @Test
    public void validation_update_description(){
        //min length
        //max length
        //invalid char
        //null
        //empty
        //blank
    }
    @Test
    public void validation_update_is_websocket(){
        //null
        //true with wrong config

    }
    @Test
    public void validation_update_csrf_enabled(){
        //null
        //true but is websocket
    }
    @Test
    public void validation_update_cors_id(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
    }
    @Test
    public void validation_update_cache_profile_id(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
    }
    @Test
    public void validation_update_version() {
        //null
        //min value
        //max value
        //version mismatch
    }
    @Test
    public void validation_update_project_id() {
        //mismatch
        //blank
        //empty
        //wrong format
        //null
    }
    @Test
    public void validation_update_path(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
    }
    @Test
    public void validation_update_replenish_rate(){
        //min value
        //max value
        //null
    }
    @Test
    public void validation_update_burst_capacity(){
        //min value
        //max value
        //null
    }
    @Test
    public void validation_update_method(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
        //invalid value

    }

    @Test
    public void validation_patch_name(){
        //min length
        //max length
        //invalid char
        //null
        //empty
        //blank
    }
    @Test
    public void validation_patch_description(){
        //min length
        //max length
        //invalid char
        //null
        //empty
        //blank
    }
    @Test
    public void validation_patch_path(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
    }
    @Test
    public void validation_patch_method(){
        //null
        //blank
        //empty
        //wrong format
        //min length
        //max length
        //invalid value

    }
    @Test
    public void validation_expire_reason(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
}
