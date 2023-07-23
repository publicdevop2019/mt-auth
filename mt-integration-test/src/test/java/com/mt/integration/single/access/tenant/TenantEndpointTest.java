package com.mt.integration.single.access.tenant;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TenantUtility;
import com.mt.helper.utility.UrlUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantEndpointTest {
    private static TenantContext tenantContext;
    private static Client client;

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach(log);
    }
    @BeforeAll
    public static void beforeAll() {
        tenantContext = TestHelper.beforeAllTenant(log);
        client = ClientUtility.createValidBackendClient();
        client.setResourceIndicator(true);
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        client.setId(UrlUtility.getId(tenantClient));
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


}
