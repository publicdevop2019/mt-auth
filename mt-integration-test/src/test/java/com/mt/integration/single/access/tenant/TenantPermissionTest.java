package com.mt.integration.single.access.tenant;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.Permission;
import com.mt.helper.pojo.Role;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.UpdateType;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.PermissionUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.RoleUtility;
import com.mt.helper.utility.TenantUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UrlUtility;
import java.util.Collections;
import java.util.List;
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
public class TenantPermissionTest {
    private static TenantContext tenantContext;
    private static Permission rootPermission;
    private static Endpoint publicEndpointObj;
    private static Endpoint sharedEndpointObj;
    private static Client client;
    private static Role role;

    @BeforeAll
    public static void initTenant() {
        TestHelper.beforeAll(log);
        log.info("init tenant in progress");
        tenantContext = TenantUtility.initTenant();
        //create root node
        rootPermission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response = PermissionUtility.createTenantPermission(tenantContext,
            rootPermission);
        rootPermission.setId(UrlUtility.getId(response));
        log.info("init tenant complete");
        //create client for endpoint
        client = ClientUtility.createRandomSharedBackendClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        String clientId = UrlUtility.getId(tenantClient);
        client.setId(clientId);
        //create public endpoint
        publicEndpointObj = EndpointUtility.createRandomPublicEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, publicEndpointObj);
        publicEndpointObj.setId(UrlUtility.getId(tenantEndpoint));
        //create shared endpoint
        sharedEndpointObj = EndpointUtility.createValidSharedEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext, sharedEndpointObj);
        sharedEndpointObj.setId(UrlUtility.getId(tenantEndpoint2));
        //create rol
        role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(tenantRole));

    }

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach(log);
    }
    @Test
    public void tenant_can_create_permission() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(UrlUtility.getId(response));
    }

    @Test
    public void tenant_can_update_permission() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        String s = UrlUtility.getId(response);
        permission.setId(s);
        permission.setName(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        ResponseEntity<Permission> permissionResponseEntity =
            PermissionUtility.readTenantPermissionById(tenantContext, permission);
        Assertions.assertEquals(1, permissionResponseEntity.getBody().getVersion().intValue());

    }

    @Test
    public void tenant_can_view_permission_list() {
        ResponseEntity<SumTotal<Permission>> response2 =
            PermissionUtility.readTenantPermission(tenantContext);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assertions.assertNotSame(0, response2.getBody().getData().size());
    }

    @Test
    public void tenant_can_delete_permission() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        String s = UrlUtility.getId(response);
        permission.setId(s);
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.deleteTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_delete_assigned_permission() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        String s = UrlUtility.getId(response);
        permission.setId(s);
        role.setCommonPermissionIds(Collections.singleton(permission.getId()));
        role.setType(UpdateType.COMMON_PERMISSION.name());
        ResponseEntity<Void> voidResponseEntity2 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity2.getStatusCode());
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.deleteTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void permission_cleanup_after_linked_endpoint_deleted() throws InterruptedException {
        //create secured endpoint
        Endpoint endpoint =
            EndpointUtility.createValidGetEndpoint(client.getId());
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, endpoint);
        String endpointId = UrlUtility.getId(tenantEndpoint);
        endpoint.setId(endpointId);
        //link this endpoint to permission
        Permission permission = PermissionUtility.createRandomPermissionObj();
        permission.setLinkedApiIds(List.of(endpointId));
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        String s = UrlUtility.getId(response);
        permission.setId(s);
        ResponseEntity<Permission> permissionResponseEntity =
            PermissionUtility.readTenantPermissionById(tenantContext, permission);
        //linked permission added
        List<String> linkedApiPermissionIds =
            permissionResponseEntity.getBody().getLinkedApiPermissionIds();
        Assertions.assertEquals(1, linkedApiPermissionIds.size());

        //delete endpoint
        ResponseEntity<Void> response1 =
            EndpointUtility.deleteTenantEndpoint(tenantContext, endpoint);
        //wait for cleanup
        Thread.sleep(5 * 1000);
        //permission linked api permission should be cleanup
        ResponseEntity<Permission> afterCleanup =
            PermissionUtility.readTenantPermissionById(tenantContext, permission);
        //linked permission added
        List<String> list =
            afterCleanup.getBody().getLinkedApiPermissionIds();
        Assertions.assertEquals(0, list.size());
    }

    @Test
    public void tenant_can_delete_assigned_permission_with_linked_endpoint() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        permission.setLinkedApiIds(Collections.singletonList(sharedEndpointObj.getId()));
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        String s = UrlUtility.getId(response);
        permission.setId(s);
        role.setCommonPermissionIds(Collections.singleton(permission.getId()));
        role.setType(UpdateType.COMMON_PERMISSION.name());
        ResponseEntity<Void> voidResponseEntity2 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity2.getStatusCode());
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.deleteTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_create_permission_with_linked_endpoint() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        permission.setLinkedApiIds(Collections.singletonList(sharedEndpointObj.getId()));
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(UrlUtility.getId(response));
        String s = UrlUtility.getId(response);
        permission.setId(s);
        ResponseEntity<Permission> permissionResponseEntity =
            PermissionUtility.readTenantPermissionById(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, permissionResponseEntity.getStatusCode());
        Assertions.assertEquals(1,
            permissionResponseEntity.getBody().getLinkedApiPermissionIds().size());

    }

    @Test
    public void tenant_can_update_permission_with_linked_endpoint() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        permission.setLinkedApiIds(Collections.singletonList(sharedEndpointObj.getId()));
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(UrlUtility.getId(response));
        String s = UrlUtility.getId(response);
        permission.setId(s);
        permission.setLinkedApiIds(Collections.emptyList());
        //update
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());

        ResponseEntity<Permission> permissionResponseEntity =
            PermissionUtility.readTenantPermissionById(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, permissionResponseEntity.getStatusCode());
        Assertions.assertEquals(0,
            permissionResponseEntity.getBody().getLinkedApiPermissionIds().size());
        Assertions.assertEquals(1, permissionResponseEntity.getBody().getVersion().intValue());
    }

    @Test
    public void tenant_can_view_permission_tree() {
        ResponseEntity<SumTotal<Permission>> response2 =
            PermissionUtility.readTenantPermissionWithQuery(tenantContext,
                "query=types:COMMON,parentId:null");
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assertions.assertNotSame(0, response2.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_permission_detail() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        String s = UrlUtility.getId(response);
        permission.setId(s);
        ResponseEntity<Permission> response2 =
            PermissionUtility.readTenantPermissionById(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assertions.assertEquals(0, response2.getBody().getVersion().intValue());

    }

}
