package com.hw.integration.single.access.tenant;

import com.hw.helper.Client;
import com.hw.helper.Endpoint;
import com.hw.helper.Permission;
import com.hw.helper.Role;
import com.hw.helper.SumTotal;
import com.hw.helper.UpdateType;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.PermissionUtility;
import com.hw.helper.utility.RandomUtility;
import com.hw.helper.utility.RoleUtility;
import com.hw.helper.utility.TenantUtility;
import com.hw.helper.utility.TestContext;
import java.util.Collections;
import java.util.Objects;
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
public class TenantPermissionTest {
    private static TenantUtility.TenantContext tenantContext;
    private static Permission rootPermission;
    private static Endpoint publicEndpointObj;
    private static Endpoint sharedEndpointObj;
    private static Role role;

    @BeforeClass
    public static void initTenant() {
        log.info("init tenant in progress");
        TestContext.init();
        tenantContext = TenantUtility.initTenant();
        //create root node
        rootPermission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response = PermissionUtility.createTenantPermission(tenantContext,
            rootPermission);
        rootPermission.setId(response.getHeaders().getLocation().toString());
        log.info("init tenant complete");
        //create client for endpoint
        Client randomBackendClientObj = ClientUtility.createRandomSharedBackendClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, randomBackendClientObj);
        String clientId = tenantClient.getHeaders().getLocation().toString();
        //create public endpoint
        publicEndpointObj = EndpointUtility.createRandomPublicEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, publicEndpointObj);
        publicEndpointObj.setId(tenantEndpoint.getHeaders().getLocation().toString());
        //create shared endpoint
        sharedEndpointObj = EndpointUtility.createRandomSharedEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext, sharedEndpointObj);
        sharedEndpointObj.setId(tenantEndpoint2.getHeaders().getLocation().toString());
        //create rol
        role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(tenantRole.getHeaders().getLocation().toString());

    }

    @Test
    public void tenant_can_create_permission() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(
            Objects.requireNonNull(response.getHeaders().getLocation()).toString());
    }

    @Test
    public void tenant_can_update_permission() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        String s = response.getHeaders().getLocation().toString();
        randomPermissionObj.setId(s);
        randomPermissionObj.setName(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.updateTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        ResponseEntity<Permission> permissionResponseEntity =
            PermissionUtility.readTenantPermissionById(tenantContext, randomPermissionObj);
        Assert.assertEquals(1, permissionResponseEntity.getBody().getVersion().intValue());

    }

    @Test
    public void tenant_can_view_permission_list() {
        ResponseEntity<SumTotal<Permission>> response2 =
            PermissionUtility.readTenantPermission(tenantContext);
        Assert.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assert.assertNotSame(0, response2.getBody().getData().size());
    }

    @Test
    public void tenant_can_delete_permission() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        String s = response.getHeaders().getLocation().toString();
        randomPermissionObj.setId(s);
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.deleteTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_delete_assigned_permission() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        String s = response.getHeaders().getLocation().toString();
        randomPermissionObj.setId(s);
        role.setCommonPermissionIds(Collections.singleton(randomPermissionObj.getId()));
        role.setType(UpdateType.COMMON_PERMISSION.name());
        ResponseEntity<Void> voidResponseEntity2 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity2.getStatusCode());
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.deleteTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_delete_assigned_permission_with_linked_endpoint() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        randomPermissionObj.setLinkedApiIds(Collections.singletonList(sharedEndpointObj.getId()));
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        String s = response.getHeaders().getLocation().toString();
        randomPermissionObj.setId(s);
        role.setCommonPermissionIds(Collections.singleton(randomPermissionObj.getId()));
        role.setType(UpdateType.COMMON_PERMISSION.name());
        ResponseEntity<Void> voidResponseEntity2 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity2.getStatusCode());
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.deleteTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_create_permission_with_linked_endpoint() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        randomPermissionObj.setLinkedApiIds(Collections.singletonList(sharedEndpointObj.getId()));
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(
            Objects.requireNonNull(response.getHeaders().getLocation()).toString());
        String s = response.getHeaders().getLocation().toString();
        randomPermissionObj.setId(s);
        ResponseEntity<Permission> permissionResponseEntity =
            PermissionUtility.readTenantPermissionById(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, permissionResponseEntity.getStatusCode());
        Assert.assertEquals(1, permissionResponseEntity.getBody().getLinkedApiPermissionIds().size());

    }

    @Test
    public void tenant_can_update_permission_with_linked_endpoint() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        permission.setLinkedApiIds(Collections.singletonList(sharedEndpointObj.getId()));
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(
            Objects.requireNonNull(response.getHeaders().getLocation()).toString());
        String s = response.getHeaders().getLocation().toString();
        permission.setId(s);
        permission.setLinkedApiIds(Collections.emptyList());
        //update
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());

        ResponseEntity<Permission> permissionResponseEntity =
            PermissionUtility.readTenantPermissionById(tenantContext, permission);
        Assert.assertEquals(HttpStatus.OK, permissionResponseEntity.getStatusCode());
        Assert.assertEquals(0, permissionResponseEntity.getBody().getLinkedApiPermissionIds().size());
        Assert.assertEquals(1, permissionResponseEntity.getBody().getVersion().intValue());
    }

    @Test
    public void tenant_can_view_permission_tree() {
        ResponseEntity<SumTotal<Permission>> response2 =
            PermissionUtility.readTenantPermissionWithQuery(tenantContext,
                "query=types:COMMON,parentId:null");
        Assert.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assert.assertNotSame(0, response2.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_permission_detail() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        String s = response.getHeaders().getLocation().toString();
        randomPermissionObj.setId(s);
        ResponseEntity<Permission> response2 =
            PermissionUtility.readTenantPermissionById(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assert.assertEquals(0, response2.getBody().getVersion().intValue());

    }

    @Test
    public void permission_validation_should_work() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        randomPermissionObj.setName(null);
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //cannot add public endpoints
    }
}
