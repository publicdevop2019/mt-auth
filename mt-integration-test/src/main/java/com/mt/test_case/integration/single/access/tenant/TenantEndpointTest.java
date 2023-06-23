package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantTest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void validation_create_name() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        endpoint.setName(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        endpoint.setName("");
        ResponseEntity<Void> response1 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        endpoint.setName(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        endpoint.setName("1");
        ResponseEntity<Void> response3 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //max length
        endpoint.setName("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response4 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        endpoint.setName("<");
        ResponseEntity<Void> response5 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_description() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        endpoint.setDescription(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        //empty
        endpoint.setDescription("");
        ResponseEntity<Void> response1 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        endpoint.setDescription(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //max length
        endpoint.setDescription("012345678901234567890123456789012345678901234567890123456789" +
            "0123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response4 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        endpoint.setDescription("<");
        ResponseEntity<Void> response5 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
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

    @Test
    public void validation_create_cors_id() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        endpoint.setCorsProfileId(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        //blank
        endpoint.setCorsProfileId(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        endpoint.setCorsProfileId("");
        ResponseEntity<Void> response3 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //wrong format
        endpoint.setCorsProfileId("abc");
        ResponseEntity<Void> response4 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //other tenant's id
        endpoint.setCorsProfileId(AppConstant.MT_ACCESS_CORS_ID);
        ResponseEntity<Void> response5 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //not exit tenant's id
        endpoint.setCorsProfileId("0O8999999999");
        ResponseEntity<Void> response6 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_cache_profile_id_null() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null is ok
        endpoint.setCacheProfileId(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void validation_create_cache_profile_id() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());

        //blank
        endpoint.setCacheProfileId(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        endpoint.setCacheProfileId("");
        ResponseEntity<Void> response3 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //wrong format
        endpoint.setCacheProfileId("abc");
        ResponseEntity<Void> response4 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //other tenant's id
        endpoint.setCacheProfileId(AppConstant.MT_ACCESS_CACHE_ID);
        ResponseEntity<Void> response5 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //not exit tenant's id
        endpoint.setCacheProfileId("0X8999999999");
        ResponseEntity<Void> response6 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_resource_id() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());

        //null
        endpoint.setResourceId(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        endpoint.setResourceId(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        endpoint.setResourceId("");
        ResponseEntity<Void> response3 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //wrong format
        endpoint.setResourceId("abc");
        ResponseEntity<Void> response4 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //other tenant's id
        endpoint.setResourceId(AppConstant.CLIENT_ID_OAUTH2_ID);
        ResponseEntity<Void> response5 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //not exit tenant's id
        endpoint.setResourceId("0C8999999999");
        ResponseEntity<Void> response6 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_path() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        endpoint.setPath(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        endpoint.setPath("");
        ResponseEntity<Void> response1 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        endpoint.setPath(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //max length
        endpoint.setPath(RandomUtility.randomHttpPath() +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij");
        ResponseEntity<Void> response4 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //wrong format
        endpoint.setPath(RandomUtility.randomStringNoNum() + "-/-test");
        ResponseEntity<Void> response5 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //wrong format
        endpoint.setPath(RandomUtility.randomStringNoNum() + "//test");
        ResponseEntity<Void> response7 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
        //invalid char
        endpoint.setPath("<");
        ResponseEntity<Void> response6 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_external() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        endpoint.setExternal(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void validation_create_replenish_rate() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null but burst capacity not null
        endpoint.setReplenishRate(null);
        endpoint.setBurstCapacity(10);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //min value
        endpoint.setReplenishRate(0);
        endpoint.setBurstCapacity(10);
        ResponseEntity<Void> response1 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max value
        endpoint.setReplenishRate(1001);
        endpoint.setBurstCapacity(1500);
        ResponseEntity<Void> response2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //replenish rate must =< burst capacity
        endpoint.setReplenishRate(100);
        endpoint.setBurstCapacity(50);
        ResponseEntity<Void> response3 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_burst_capacity() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null but burst capacity not null
        endpoint.setReplenishRate(10);
        endpoint.setBurstCapacity(null);
        ResponseEntity<Void> response =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //min value
        endpoint.setReplenishRate(10);
        endpoint.setBurstCapacity(0);
        ResponseEntity<Void> response1 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max value
        endpoint.setReplenishRate(10);
        endpoint.setBurstCapacity(Integer.MAX_VALUE);
        ResponseEntity<Void> response2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_create_method() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        endpoint.setMethod(null);
        ResponseEntity<Void> response2 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //blank
        endpoint.setMethod(" ");
        ResponseEntity<Void> response3 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //empty
        endpoint.setMethod("");
        ResponseEntity<Void> response4 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        endpoint.setMethod("abc");
        ResponseEntity<Void> response5 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_project_id() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        //null
        Project project1 = new Project();
        project1.setId("null");
        String url = EndpointUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resource.getStatusCode());
        //empty
        project1.setId("");
        String url3 = EndpointUtility.getUrl(project1);
        ResponseEntity<Void> resource3 =
            Utility.createResource(tenantContext.getCreator(), url3, endpoint);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource3.getStatusCode());
        //blank
        project1.setId(" ");
        String url4 = EndpointUtility.getUrl(project1);
        ResponseEntity<Void> resource4 =
            Utility.createResource(tenantContext.getCreator(), url4, endpoint);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource4.getStatusCode());
        //other tenant's project id
        project1.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url2 = EndpointUtility.getUrl(project1);
        ResponseEntity<Void> resource2 =
            Utility.createResource(tenantContext.getCreator(), url2, endpoint);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource2.getStatusCode());
    }

    @Test
    public void validation_update_name() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null
        endpoint.setName(null);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        endpoint.setName("");
        ResponseEntity<Void> response1 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        endpoint.setName(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        endpoint.setName("1");
        ResponseEntity<Void> response3 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //max length
        endpoint.setName("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response4 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        endpoint.setName("<");
        ResponseEntity<Void> response5 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());

    }

    @Test
    public void validation_update_description() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null
        endpoint.setDescription(null);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        //empty
        endpoint.setDescription("");
        ResponseEntity<Void> response1 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        endpoint.setDescription(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //max length
        endpoint.setDescription("012345678901234567890123456789012345678901234567890123456789" +
            "0123456789012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response4 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        endpoint.setDescription("<");
        ResponseEntity<Void> response5 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
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

    @Test
    public void validation_update_cors_id() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null
        endpoint.setCorsProfileId(null);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        //blank
        endpoint.setCorsProfileId(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        endpoint.setCorsProfileId("");
        ResponseEntity<Void> response3 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //wrong format
        endpoint.setCorsProfileId("abc");
        ResponseEntity<Void> response4 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //other tenant's id
        endpoint.setCorsProfileId(AppConstant.MT_ACCESS_CORS_ID);
        ResponseEntity<Void> response5 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //not exit tenant's id
        endpoint.setCorsProfileId("0O8999999999");
        ResponseEntity<Void> response6 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_update_cache_profile_id() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null
        endpoint.setCacheProfileId(null);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        //blank
        endpoint.setCacheProfileId(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        endpoint.setCacheProfileId("");
        ResponseEntity<Void> response3 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //wrong format
        endpoint.setCacheProfileId("abc");
        ResponseEntity<Void> response4 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //other tenant's id
        endpoint.setCacheProfileId(AppConstant.MT_ACCESS_CACHE_ID);
        ResponseEntity<Void> response5 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //not exit tenant's id
        endpoint.setCacheProfileId("0X8999999999");
        ResponseEntity<Void> response6 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_update_project_id() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null
        Project project1 = new Project();
        project1.setId("null");
        String url = EndpointUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.updateResource(tenantContext.getCreator(), url, endpoint, endpoint.getId());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resource.getStatusCode());
        //empty
        project1.setId("");
        String url3 = EndpointUtility.getUrl(project1);
        ResponseEntity<Void> resource3 =
            Utility.updateResource(tenantContext.getCreator(), url3, endpoint, endpoint.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource3.getStatusCode());
        //blank
        project1.setId(" ");
        String url4 = EndpointUtility.getUrl(project1);
        ResponseEntity<Void> resource4 =
            Utility.updateResource(tenantContext.getCreator(), url4, endpoint, endpoint.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource4.getStatusCode());
        //other tenant's project id
        project1.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url2 = EndpointUtility.getUrl(project1);
        ResponseEntity<Void> resource2 =
            Utility.updateResource(tenantContext.getCreator(), url2, endpoint, endpoint.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource2.getStatusCode());
    }

    @Test
    public void validation_update_path() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null
        endpoint.setPath(null);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        endpoint.setPath("");
        ResponseEntity<Void> response1 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        endpoint.setPath(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //max length
        endpoint.setPath(RandomUtility.randomHttpPath() +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij");
        ResponseEntity<Void> response4 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //wrong format
        endpoint.setPath(RandomUtility.randomStringNoNum() + "-/-test");
        ResponseEntity<Void> response5 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //wrong format
        endpoint.setPath(RandomUtility.randomStringNoNum() + "//test");
        ResponseEntity<Void> response7 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
        //invalid char
        endpoint.setPath("<");
        ResponseEntity<Void> response6 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_update_replenish_rate() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null but burst capacity not null
        endpoint.setReplenishRate(null);
        endpoint.setBurstCapacity(10);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //min value
        endpoint.setReplenishRate(0);
        endpoint.setBurstCapacity(10);
        ResponseEntity<Void> response1 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max value
        endpoint.setReplenishRate(1001);
        endpoint.setBurstCapacity(1500);
        ResponseEntity<Void> response2 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //replenish rate must =< burst capacity
        endpoint.setReplenishRate(100);
        endpoint.setBurstCapacity(50);
        ResponseEntity<Void> response3 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_update_burst_capacity() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null but burst capacity not null
        endpoint.setReplenishRate(10);
        endpoint.setBurstCapacity(null);
        ResponseEntity<Void> response =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //min value
        endpoint.setReplenishRate(10);
        endpoint.setBurstCapacity(0);
        ResponseEntity<Void> response1 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max value
        endpoint.setReplenishRate(10);
        endpoint.setBurstCapacity(Integer.MAX_VALUE);
        ResponseEntity<Void> response2 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_update_method() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        //null
        endpoint.setMethod(null);
        ResponseEntity<Void> response2 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //blank
        endpoint.setMethod(" ");
        ResponseEntity<Void> response3 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //empty
        endpoint.setMethod("");
        ResponseEntity<Void> response4 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        endpoint.setMethod("abc");
        ResponseEntity<Void> response5 =
            EndpointUtility.updateTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_patch_name() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        //null
        patchCommand.setValue(null);
        ResponseEntity<Void> response =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        patchCommand.setValue("");
        ResponseEntity<Void> response1 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        patchCommand.setValue(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        patchCommand.setValue("1");
        ResponseEntity<Void> response3 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //max length
        patchCommand.setValue("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response4 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        patchCommand.setValue("<");
        ResponseEntity<Void> response5 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_patch_description() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
    }

    @Test
    public void validation_patch_path() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/path");
        //null
        patchCommand.setValue(null);
        ResponseEntity<Void> response =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        patchCommand.setValue("");
        ResponseEntity<Void> response1 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        patchCommand.setValue(" ");
        ResponseEntity<Void> response2 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //max length
        patchCommand.setValue(RandomUtility.randomHttpPath() +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij");
        ResponseEntity<Void> response4 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //wrong format
        patchCommand.setValue(RandomUtility.randomStringNoNum() + "-/-test");
        ResponseEntity<Void> response5 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //wrong format
        patchCommand.setValue(RandomUtility.randomStringNoNum() + "//test");
        ResponseEntity<Void> response7 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
        //invalid char
        patchCommand.setValue("<");
        ResponseEntity<Void> response6 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_patch_method() {
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> response0 =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        endpoint.setId(UrlUtility.getId(response0));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/method");
        //null
        patchCommand.setValue(null);
        ResponseEntity<Void> response2 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //blank
        patchCommand.setValue(" ");
        ResponseEntity<Void> response3 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //empty
        patchCommand.setValue("");
        ResponseEntity<Void> response4 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        patchCommand.setValue("abc");
        ResponseEntity<Void> response5 =
            EndpointUtility.patchTenantEndpoint(tenantContext, endpoint, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());

    }

    @Test
    public void validation_expire_reason() {
        //create endpoint
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint.getStatusCode());
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //blank
        ResponseEntity<Void> response1 =
            EndpointUtility.expireTenantEndpoint(tenantContext, endpoint, " ");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        ResponseEntity<Void> response2 =
            EndpointUtility.expireTenantEndpoint(tenantContext, endpoint, "");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        ResponseEntity<Void> response3 =
            EndpointUtility.expireTenantEndpoint(tenantContext, endpoint, "a");
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //max length
        ResponseEntity<Void> response4 =
            EndpointUtility.expireTenantEndpoint(tenantContext, endpoint,
                "01234567890123456789012345678901234567890123456789012345678901234567890123456789");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        ResponseEntity<Void> response5 =
            EndpointUtility.expireTenantEndpoint(tenantContext, endpoint,
                "<");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }
}
