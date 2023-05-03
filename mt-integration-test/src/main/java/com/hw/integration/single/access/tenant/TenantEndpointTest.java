package com.hw.integration.single.access.tenant;

import com.hw.helper.Client;
import com.hw.helper.Endpoint;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.RandomUtility;
import com.hw.helper.utility.TenantUtility;
import com.hw.helper.utility.TestContext;
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
            ClientUtility.createTenantClient(tenantContext.getCreator(), client,
                tenantContext.getProject().getId());
        client.setId(tenantClient.getHeaders().getLocation().toString());
        log.info("init tenant complete");
    }

    @Test
    public void tenant_can_delete_endpoint() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext.getCreator(), endpoint,
                tenantContext.getProject().getId());
        endpoint.setId(tenantEndpoint.getHeaders().getLocation().toString());
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        //delete endpoint
        ResponseEntity<Void> voidResponseEntity =
            EndpointUtility.deleteTenantEndpoint(tenantContext.getCreator(), endpoint,
                tenantContext.getProject().getId());
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }


    @Test
    public void tenant_can_update_endpoint() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext.getCreator(), endpoint,
                tenantContext.getProject().getId());
        endpoint.setId(tenantEndpoint.getHeaders().getLocation().toString());
        endpoint.setName(RandomUtility.randomStringWithNum());
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        //update endpoint
        ResponseEntity<Void> voidResponseEntity =
            EndpointUtility.updateTenantEndpoint(tenantContext.getCreator(), endpoint,
                tenantContext.getProject().getId());
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        //read endpoint
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext.getCreator(), endpoint.getId(),
                tenantContext.getProject().getId());
        Assert.assertEquals(1, endpointResponseEntity.getBody().getVersion().intValue());
    }

    @Test
    public void tenant_can_read_endpoint() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext.getCreator(), endpoint,
                tenantContext.getProject().getId());
        endpoint.setId(tenantEndpoint.getHeaders().getLocation().toString());
        //read endpoint
        ResponseEntity<Endpoint> endpointResponseEntity =
            EndpointUtility.readTenantEndpoint(tenantContext.getCreator(), endpoint.getId(),
                tenantContext.getProject().getId());
        Assert.assertEquals(HttpStatus.OK, endpointResponseEntity.getStatusCode());
        Assert.assertNotNull(endpointResponseEntity.getBody().getId());
    }

    @Test
    public void create_endpoint_for_share_then_expire_and_delete() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomSharedEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext.getCreator(), endpoint,
                tenantContext.getProject().getId());
        Assert.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        endpoint.setId(tenantEndpoint.getHeaders().getLocation().toString());
        //try to delete endpoint
        ResponseEntity<Void> voidResponseEntity =
            EndpointUtility.deleteTenantEndpoint(tenantContext.getCreator(), endpoint,
                tenantContext.getProject().getId());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
        //expire endpoint
        ResponseEntity<Void> expireTenantEndpoint =
            EndpointUtility.expireTenantEndpoint(tenantContext.getCreator(), endpoint,
                tenantContext.getProject().getId());
        Assert.assertEquals(HttpStatus.OK, expireTenantEndpoint.getStatusCode());
        //try to delete endpoint again
        ResponseEntity<Void> voidResponseEntity2 =
            EndpointUtility.deleteTenantEndpoint(tenantContext.getCreator(), endpoint,
                tenantContext.getProject().getId());
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
            ClientUtility.createTenantClient(tenantContext.getCreator(), client,
                tenantContext.getProject().getId());
        client.setId(tenantClient.getHeaders().getLocation().toString());
        Endpoint endpoint2 =
            EndpointUtility.createRandomEndpointObj(client.getId());
        endpoint2.setShared(true);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext.getCreator(), endpoint2,
                tenantContext.getProject().getId());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tenantEndpoint2.getStatusCode());
    }

}
