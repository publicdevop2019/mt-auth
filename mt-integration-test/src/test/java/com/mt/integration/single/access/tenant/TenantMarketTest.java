package com.mt.integration.single.access.tenant;

import static com.mt.helper.AppConstant.X_MT_RATELIMIT_LEFT;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.AssignRoleReq;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.Permission;
import com.mt.helper.pojo.Role;
import com.mt.helper.pojo.SubscriptionReq;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.UpdateType;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.MarketUtility;
import com.mt.helper.utility.PermissionUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.RoleUtility;
import com.mt.helper.utility.TenantUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.TestUtility;
import com.mt.helper.utility.UserUtility;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantMarketTest {
    protected static TenantContext tenantContextA;
    protected static TenantContext tenantContextB;
    protected static Client clientA;

    @BeforeAll
    public static void initTenant() {
        TestHelper.beforeAll(log);
        log.info("init tenant in progress");
        tenantContextA = TenantUtility.initTenant();
        tenantContextB = TenantUtility.initTenant();

        clientA = ClientUtility.createValidBackendClient();
        clientA.setResourceIndicator(true);
        clientA.setExternalUrl("http://localhost:9999");
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContextA, clientA);
        clientA.setId(HttpUtility.getId(tenantClient));

        log.info("init tenant complete");
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void tenant_can_view_api_on_market() {
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.readMarketEndpoint(creator);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotSame(0, response.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_public_api() {
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.readMarketEndpoint(creator);
        List<Endpoint> collect = response.getBody().getData().stream().filter(e -> !e.getSecured())
            .collect(Collectors.toList());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotSame(0, collect.size());
    }

    @Test
    public void tenant_can_view_shared_api() {
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.readMarketEndpoint(creator);
        List<Endpoint> collect = response.getBody().getData().stream().filter(Endpoint::getSecured)
            .collect(Collectors.toList());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotSame(0, collect.size());
    }

    @Test
    public void tenant_can_view_its_public_api_in_market() {
        //create public endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        //find this endpoint in market
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.searchMarketEndpoint(creator, endpoint.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertSame(1, response.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_its_shared_api_in_market() {
        //create shared endpoint
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        //find this endpoint in market
        User creator = tenantContextA.getCreator();
        ResponseEntity<SumTotal<Endpoint>> response =
            MarketUtility.searchMarketEndpoint(creator, endpoint.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertSame(1, response.getBody().getData().size());
        Assertions.assertTrue(response.getBody().getData().stream().findFirst().get().getSecured());
    }

    @Test
    public void tenant_can_send_sub_req_for_shared_api() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_cannot_send_sub_req_for_its_shared_api() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextA, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextA.getCreator(), randomTenantSubReqObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_cannot_send_sub_req_for_public_api() {
        //create public endpoint
        Endpoint endpoint =
            EndpointUtility.createRandomPublicEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_approve_sub_req_for_shared_endpoint() {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        String subReqId = HttpUtility.getId(voidResponseEntity);
        //tenantB can view sub req
        ResponseEntity<SumTotal<SubscriptionReq>> voidResponseEntity12 =
            MarketUtility.viewMySubReq(tenantContextB);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity12.getStatusCode());
        Assertions.assertNotSame(0, voidResponseEntity12.getBody().getData().size());
        //tenantA can view request
        ResponseEntity<SumTotal<SubscriptionReq>> voidResponseEntity13 =
            MarketUtility.viewMyPendingApprove(tenantContextA);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity13.getStatusCode());
        Assertions.assertNotSame(0, voidResponseEntity13.getBody().getData().size());
        //tenantA approve request
        ResponseEntity<Void> voidResponseEntity1 =
            MarketUtility.approveSubReq(tenantContextA, subReqId);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity1.getStatusCode());
        //tenantB can view approved req
        ResponseEntity<SumTotal<SubscriptionReq>> voidResponseEntity14 =
            MarketUtility.viewMySubs(tenantContextB);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity14.getStatusCode());
        Assertions.assertNotSame(0, voidResponseEntity14.getBody().getData().size());
    }

    @Test
    public void tenant_can_assign_approved_api_to_role() throws InterruptedException {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        String subReqId = HttpUtility.getId(voidResponseEntity);
        //approve sub req
        MarketUtility.approveSubReq(tenantContextA, subReqId);

        //create tenantB role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContextB, role);
        role.setId(HttpUtility.getId(tenantRole));
        //wait for cache to expire
        Thread.sleep(5 * 1000);
        //update it's api
        ResponseEntity<SumTotal<Permission>> shared =
            PermissionUtility.readTenantPermissionShared(tenantContextB);
        String permissionId = shared.getBody().getData().get(0).getId();
        role.setExternalPermissionIds(Collections.singleton(permissionId));
        role.setType(UpdateType.API_PERMISSION.name());
        ResponseEntity<Void> response4 =
            RoleUtility.updateTenantRole(tenantContextB, role);
        Assertions.assertEquals(HttpStatus.OK, response4.getStatusCode());
    }

    @Test
    public void tenant_can_assign_approved_api_to_role_then_call_it_w_rate_limit()
        throws InterruptedException {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        endpoint.setPath("test/expire/" + RandomUtility.randomStringNoNum() + "/random");
        endpoint.setMethod("GET");
        endpoint.setReplenishRate(10);
        endpoint.setBurstCapacity(20);

        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        String name = endpoint.getName();
        //send sub req tenantB
        SubscriptionReq subReq =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), subReq);
        String subReqId = HttpUtility.getId(voidResponseEntity);
        //approve sub req
        MarketUtility.approveSubReq(tenantContextA, subReqId);

        //create tenantB role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContextB, role);
        role.setId(HttpUtility.getId(tenantRole));
        //update it's api
        ResponseEntity<SumTotal<Permission>> shared =
            PermissionUtility.readTenantPermissionShared(tenantContextB);
        String permissionId = shared.getBody()
            .getData().stream().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst().get()
            .getId();
        role.setExternalPermissionIds(Collections.singleton(permissionId));
        role.setType(UpdateType.API_PERMISSION.name());
        ResponseEntity<Void> response4 =
            RoleUtility.updateTenantRole(tenantContextB, role);
        Assertions.assertEquals(HttpStatus.OK, response4.getStatusCode());
        //read user
        User user = tenantContextB.getUsers().get(0);
        AssignRoleReq assignRoleReq = new AssignRoleReq();
        assignRoleReq.getRoleIds().add(role.getId());
        //assign role
        ResponseEntity<Void> response =
            UserUtility.assignTenantUserRole(tenantContextB, user, assignRoleReq);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        TestUtility.proxyDefaultWait();

        String tenantAUrl = HttpUtility.getTenantUrl(clientA.getPath(), endpoint.getPath());
        String jwt = UserUtility.userLoginToTenant(tenantContextB.getProject(),
            tenantContextB.getLoginClient(), user).getBody().getValue();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(jwt);
        HttpEntity<Void> entity =
            new HttpEntity<>(headers1);
        ResponseEntity<String> exchange = TestContext.getRestTemplate()
            .exchange(tenantAUrl, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String first = exchange.getHeaders().getFirst(X_MT_RATELIMIT_LEFT);
        Assertions.assertEquals("19", first);

    }

    @Test
    public void tenant_assign_approved_api_to_role_when_api_delete_cleanup_performed()
        throws InterruptedException {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        String subReqId = HttpUtility.getId(voidResponseEntity);
        //approve sub req
        MarketUtility.approveSubReq(tenantContextA, subReqId);

        //create tenantB role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContextB, role);
        role.setId(HttpUtility.getId(tenantRole));
        //wait for cache to expire
        Thread.sleep(5 * 1000);
        //update it's api
        ResponseEntity<SumTotal<Permission>> shared =
            PermissionUtility.readTenantPermissionShared(tenantContextB);
        String permissionId = shared.getBody().getData().stream()
            .filter(e -> e.getName().equalsIgnoreCase(
                endpoint.getName())).findFirst().get().getId();

        role.setExternalPermissionIds(Collections.singleton(permissionId));
        role.setType(UpdateType.API_PERMISSION.name());
        ResponseEntity<Void> response4 =
            RoleUtility.updateTenantRole(tenantContextB, role);
        //api is added
        ResponseEntity<Role> roleResponseEntity2 =
            RoleUtility.readTenantRoleById(tenantContextB, role);
        Assertions.assertEquals(1, roleResponseEntity2.getBody()
            .getExternalPermissionIds().size());

        Assertions.assertEquals(HttpStatus.OK, response4.getStatusCode());
        //expire then delete endpoint
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.expireTenantEndpoint(tenantContextA, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint2.getStatusCode());
        ResponseEntity<Void> tenantEndpoint3 =
            EndpointUtility.deleteTenantEndpoint(tenantContextA, endpoint);
        Assertions.assertEquals(HttpStatus.OK, tenantEndpoint3.getStatusCode());
        //wait for cleanup
        Thread.sleep(10 * 1000);
        ResponseEntity<Role> roleResponseEntity =
            RoleUtility.readTenantRoleById(tenantContextB, role);
        Assertions.assertEquals(0, roleResponseEntity.getBody()
            .getExternalPermissionIds().size());
    }

    @Test
    public void tenant_other_admin_can_see_and_assign_approved_api_to_role_requested_by_dif_admin()
        throws InterruptedException {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(HttpUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        String subReqId = HttpUtility.getId(voidResponseEntity);
        //approve sub req
        MarketUtility.approveSubReq(tenantContextA, subReqId);
        User user = tenantContextB.getUsers().get(0);
        //dif admin can see approve api
        ResponseEntity<SumTotal<Permission>> sumTotalResponseEntity =
            PermissionUtility.readTenantPermissionShared(tenantContextB.getProject(), user
            );
        Set<Permission> collect = sumTotalResponseEntity.getBody().getData().stream()
            .filter(e -> e.getName().equalsIgnoreCase(endpoint.getName())).collect(
                Collectors.toSet());
        Assertions.assertEquals(1, collect.size());
        //create tenantB role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContextB.getProject(), user, role);
        role.setId(HttpUtility.getId(tenantRole));
        //wait for cache to expire
        Thread.sleep(5 * 1000);
        //update it's api
        ResponseEntity<SumTotal<Permission>> shared =
            PermissionUtility.readTenantPermissionShared(tenantContextB);
        String permissionId = shared.getBody().getData().get(0).getId();
        role.setExternalPermissionIds(Collections.singleton(permissionId));
        role.setType(UpdateType.API_PERMISSION.name());
        ResponseEntity<Void> response4 =
            RoleUtility.updateTenantRole(tenantContextB.getProject(),
                tenantContextB.getUsers().get(0), role);
        Assertions.assertEquals(HttpStatus.OK, response4.getStatusCode());
    }

}
