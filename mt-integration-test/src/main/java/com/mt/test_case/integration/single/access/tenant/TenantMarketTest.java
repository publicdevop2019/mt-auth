package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.Permission;
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
import com.mt.test_case.helper.TenantContext;
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

        clientA = ClientUtility.createRandomBackendClientObj();
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
        List<Endpoint> collect = response.getBody().getData().stream().filter(e -> !e.isSecured())
            .collect(Collectors.toList());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotSame(0, collect.size());
    }

    @Test
    public void tenant_can_view_shared_api() {
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.readMarketEndpoint(creator);
        List<Endpoint> collect = response.getBody().getData().stream().filter(Endpoint::isShared)
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
            EndpointUtility.createRandomSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //find this endpoint in market
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.searchMarketEndpoint(creator, endpoint.getId());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertSame(1, response.getBody().getData().size());
        Assert.assertTrue(response.getBody().getData().stream().findFirst().get().isShared());
    }

    @Test
    public void tenant_can_send_sub_req_for_shared_api() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createRandomSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createRandomTenantSubReqObj(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_cannot_send_sub_req_for_its_shared_api() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createRandomSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createRandomTenantSubReqObj(tenantContextA, endpoint.getId());
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
            MarketUtility.createRandomTenantSubReqObj(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_approve_sub_req_for_shared_endpoint() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createRandomSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createRandomTenantSubReqObj(tenantContextB, endpoint.getId());
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
            EndpointUtility.createRandomSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createRandomTenantSubReqObj(tenantContextB, endpoint.getId());
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
        Thread.sleep(20*1000);
        //update it's api
        ResponseEntity<SumTotal<Permission>> shared =
            PermissionUtility.readTenantPermissionShared(tenantContextB);
        String permissionId = shared.getBody().getData().get(0).getId();
        role.setApiPermissionIds(Collections.singleton(permissionId));
        role.setType(UpdateType.API_PERMISSION.name());
        ResponseEntity<Void> response4 =
            RoleUtility.updateTenantRole(tenantContextB, role);
        Assert.assertEquals(HttpStatus.OK, response4.getStatusCode());
    }

    @Test
    public void validation_create_endpoint_id(){
        //null
        //blank
        //empty
        //invalid value
        //other tenant's id
    }
    @Test
    public void validation_create_project_id(){
        //null
        //blank
        //empty
        //invalid value
        //other tenant's id
    }
    @Test
    public void validation_create_burst_capacity(){
        //null
        //blank
        //empty
        //min value
        //max value
        //invalid value
    }
    @Test
    public void validation_create_replenish_rate(){
        //null
        //blank
        //empty
        //min value
        //max value
        //invalid value
    }
    @Test
    public void validation_update_burst_capacity(){
        //null
        //blank
        //empty
        //min value
        //max value
        //invalid value
    }
    @Test
    public void validation_update_replenish_rate(){
        //null
        //blank
        //empty
        //min value
        //max value
        //invalid value
    }

    @Test
    public void validation_reject(){
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
}
