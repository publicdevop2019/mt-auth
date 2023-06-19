package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.Permission;
import com.mt.test_case.helper.pojo.RejectSubRequestCommand;
import com.mt.test_case.helper.pojo.Role;
import com.mt.test_case.helper.pojo.SubscriptionReq;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.UpdateType;
import com.mt.test_case.helper.pojo.User;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.MarketUtility;
import com.mt.test_case.helper.utility.PermissionUtility;
import com.mt.test_case.helper.utility.RoleUtility;
import com.mt.test_case.helper.utility.TenantUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantMarketTest {
    protected static TenantContext tenantContextA;
    protected static TenantContext tenantContextB;
    protected static Client clientA;

    @BeforeClass
    public static void initTenant() {
        log.info("init tenant in progress");
        TestContext.init();
        tenantContextA = TenantUtility.initTenant();
        tenantContextB = TenantUtility.initTenant();

        clientA = ClientUtility.createValidBackendClient();
        clientA.setResourceIndicator(true);
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContextA, clientA);
        clientA.setId(UrlUtility.getId(tenantClient));

        log.info("init tenant complete");
    }

    @Test
    public void tenant_can_view_api_on_market() {
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.readMarketEndpoint(creator);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotSame(0, response.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_public_api() {
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.readMarketEndpoint(creator);
        List<Endpoint> collect = response.getBody().getData().stream().filter(e -> !e.getSecured())
            .collect(Collectors.toList());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotSame(0, collect.size());
    }

    @Test
    public void tenant_can_view_shared_api() {
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.readMarketEndpoint(creator);
        List<Endpoint> collect = response.getBody().getData().stream().filter(Endpoint::getSecured)
            .collect(Collectors.toList());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotSame(0, collect.size());
    }

    @Test
    public void tenant_can_view_its_public_api_in_market() {
        //create public endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //find this endpoint in market
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.searchMarketEndpoint(creator, endpoint.getId());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertSame(1, response.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_its_shared_api_in_market() {
        //create shared endpoint
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //find this endpoint in market
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.searchMarketEndpoint(creator, endpoint.getId());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertSame(1, response.getBody().getData().size());
        Assert.assertTrue(response.getBody().getData().stream().findFirst().get().getSecured());
    }

    @Test
    public void tenant_can_send_sub_req_for_shared_api() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_cannot_send_sub_req_for_its_shared_api() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextA, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextA.getCreator(), randomTenantSubReqObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_cannot_send_sub_req_for_public_api() {
        //create public endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_approve_sub_req_for_shared_endpoint() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        String subReqId = UrlUtility.getId(voidResponseEntity);
        //tenantB can view sub req
        ResponseEntity<SumTotal<SubscriptionReq>> voidResponseEntity12 =
            MarketUtility.viewMySubReq(tenantContextB);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity12.getStatusCode());
        Assert.assertNotSame(0, voidResponseEntity12.getBody().getData().size());
        //tenantA can view request
        ResponseEntity<SumTotal<SubscriptionReq>> voidResponseEntity13 =
            MarketUtility.viewMyPendingApprove(tenantContextA);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity13.getStatusCode());
        Assert.assertNotSame(0, voidResponseEntity13.getBody().getData().size());
        //tenantA approve request
        ResponseEntity<Void> voidResponseEntity1 =
            MarketUtility.approveSubReq(tenantContextA, subReqId);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity1.getStatusCode());
        //tenantB can view approved req
        ResponseEntity<SumTotal<SubscriptionReq>> voidResponseEntity14 =
            MarketUtility.viewMySubs(tenantContextB);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity14.getStatusCode());
        Assert.assertNotSame(0, voidResponseEntity14.getBody().getData().size());
    }

    @Test
    public void tenant_can_assign_approved_api_to_role() throws InterruptedException {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        String subReqId = UrlUtility.getId(voidResponseEntity);
        //approve sub req
        MarketUtility.approveSubReq(tenantContextA, subReqId);

        //create tenantB role
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContextB, role);
        role.setId(UrlUtility.getId(tenantRole));
        //wait for cache to expire
        Thread.sleep(20 * 1000);
        //update it's api
        ResponseEntity<SumTotal<Permission>> shared =
            PermissionUtility.readTenantPermissionShared(tenantContextB);
        String permissionId = shared.getBody().getData().get(0).getId();
        role.setExternalPermissionIds(Collections.singleton(permissionId));
        role.setType(UpdateType.API_PERMISSION.name());
        ResponseEntity<Void> response4 =
            RoleUtility.updateTenantRole(tenantContextB, role);
        Assert.assertEquals(HttpStatus.OK, response4.getStatusCode());
    }

    @Test
    public void validation_create_endpoint_id() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        SubscriptionReq req =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        //send sub req tenantB
        //null
        req.setEndpointId(null);
        ResponseEntity<Void> response =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        req.setEndpointId(" ");
        ResponseEntity<Void> response1 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        req.setEndpointId("");
        ResponseEntity<Void> response2 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //invalid value
        req.setEndpointId("0E9999999999");
        ResponseEntity<Void> response3 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //other tenant's id
        req.setEndpointId(AppConstant.MT_ACCESS_ENDPOINT_ID);
        ResponseEntity<Void> response4 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_create_project_id() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        SubscriptionReq req =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        //send sub req tenantB
        //null
        req.setProjectId(null);
        ResponseEntity<Void> response =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        req.setProjectId(" ");
        ResponseEntity<Void> response1 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        req.setProjectId("");
        ResponseEntity<Void> response2 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //invalid value
        req.setProjectId("");
        ResponseEntity<Void> response3 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //other tenant's id
        req.setProjectId(AppConstant.MT_ACCESS_PROJECT_ID);
        ResponseEntity<Void> response4 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.FORBIDDEN, response4.getStatusCode());
    }

    @Test
    public void validation_create_burst_capacity() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        SubscriptionReq req =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        //send sub req tenantB
        //null
        req.setBurstCapacity(null);
        ResponseEntity<Void> response =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //min value
        req.setBurstCapacity(0);
        ResponseEntity<Void> response1 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max value
        req.setBurstCapacity(Integer.MAX_VALUE);
        ResponseEntity<Void> response2 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_create_replenish_rate() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        SubscriptionReq req =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        //send sub req tenantB
        //null
        req.setReplenishRate(null);
        ResponseEntity<Void> response =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //min value
        req.setReplenishRate(0);
        ResponseEntity<Void> response1 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max value
        req.setReplenishRate(Integer.MAX_VALUE);
        ResponseEntity<Void> response2 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //invalid value, bust capacity < replenish rate
        req.setReplenishRate(60);
        req.setBurstCapacity(20);
        ResponseEntity<Void> response3 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_reject() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        String subReqId = UrlUtility.getId(voidResponseEntity);
        //tenantA reject request
        RejectSubRequestCommand command = new RejectSubRequestCommand();
        //null
        command.setRejectionReason(null);
        ResponseEntity<Void> response =
            MarketUtility.rejectSubReq(tenantContextA, subReqId, command);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        command.setRejectionReason(" ");
        ResponseEntity<Void> response2 =
            MarketUtility.rejectSubReq(tenantContextA, subReqId, command);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        command.setRejectionReason("");
        ResponseEntity<Void> response3 =
            MarketUtility.rejectSubReq(tenantContextA, subReqId, command);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //min length
        command.setRejectionReason("1");
        ResponseEntity<Void> response4 =
            MarketUtility.rejectSubReq(tenantContextA, subReqId, command);
        Assert.assertEquals(HttpStatus.OK, response4.getStatusCode());
        //max length
        command.setRejectionReason("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            MarketUtility.rejectSubReq(tenantContextA, subReqId, command);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid char
        command.setRejectionReason("<");
        ResponseEntity<Void> response6 =
            MarketUtility.rejectSubReq(tenantContextA, subReqId, command);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_update_burst_capacity() {
        //null
        //blank
        //empty
        //min value
        //max value
        //invalid value
    }

    @Test
    public void validation_update_replenish_rate() {
        //null
        //blank
        //empty
        //min value
        //max value
        //invalid value
    }

}
