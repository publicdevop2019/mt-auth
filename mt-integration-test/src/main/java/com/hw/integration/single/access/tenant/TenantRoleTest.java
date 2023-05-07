package com.hw.integration.single.access.tenant;

import com.hw.helper.Client;
import com.hw.helper.Endpoint;
import com.hw.helper.Permission;
import com.hw.helper.Role;
import com.hw.helper.SumTotal;
import com.hw.helper.UpdateType;
import com.hw.helper.User;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.EndpointUtility;
import com.hw.helper.utility.PermissionUtility;
import com.hw.helper.utility.RandomUtility;
import com.hw.helper.utility.RoleUtility;
import com.hw.helper.utility.TenantUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UserUtility;
import java.util.ArrayList;
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
public class TenantRoleTest {
    private static TenantUtility.TenantContext tenantContext;
    private static Role rootRole;
    private static Endpoint sharedEndpointObj;

    @BeforeClass
    public static void initTenant() {
        log.info("init tenant in progress");
        TestContext.init();
        tenantContext = TenantUtility.initTenant();
        //create root node
        rootRole = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole = RoleUtility.createTenantRole(tenantContext, rootRole);
        rootRole.setId(tenantRole.getHeaders().getLocation().toString());
        log.info("init tenant complete");

        //create client for endpoint
        Client randomBackendClientObj = ClientUtility.createRandomSharedBackendClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, randomBackendClientObj);
        String clientId = tenantClient.getHeaders().getLocation().toString();
        //create shared endpoint
        sharedEndpointObj = EndpointUtility.createRandomSharedEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext, sharedEndpointObj);
        sharedEndpointObj.setId(tenantEndpoint2.getHeaders().getLocation().toString());
    }

    @Test
    public void tenant_can_create_role() {
        Role randomRoleObj = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, randomRoleObj);
        Assert.assertEquals(HttpStatus.OK, tenantRole.getStatusCode());
        Assert.assertNotNull(
            Objects.requireNonNull(tenantRole.getHeaders().getLocation()).toString());
    }

    @Test
    public void tenant_can_view_role_list() {
        ResponseEntity<SumTotal<Role>> sumTotalResponseEntity =
            RoleUtility.readTenantRole(tenantContext);
        Assert.assertEquals(HttpStatus.OK, sumTotalResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_view_role_tree() {
        //query root node
        ResponseEntity<SumTotal<Role>> response =
            RoleUtility.readTenantRoleWithQuery(tenantContext,
                "query=parentId:null,types:USER.PROJECT");
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotSame(0, response.getBody().getData().size());
        //query by parent id
        ResponseEntity<SumTotal<Role>> sumTotalResponseEntity2 =
            RoleUtility.readTenantRoleWithQuery(tenantContext,
                "query=parentId:" + rootRole.getId());
        Assert.assertEquals(HttpStatus.OK, sumTotalResponseEntity2.getStatusCode());
    }

    @Test
    public void tenant_can_view_role_detail() {
        ResponseEntity<Role> response =
            RoleUtility.readTenantRoleById(tenantContext,
                rootRole);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody().getName());
    }

    @Test
    public void tenant_can_delete_role() {
        Role randomRoleObj = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, randomRoleObj);
        randomRoleObj.setId(tenantRole.getHeaders().getLocation().toString());
        ResponseEntity<Void> voidResponseEntity = RoleUtility.deleteTenantRole(tenantContext,
            randomRoleObj);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_delete_assigned_role() {
        //create role
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(tenantRole.getHeaders().getLocation().toString());
        //read user
        User user = tenantContext.getUsers().get(0);
        ResponseEntity<User> userResponseEntity = UserUtility.readTenantUser(tenantContext, user);
        User body = userResponseEntity.getBody();
        ArrayList<String> strings = new ArrayList<>();
        strings.add(role.getId());
        strings.addAll(body.getRoles());
        body.setRoles(strings);
        //assign role
        ResponseEntity<Void> voidResponseEntity = UserUtility.updateTenantUser(tenantContext, body);
        Assert.assertEquals(HttpStatus.OK,voidResponseEntity.getStatusCode());
        //delete role
        ResponseEntity<Void> voidResponseEntity2 = RoleUtility.deleteTenantRole(tenantContext,
            role);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity2.getStatusCode());
    }

    @Test
    public void tenant_can_update_role_detail() {
        //create role
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(tenantRole.getHeaders().getLocation().toString());
        //update it's permission
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(response.getHeaders().getLocation().toString());
        role.setCommonPermissionIds(Collections.singleton(permission.getId()));
        role.setType(UpdateType.COMMON_PERMISSION.name());
        ResponseEntity<Void> response2 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assert.assertEquals(HttpStatus.OK, response2.getStatusCode());
        //update it's api
        ResponseEntity<SumTotal<Permission>> sumTotalResponseEntity =
            PermissionUtility.readTenantPermissionWithQuery(tenantContext,
                "query=parentId:null,types:API");
        String permissionId = sumTotalResponseEntity.getBody().getData().get(0).getId();
        role.setApiPermissionIds(Collections.singleton(permissionId));
        role.setType(UpdateType.API_PERMISSION.name());
        ResponseEntity<Void> response4 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assert.assertEquals(HttpStatus.OK, response4.getStatusCode());
        //update basic info
        role.setName(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> response3 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assert.assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    public void role_validation_should_work() {
        //update type is required
        //name is required
//        Role randomRoleObj = RoleUtility.createRandomRoleObj();
//        randomRoleObj.setName(null);
//        ResponseEntity<Void> tenantRole =
//            RoleUtility.createTenantRole(tenantContext, randomRoleObj);
//        Assert.assertEquals(HttpStatus.BAD_REQUEST, tenantRole.getStatusCode());
    }
}
