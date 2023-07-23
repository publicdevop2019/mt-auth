package com.mt.integration.single.access.validation;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.args.LinkedApiIdArgs;
import com.mt.helper.args.NameArgs;
import com.mt.helper.args.PermissionParentIdArgs;
import com.mt.helper.args.ProjectIdArgs;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Permission;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.Role;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.PermissionUtility;
import com.mt.helper.utility.RoleUtility;
import com.mt.helper.utility.TenantUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UrlUtility;
import com.mt.helper.utility.Utility;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@Tag("validation")

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantPermissionValidationTest {
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
