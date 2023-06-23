package com.mt.test_case.integration.single.access.tenant;

import static com.mt.test_case.helper.AppConstant.MT_ACCESS_ENDPOINT_ID;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.PatchCommand;
import com.mt.test_case.helper.pojo.Permission;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.Role;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.UpdateType;
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
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
public class TenantPermissionTest {
    private static TenantContext tenantContext;
    private static Permission rootPermission;
    private static Endpoint publicEndpointObj;
    private static Endpoint sharedEndpointObj;
    private static Role role;

    @BeforeAll
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

    @Test
    public void validation_create_name() {
        Permission permission = PermissionUtility.createRandomPermissionObj();

        //null
        permission.setName(null);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        permission.setName(" ");
        ResponseEntity<Void> response2 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        permission.setName("");
        ResponseEntity<Void> response3 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        permission.setName("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response4 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        permission.setName("<");
        ResponseEntity<Void> response5 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_parent_id() {
        Permission permission = PermissionUtility.createRandomPermissionObj();

        //null
        permission.setParentId(null);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
        //blank
        permission.setParentId(" ");
        ResponseEntity<Void> response2 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        permission.setParentId("");
        ResponseEntity<Void> response3 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //invalid value
        permission.setParentId("123");
        ResponseEntity<Void> response4 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //other tenant's id
        permission.setParentId(AppConstant.MT_ACCESS_PERMISSION_ID);
        ResponseEntity<Void> response5 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //not exist id
        permission.setParentId("0Y99999999");
        ResponseEntity<Void> response6 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_project_id() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        //null
        Project project1 = new Project();
        project1.setId("null");
        String url = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resource.getStatusCode());
        //empty
        project1.setId("");
        String url3 = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource3 =
            Utility.createResource(tenantContext.getCreator(), url3, permission);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource3.getStatusCode());
        //blank
        project1.setId(" ");
        String url4 = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource4 =
            Utility.createResource(tenantContext.getCreator(), url4, permission);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource4.getStatusCode());
        //other tenant's project id
        project1.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url2 = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource2 =
            Utility.createResource(tenantContext.getCreator(), url2, permission);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource2.getStatusCode());
    }

    @Test
    public void validation_create_valid() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void validation_create_linked_api_ids() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        //null
        permission.setLinkedApiIds(null);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
        //empty collection
        permission.setLinkedApiIds(Collections.emptyList());
        ResponseEntity<Void> response2 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        //blank
        permission.setLinkedApiIds(Collections.singletonList(""));
        ResponseEntity<Void> response3 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //empty
        permission.setLinkedApiIds(Collections.singletonList(" "));
        ResponseEntity<Void> response4 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        List<String> strings = new ArrayList<>();
        strings.add(sharedEndpointObj.getId());
        strings.add("0E8AZTODP401");
        strings.add("0E8AZTODP402");
        strings.add("0E8AZTODP403");
        strings.add("0E8AZTODP404");
        strings.add("0E8AZTODP405");
        permission.setLinkedApiIds(strings);
        ResponseEntity<Void> response5 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid value
        permission.setLinkedApiIds(Collections.singletonList("abc"));
        ResponseEntity<Void> response6 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //other tenant's id
        permission.setLinkedApiIds(Collections.singletonList(MT_ACCESS_ENDPOINT_ID));
        ResponseEntity<Void> response7 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
        //not exist id
        permission.setLinkedApiIds(Collections.singletonList("0E99999999"));
        ResponseEntity<Void> response8 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
        //too many elements
        List<String> strings2 = new ArrayList<>();
        strings2.add("0E8AZTODP400");
        strings2.add("0E8AZTODP401");
        strings2.add("0E8AZTODP402");
        strings2.add("0E8AZTODP403");
        strings2.add("0E8AZTODP404");
        strings2.add("0E8AZTODP405");
        strings2.add("0E8AZTODP406");
        strings2.add("0E8AZTODP407");
        strings2.add("0E8AZTODP408");
        strings2.add("0E8AZTODP409");
        strings2.add("0E8AZTODP410");
        strings2.add("0E8AZTODP411");
        strings2.add("0E8AZTODP412");
        strings2.add("0E8AZTODP413");
        strings2.add("0E8AZTODP414");
        strings2.add("0E8AZTODP415");
        strings2.add("0E8AZTODP416");
        strings2.add("0E8AZTODP417");
        strings2.add("0E8AZTODP418");
        strings2.add("0E8AZTODP419");
        strings2.add("0E8AZTODP420");
        permission.setLinkedApiIds(strings2);
        ResponseEntity<Void> response9 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }

    @Test
    public void validation_update_name() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(UrlUtility.getId(response));

        //null
        permission.setName(null);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        permission.setName(" ");
        ResponseEntity<Void> response2 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        permission.setName("");
        ResponseEntity<Void> response3 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        permission.setName("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response4 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        permission.setName("<");
        ResponseEntity<Void> response5 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_update_project_id() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(UrlUtility.getId(response));

        //null
        Project project1 = new Project();
        project1.setId("null");
        String url = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.updateResource(tenantContext.getCreator(), url, permission, permission.getId());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resource.getStatusCode());
        //empty
        project1.setId("");
        String url3 = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource3 =
            Utility.updateResource(tenantContext.getCreator(), url3, permission,
                permission.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource3.getStatusCode());
        //blank
        project1.setId(" ");
        String url4 = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource4 =
            Utility.updateResource(tenantContext.getCreator(), url4, permission,
                permission.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource4.getStatusCode());
        //other tenant's project id
        project1.setId(AppConstant.MT_ACCESS_PROJECT_ID);
        String url2 = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource2 =
            Utility.updateResource(tenantContext.getCreator(), url2, permission,
                permission.getId());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, resource2.getStatusCode());
    }

    @Test
    public void validation_update_linked_api_ids() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(UrlUtility.getId(response));
        //null
        permission.setLinkedApiIds(null);
        ResponseEntity<Void> response1 =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
        //empty collection
        permission.setLinkedApiIds(Collections.emptyList());
        ResponseEntity<Void> response2 =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        //blank
        permission.setLinkedApiIds(Collections.singletonList(""));
        ResponseEntity<Void> response3 =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //empty
        permission.setLinkedApiIds(Collections.singletonList(" "));
        ResponseEntity<Void> response4 =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        List<String> strings = new ArrayList<>();
        strings.add(sharedEndpointObj.getId());
        strings.add("0E8AZTODP401");
        strings.add("0E8AZTODP402");
        strings.add("0E8AZTODP403");
        strings.add("0E8AZTODP404");
        strings.add("0E8AZTODP405");
        permission.setLinkedApiIds(strings);
        ResponseEntity<Void> response5 =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid value
        permission.setLinkedApiIds(Collections.singletonList("abc"));
        ResponseEntity<Void> response6 =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //other tenant's id
        permission.setLinkedApiIds(Collections.singletonList(MT_ACCESS_ENDPOINT_ID));
        ResponseEntity<Void> response7 =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response7.getStatusCode());
        //not exist id
        permission.setLinkedApiIds(Collections.singletonList("0E99999999"));
        ResponseEntity<Void> response8 =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
        //too many elements
        List<String> strings2 = new ArrayList<>();
        strings2.add("0E8AZTODP400");
        strings2.add("0E8AZTODP401");
        strings2.add("0E8AZTODP402");
        strings2.add("0E8AZTODP403");
        strings2.add("0E8AZTODP404");
        strings2.add("0E8AZTODP405");
        strings2.add("0E8AZTODP406");
        strings2.add("0E8AZTODP407");
        strings2.add("0E8AZTODP408");
        strings2.add("0E8AZTODP409");
        strings2.add("0E8AZTODP410");
        strings2.add("0E8AZTODP411");
        strings2.add("0E8AZTODP412");
        strings2.add("0E8AZTODP413");
        strings2.add("0E8AZTODP414");
        strings2.add("0E8AZTODP415");
        strings2.add("0E8AZTODP416");
        strings2.add("0E8AZTODP417");
        strings2.add("0E8AZTODP418");
        strings2.add("0E8AZTODP419");
        strings2.add("0E8AZTODP420");
        permission.setLinkedApiIds(strings2);
        ResponseEntity<Void> response9 =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }

    @Test
    public void validation_patch_name() {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(UrlUtility.getId(response));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        //null
        patchCommand.setValue(null);
        ResponseEntity<Void> response1 =
            PermissionUtility.patchTenantPermission(tenantContext, permission, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //blank
        patchCommand.setValue(" ");
        ResponseEntity<Void> response2 =
            PermissionUtility.patchTenantPermission(tenantContext, permission, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        patchCommand.setValue("");
        ResponseEntity<Void> response3 =
            PermissionUtility.patchTenantPermission(tenantContext, permission, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        patchCommand.setValue("012345678901234567890123456789012345678901234567890123456789");
        ResponseEntity<Void> response4 =
            PermissionUtility.patchTenantPermission(tenantContext, permission, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid char
        patchCommand.setValue("<");
        ResponseEntity<Void> response5 =
            PermissionUtility.patchTenantPermission(tenantContext, permission, patchCommand);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

}
