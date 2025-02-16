package com.mt.integration.single.access.tenant.validation;

import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.args.DescriptionArgs;
import com.mt.helper.args.NameArgs;
import com.mt.helper.args.ProjectIdArgs;
import com.mt.helper.args.RoleParentIdArgs;
import com.mt.helper.args.RolePermissionIdsArgs;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.Endpoint;
import com.mt.helper.pojo.Permission;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.Role;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.EndpointUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.PermissionUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.RoleUtility;
import com.mt.helper.utility.TenantUtility;
import com.mt.helper.utility.Utility;
import java.util.Collections;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Tag("validation")

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantRoleValidationTest {
    private static TenantContext tenantContext;
    private static Role rootRole;
    private static Endpoint sharedEndpointObj;
    private static Client client;

    @BeforeAll
    public static void initTenant() {
        TestHelper.beforeAll(log);
        log.info("init tenant in progress");
        tenantContext = TenantUtility.initTenant();
        //create root node
        rootRole = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole = RoleUtility.createTenantRole(tenantContext, rootRole);
        rootRole.setId(HttpUtility.getId(tenantRole));
        log.info("init tenant complete");

        //create client for endpoint
        client = ClientUtility.createRandomSharedBackendClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        String clientId = HttpUtility.getId(tenantClient);
        client.setId(clientId);
        //create shared endpoint
        sharedEndpointObj = EndpointUtility.createValidSharedEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext, sharedEndpointObj);
        sharedEndpointObj.setId(HttpUtility.getId(tenantEndpoint2));
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void validation_create_valid() {
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_create_name(String name, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        role.setName(name);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_create_description(String description, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        role.setDescription(description);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RoleParentIdArgs.class)
    public void validation_create_parent_id(String parentId, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        role.setParentId(parentId);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_create_project_id(String projectId, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        Project project1 = new Project();
        project1.setId(projectId);
        String url = RoleUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.createResource(tenantContext.getCreator(), url, role);
        Assertions.assertEquals(httpStatus, resource.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(RolePermissionIdsArgs.class)
    public void validation_create_api_permission_ids(Set<String> ids, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        role.setApiPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RolePermissionIdsArgs.class)
    public void validation_create_common_permission_ids(Set<String> ids, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        role.setCommonPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(RolePermissionIdsArgs.class)
    public void validation_create_external_permission_ids(Set<String> ids, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        role.setExternalPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_update_description(String description, HttpStatus httpStatus) {
        //create role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(response));
        role.setType("BASIC");
        role.setDescription(description);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @Test
    public void validation_update_type() {
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(response));
        //null
        role.setType(null);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //invalid value
        role.setType(RandomUtility.randomStringNoNum());
        ResponseEntity<Void> response2 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //basic but update permission , then permission will not update
        role.setType("BASIC");
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response4 =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(HttpUtility.getId(response4));
        role.setCommonPermissionIds(Collections.singleton(permission.getId()));
        ResponseEntity<Void> response3 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
        ResponseEntity<Role> roleResponseEntity =
            RoleUtility.readTenantRoleById(tenantContext, role);
        Assertions.assertEquals(0, roleResponseEntity.getBody().getCommonPermissionIds().size());

    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_update_name(String name, HttpStatus httpStatus) {
        //create role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(response));
        role.setType("BASIC");
        role.setName(name);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RoleParentIdArgs.class)
    public void validation_update_parent_id(String parentId, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(response));
        role.setType("BASIC");
        role.setParentId(parentId);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_update_project_id(String projectId, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(response));
        role.setType("BASIC");
        Project project1 = new Project();
        project1.setId(projectId);
        String url = RoleUtility.getUrl(project1);
        ResponseEntity<Void> resource =
            Utility.updateResource(tenantContext.getCreator(), url, role, role.getId());
        Assertions.assertEquals(httpStatus, resource.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RolePermissionIdsArgs.class)
    public void validation_update_api_permission_ids(Set<String> ids, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(response));
        role.setType("API_PERMISSION");
        role.setApiPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RolePermissionIdsArgs.class)
    public void validation_update_common_permission_ids(Set<String> ids, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(response));
        role.setType("COMMON_PERMISSION");
        role.setCommonPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RolePermissionIdsArgs.class)
    public void validation_update_external_permission_ids(Set<String> ids, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(response));
        role.setType("API_PERMISSION");
        role.setExternalPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }
}
