package com.mt.test_case.integration.single.access.tenant;

import static com.mt.test_case.helper.AppConstant.MT_ACCESS_ENDPOINT_ID;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.Permission;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.Role;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.UpdateType;
import com.mt.test_case.helper.utility.CacheUtility;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.PermissionUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.RoleUtility;
import com.mt.test_case.helper.utility.TenantUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.Utility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    private static TenantContext tenantContext;
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
        rootPermission.setId(UrlUtility.getId(response));
        log.info("init tenant complete");
        //create client for endpoint
        Client randomBackendClientObj = ClientUtility.createRandomSharedBackendClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, randomBackendClientObj);
        String clientId = UrlUtility.getId(tenantClient);
        //create public endpoint
        publicEndpointObj = EndpointUtility.createRandomPublicEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint =
            EndpointUtility.createTenantEndpoint(tenantContext, publicEndpointObj);
        publicEndpointObj.setId(UrlUtility.getId(tenantEndpoint));
        //create shared endpoint
        sharedEndpointObj = EndpointUtility.createRandomSharedEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext, sharedEndpointObj);
        sharedEndpointObj.setId(UrlUtility.getId(tenantEndpoint2));
        //create rol
        role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(tenantRole));

    }

    @Test
    public void tenant_can_create_permission() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(UrlUtility.getId(response));
    }

    @Test
    public void tenant_can_update_permission() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        String s = UrlUtility.getId(response);
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
        String s = UrlUtility.getId(response);
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
        String s = UrlUtility.getId(response);
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
        String s = UrlUtility.getId(response);
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
        Assert.assertNotNull(UrlUtility.getId(response));
        String s = UrlUtility.getId(response);
        randomPermissionObj.setId(s);
        ResponseEntity<Permission> permissionResponseEntity =
            PermissionUtility.readTenantPermissionById(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, permissionResponseEntity.getStatusCode());
        Assert.assertEquals(1,
            permissionResponseEntity.getBody().getLinkedApiPermissionIds().size());

    }

    @Test
    public void tenant_can_update_permission_with_linked_endpoint() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        permission.setLinkedApiIds(Collections.singletonList(sharedEndpointObj.getId()));
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(UrlUtility.getId(response));
        String s = UrlUtility.getId(response);
        permission.setId(s);
        permission.setLinkedApiIds(Collections.emptyList());
        //update
        ResponseEntity<Void> voidResponseEntity =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());

        ResponseEntity<Permission> permissionResponseEntity =
            PermissionUtility.readTenantPermissionById(tenantContext, permission);
        Assert.assertEquals(HttpStatus.OK, permissionResponseEntity.getStatusCode());
        Assert.assertEquals(0,
            permissionResponseEntity.getBody().getLinkedApiPermissionIds().size());
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
        String s = UrlUtility.getId(response);
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

    @Test
    public void validation_create_name() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        //null
        randomPermissionObj.setName(null);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        randomPermissionObj.setName(" ");
        ResponseEntity<Void> response2 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        randomPermissionObj.setName("");
        ResponseEntity<Void> response3 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        randomPermissionObj.setName("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response4 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        randomPermissionObj.setName("<");
        ResponseEntity<Void> response5 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_parent_id() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        //null
        randomPermissionObj.setParentId(null);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response1.getStatusCode());
        //blank
        randomPermissionObj.setParentId(" ");
        ResponseEntity<Void> response2 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        randomPermissionObj.setParentId("");
        ResponseEntity<Void> response3 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //invalid value
        randomPermissionObj.setParentId("123");
        ResponseEntity<Void> response4 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //other tenant's id
        randomPermissionObj.setParentId(AppConstant.MT_ACCESS_PERMISSION_ID);
        ResponseEntity<Void> response5 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //not exist id
        randomPermissionObj.setParentId("0Y99999999");
        ResponseEntity<Void> response6 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_project_id() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        //null
        Project project1 = new Project();
        project1.setId("null");
        String url = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, resource.getStatusCode());
        //empty
        project1.setId("");
        String url3 = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource3 =
            Utility.createResource(tenantContext.getCreator(), url3, randomPermissionObj);
        Assert.assertEquals(HttpStatus.FORBIDDEN, resource3.getStatusCode());
        //blank
        project1.setId(" ");
        String url4 = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource4 =
            Utility.createResource(tenantContext.getCreator(), url4, randomPermissionObj);
        Assert.assertEquals(HttpStatus.FORBIDDEN, resource4.getStatusCode());
        //other tenant's project id
        project1.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url2 = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource2 =
            Utility.createResource(tenantContext.getCreator(), url2, randomPermissionObj);
        Assert.assertEquals(HttpStatus.FORBIDDEN, resource2.getStatusCode());
    }

    @Test
    public void validation_create_linked_api_ids() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        //null
        randomPermissionObj.setLinkedApiIds(null);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response1.getStatusCode());
        //empty collection
        randomPermissionObj.setLinkedApiIds(Collections.emptyList());
        ResponseEntity<Void> response2 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response2.getStatusCode());
        //blank
        randomPermissionObj.setLinkedApiIds(Collections.singletonList(""));
        ResponseEntity<Void> response3 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //empty
        randomPermissionObj.setLinkedApiIds(Collections.singletonList(" "));
        ResponseEntity<Void> response4 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        List<String> strings = new ArrayList<>();
        strings.add(sharedEndpointObj.getId());
        strings.add("0E8AZTODP401");
        strings.add("0E8AZTODP402");
        strings.add("0E8AZTODP403");
        strings.add("0E8AZTODP404");
        strings.add("0E8AZTODP405");
        randomPermissionObj.setLinkedApiIds(strings);
        ResponseEntity<Void> response5 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid value
        randomPermissionObj.setLinkedApiIds(Collections.singletonList("abc"));
        ResponseEntity<Void> response6 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //other tenant's id
        randomPermissionObj.setLinkedApiIds(Collections.singletonList(MT_ACCESS_ENDPOINT_ID));
        ResponseEntity<Void> response7 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
        //not exist id
        randomPermissionObj.setLinkedApiIds(Collections.singletonList("0E99999999"));
        ResponseEntity<Void> response8 =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
    }

    @Test
    public void validation_update_name() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }

    @Test
    public void validation_update_project_id() {
        //null
        //blank
        //empty
        //invalid value
        //other tenant's id
    }

    @Test
    public void validation_update_linked_api_ids() {
        //null
        //blank
        //empty
        //too many elements
        //invalid value
        //other tenant's id
    }

    @Test
    public void validation_patch_name() {
        //null
        //blank
        //empty
        //min length
        //max length
        //invalid char
    }
}
