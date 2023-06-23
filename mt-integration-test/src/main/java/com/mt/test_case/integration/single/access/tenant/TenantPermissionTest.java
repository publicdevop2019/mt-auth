package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.args.LinkedApiIdArgs;
import com.mt.test_case.helper.args.NameArgs;
import com.mt.test_case.helper.args.PermissionParentIdArgs;
import com.mt.test_case.helper.args.ProjectIdArgs;
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
import java.util.Collections;
import java.util.List;
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
    public void validation_create_valid() {
        Permission randomPermissionObj = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, randomPermissionObj);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_create_name(String name, HttpStatus httpStatus) {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        permission.setName(name);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(PermissionParentIdArgs.class)
    public void validation_create_parent_id(String name, HttpStatus httpStatus) {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        permission.setParentId(name);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_create_project_id(String id, HttpStatus httpStatus) {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        Project project1 = new Project();
        project1.setId(id);
        String url = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, permission);
        Assertions.assertEquals(httpStatus, resource.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(LinkedApiIdArgs.class)
    public void validation_create_linked_api_ids(List<String> ids, HttpStatus status) {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        permission.setLinkedApiIds(ids);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(status, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_update_name(String name, HttpStatus httpStatus) {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(UrlUtility.getId(response));
        permission.setName(name);
        ResponseEntity<Void> response1 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_update_project_id(String id, HttpStatus httpStatus) {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(UrlUtility.getId(response));
        Project project1 = new Project();
        project1.setId(id);
        String url = PermissionUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.updateResource(tenantContext.getCreator(), url, permission, permission.getId());
        Assertions.assertEquals(httpStatus, resource.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(LinkedApiIdArgs.class)
    public void validation_update_linked_api_ids(List<String> ids, HttpStatus status) {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(UrlUtility.getId(response));
        permission.setLinkedApiIds(ids);
        ResponseEntity<Void> response1 =
            PermissionUtility.updateTenantPermission(tenantContext, permission);
        Assertions.assertEquals(status, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_patch_name(String name, HttpStatus httpStatus) {
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(UrlUtility.getId(response));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        patchCommand.setValue(name);
        ResponseEntity<Void> response1 =
            PermissionUtility.patchTenantPermission(tenantContext, permission, patchCommand);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

}
