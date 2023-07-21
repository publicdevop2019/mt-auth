package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.args.SubRequestCapacityArgs;
import com.mt.test_case.helper.args.SubRequestEndpointIdArgs;
import com.mt.test_case.helper.args.SubRequestProjectIdArgs;
import com.mt.test_case.helper.args.SubRequestRejectArgs;
import com.mt.test_case.helper.args.SubRequestReplenishArgs;
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
import java.util.Set;
import java.util.stream.Collectors;
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
public class TenantMarketTest {
    protected static TenantContext tenantContextA;
    protected static TenantContext tenantContextB;
    protected static Client clientA;

    @BeforeAll
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
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
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
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
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
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
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
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
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
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
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
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        String subReqId = UrlUtility.getId(voidResponseEntity);
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
    public void tenant_assign_approved_api_to_role_when_api_delete_cleanup_performed()
        throws InterruptedException {
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
        Thread.sleep(5 * 1000);
        //update it's api
        ResponseEntity<SumTotal<Permission>> shared =
            PermissionUtility.readTenantPermissionShared(tenantContextB);
        String permissionId = shared.getBody().getData().stream()
            .filter(e->e.getName().equalsIgnoreCase(
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
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        //send sub req tenantB
        SubscriptionReq randomTenantSubReqObj =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> voidResponseEntity =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), randomTenantSubReqObj);
        String subReqId = UrlUtility.getId(voidResponseEntity);
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
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContextB.getProject(), user, role);
        role.setId(UrlUtility.getId(tenantRole));
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

    @ParameterizedTest
    @ArgumentsSource(SubRequestEndpointIdArgs.class)
    public void validation_create_endpoint_id(String endpointId, HttpStatus status) {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        SubscriptionReq req =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        //send sub req tenantB
        req.setEndpointId(endpointId);
        ResponseEntity<Void> response =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(SubRequestProjectIdArgs.class)
    public void validation_create_project_id(String projectId, HttpStatus status) {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        SubscriptionReq req =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        //send sub req tenantB
        req.setProjectId(projectId);
        ResponseEntity<Void> response =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(SubRequestCapacityArgs.class)
    public void validation_create_burst_capacity(Integer burstCapacity, HttpStatus status) {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        SubscriptionReq req =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        //send sub req tenantB
        req.setBurstCapacity(burstCapacity);
        ResponseEntity<Void> response =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(SubRequestReplenishArgs.class)
    public void validation_create_replenish_rate(Integer replenishRate, HttpStatus status) {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        SubscriptionReq req =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        //send sub req tenantB
        req.setReplenishRate(replenishRate);
        ResponseEntity<Void> response =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(SubRequestRejectArgs.class)
    public void validation_reject(String reject, HttpStatus status) {
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
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        String subReqId = UrlUtility.getId(voidResponseEntity);
        //tenantA reject request
        RejectSubRequestCommand command = new RejectSubRequestCommand();
        command.setRejectionReason(reject);
        ResponseEntity<Void> response =
            MarketUtility.rejectSubReq(tenantContextA, subReqId, command);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(SubRequestCapacityArgs.class)
    public void validation_update_burst_capacity(Integer burstCapacity, HttpStatus status) {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        SubscriptionReq req =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> response4 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        req.setId(UrlUtility.getId(response4));
        //send sub req tenantB
        req.setBurstCapacity(burstCapacity);
        ResponseEntity<Void> response =
            MarketUtility.updateSubReq(tenantContextB.getCreator(), req);
        Assertions.assertEquals(status, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(SubRequestReplenishArgs.class)
    public void validation_update_replenish_rate(Integer replenishRate, HttpStatus status) {
        //create shared endpoint tenantA
        Endpoint endpoint =
            EndpointUtility.createValidSharedEndpointObj(clientA.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContextA, endpoint);
        endpoint.setId(UrlUtility.getId(tenantEndpoint));
        SubscriptionReq req =
            MarketUtility.createValidSubReq(tenantContextB, endpoint.getId());
        ResponseEntity<Void> response4 =
            MarketUtility.subToEndpoint(tenantContextB.getCreator(), req);
        req.setId(UrlUtility.getId(response4));
        //send sub req tenantB
        req.setReplenishRate(replenishRate);
        ResponseEntity<Void> response =
            MarketUtility.updateSubReq(tenantContextB.getCreator(), req);
        Assertions.assertEquals(status, response.getStatusCode());
    }

}
