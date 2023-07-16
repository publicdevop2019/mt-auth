package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.args.DescriptionArgs;
import com.mt.test_case.helper.args.NameArgs;
import com.mt.test_case.helper.args.ProjectIdArgs;
import com.mt.test_case.helper.args.RoleParentIdArgs;
import com.mt.test_case.helper.args.RolePermissionIdsArgs;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Endpoint;
import com.mt.test_case.helper.pojo.PatchCommand;
import com.mt.test_case.helper.pojo.Permission;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.Role;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.UpdateType;
import com.mt.test_case.helper.pojo.User;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.EndpointUtility;
import com.mt.test_case.helper.utility.PermissionUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.RoleUtility;
import com.mt.test_case.helper.utility.TenantUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
import com.mt.test_case.helper.utility.Utility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
public class TenantRoleTest {
    private static TenantContext tenantContext;
    private static Role rootRole;
    private static Endpoint sharedEndpointObj;
    private static Client client;

    static Stream<Arguments> commonDescriptionArgs() {
        return Stream.of(
            Arguments.of(null, HttpStatus.OK),
            Arguments.of("", HttpStatus.BAD_REQUEST),
            Arguments.of("  ", HttpStatus.BAD_REQUEST),
            Arguments.of("<", HttpStatus.BAD_REQUEST),
            Arguments.of("012345678901234567890123456789012345678901234567890123456789" +
                    "012345678901234567890123456789012345678901234567890123456789"
                , HttpStatus.BAD_REQUEST)
        );
    }

    @BeforeAll
    public static void initTenant() {
        log.info("init tenant in progress");
        TestContext.init();
        tenantContext = TenantUtility.initTenant();
        //create root node
        rootRole = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole = RoleUtility.createTenantRole(tenantContext, rootRole);
        rootRole.setId(UrlUtility.getId(tenantRole));
        log.info("init tenant complete");

        //create client for endpoint
        client = ClientUtility.createRandomSharedBackendClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, client);
        String clientId = UrlUtility.getId(tenantClient);
        client.setId(clientId);
        //create shared endpoint
        sharedEndpointObj = EndpointUtility.createValidSharedEndpointObj(clientId);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext, sharedEndpointObj);
        sharedEndpointObj.setId(UrlUtility.getId(tenantEndpoint2));
    }

    @Test
    public void tenant_can_create_role() {
        Role randomRoleObj = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, randomRoleObj);
        Assertions.assertEquals(HttpStatus.OK, tenantRole.getStatusCode());
        Assertions.assertNotNull(UrlUtility.getId(tenantRole));
    }

    @Test
    public void tenant_can_view_role_list() {
        ResponseEntity<SumTotal<Role>> sumTotalResponseEntity =
            RoleUtility.readTenantRole(tenantContext);
        Assertions.assertEquals(HttpStatus.OK, sumTotalResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_view_role_tree() {
        //query root node
        ResponseEntity<SumTotal<Role>> response =
            RoleUtility.readTenantRoleWithQuery(tenantContext,
                "query=parentId:null,types:USER.PROJECT");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotSame(0, response.getBody().getData().size());
        //query by parent id
        ResponseEntity<SumTotal<Role>> sumTotalResponseEntity2 =
            RoleUtility.readTenantRoleWithQuery(tenantContext,
                "query=parentId:" + rootRole.getId());
        Assertions.assertEquals(HttpStatus.OK, sumTotalResponseEntity2.getStatusCode());
    }

    @Test
    public void tenant_can_view_role_detail() {
        ResponseEntity<Role> response =
            RoleUtility.readTenantRoleById(tenantContext,
                rootRole);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody().getName());
    }

    @Test
    public void tenant_can_delete_role() {
        Role randomRoleObj = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, randomRoleObj);
        randomRoleObj.setId(UrlUtility.getId(tenantRole));
        ResponseEntity<Void> voidResponseEntity = RoleUtility.deleteTenantRole(tenantContext,
            randomRoleObj);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_delete_assigned_role() {
        //create role
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(tenantRole));
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
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
        //delete role
        ResponseEntity<Void> voidResponseEntity2 = RoleUtility.deleteTenantRole(tenantContext,
            role);
        Assertions.assertEquals(HttpStatus.OK, voidResponseEntity2.getStatusCode());
    }

    @Test
    public void tenant_can_update_role_detail() {
        //create role
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(tenantRole));
        //update it's permission
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(UrlUtility.getId(response));
        role.setCommonPermissionIds(Collections.singleton(permission.getId()));
        role.setType(UpdateType.COMMON_PERMISSION.name());
        ResponseEntity<Void> response2 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        //read again
        ResponseEntity<Role> roleResponseEntity =
            RoleUtility.readTenantRoleById(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, roleResponseEntity.getStatusCode());
        Assertions.assertEquals(1, roleResponseEntity.getBody().getCommonPermissionIds().size());
        //update it's api
        //find root
        ResponseEntity<SumTotal<Permission>> sumTotalResponseEntity =
            PermissionUtility.readTenantPermissionWithQuery(tenantContext,
                "query=parentId:null,types:API");
        String permissionId = sumTotalResponseEntity.getBody().getData().get(0).getId();
        //find api permissions
        ResponseEntity<SumTotal<Permission>> sumTotalResponseEntity2 =
            PermissionUtility.readTenantPermissionWithQuery(tenantContext,
                "query=parentId:" + permissionId + ",types:API");
        String permissionId2 = sumTotalResponseEntity2.getBody().getData().get(0).getId();
        role.setApiPermissionIds(Collections.singleton(permissionId2));
        role.setType(UpdateType.API_PERMISSION.name());
        ResponseEntity<Void> response4 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, response4.getStatusCode());
        //read again
        ResponseEntity<Role> roleResponseEntity2 =
            RoleUtility.readTenantRoleById(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, roleResponseEntity2.getStatusCode());
        Assertions.assertEquals(1, roleResponseEntity2.getBody().getApiPermissionIds().size());
        //update basic info
        role.setName(RandomUtility.randomStringWithNum());
        role.setType(UpdateType.BASIC.name());
        ResponseEntity<Void> response3 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
        //read again
        ResponseEntity<Role> roleResponseEntity3 =
            RoleUtility.readTenantRoleById(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, roleResponseEntity3.getStatusCode());
        Assertions.assertEquals(role.getName(), roleResponseEntity3.getBody().getName());
    }

    @Test
    public void tenant_role_should_not_contain_deleted_permissions() throws InterruptedException {
        //create role
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(tenantRole));
        //update it's permission
        Permission permission = PermissionUtility.createRandomPermissionObj();
        ResponseEntity<Void> response =
            PermissionUtility.createTenantPermission(tenantContext, permission);
        permission.setId(UrlUtility.getId(response));
        role.setCommonPermissionIds(Collections.singleton(permission.getId()));
        role.setType(UpdateType.COMMON_PERMISSION.name());
        RoleUtility.updateTenantRole(tenantContext, role);
        //delete permission
        PermissionUtility.deleteTenantPermission(tenantContext, permission);
        //wait
        Thread.sleep(5 * 1000);
        //read again
        ResponseEntity<Role> roleResponseEntity =
            RoleUtility.readTenantRoleById(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, roleResponseEntity.getStatusCode());
        Assertions.assertEquals(0, roleResponseEntity.getBody().getCommonPermissionIds().size());
    }

    @Test
    public void tenant_role_should_not_contain_deleted_protected_endpoints()
        throws InterruptedException {
        //create none-shared endpoint
        Endpoint ep =
            EndpointUtility.createValidSharedEndpointObj(client.getId());
        ep.setShared(false);
        ResponseEntity<Void> tenantEndpoint2 =
            EndpointUtility.createTenantEndpoint(tenantContext, ep);
        ep.setId(UrlUtility.getId(tenantEndpoint2));
        //wait for permission to create
        Thread.sleep(5 * 1000);
        //create role
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(tenantRole));
        //update it's api
        //find root
        ResponseEntity<SumTotal<Permission>> sumTotalResponseEntity =
            PermissionUtility.readTenantPermissionWithQuery(tenantContext,
                "query=parentId:null,types:API");
        String permissionId = sumTotalResponseEntity.getBody().getData().get(0).getId();
        //find api permissions
        ResponseEntity<SumTotal<Permission>> sumTotalResponseEntity2 =
            PermissionUtility.readTenantPermissionWithQuery(tenantContext,
                "query=parentId:" + permissionId + ",types:API");
        String permissionId2 = sumTotalResponseEntity2.getBody().getData().stream()
            .filter(e -> e.getName().equals(ep.getName())).findFirst().get().getId();
        role.setApiPermissionIds(Collections.singleton(permissionId2));
        role.setType(UpdateType.API_PERMISSION.name());
        RoleUtility.updateTenantRole(tenantContext, role);
        //read again
        ResponseEntity<Role> roleResponseEntity2 =
            RoleUtility.readTenantRoleById(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, roleResponseEntity2.getStatusCode());
        Assertions.assertEquals(1, roleResponseEntity2.getBody().getApiPermissionIds().size());
        //delete endpoint
        EndpointUtility.deleteTenantEndpoint(tenantContext, ep);
        //wait for role clean up
        Thread.sleep(10 * 1000);
        //read again
        ResponseEntity<Role> roleResponseEntity =
            RoleUtility.readTenantRoleById(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, roleResponseEntity.getStatusCode());
        Assertions.assertEquals(0, roleResponseEntity.getBody().getApiPermissionIds().size());
    }

    @Test
    public void validation_create_valid() {
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_create_name(String name, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomRoleObj();
        role.setName(name);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_create_description(String description, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomRoleObj();
        role.setDescription(description);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RoleParentIdArgs.class)
    public void validation_create_parent_id(String parentId, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomRoleObj();
        role.setParentId(parentId);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_create_project_id(String projectId, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomRoleObj();
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
        Role role = RoleUtility.createRandomRoleObj();
        role.setApiPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RolePermissionIdsArgs.class)
    public void validation_create_common_permission_ids(Set<String> ids, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomRoleObj();
        role.setCommonPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }


    @ParameterizedTest
    @ArgumentsSource(RolePermissionIdsArgs.class)
    public void validation_create_external_permission_ids(Set<String> ids, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomRoleObj();
        role.setExternalPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.createTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_update_description(String description, HttpStatus httpStatus) {
        //create role
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(response));
        role.setType("BASIC");
        role.setDescription(description);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @Test
    public void validation_update_type() {
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(response));
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
        permission.setId(UrlUtility.getId(response4));
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
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(response));
        role.setType("BASIC");
        role.setName(name);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RoleParentIdArgs.class)
    public void validation_update_parent_id(String parentId, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(response));
        role.setType("BASIC");
        role.setParentId(parentId);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectIdArgs.class)
    public void validation_update_project_id(String projectId, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(response));
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
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(response));
        role.setType("API_PERMISSION");
        role.setApiPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RolePermissionIdsArgs.class)
    public void validation_update_common_permission_ids(Set<String> ids, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(response));
        role.setType("COMMON_PERMISSION");
        role.setCommonPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RolePermissionIdsArgs.class)
    public void validation_update_external_permission_ids(Set<String> ids, HttpStatus httpStatus) {
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(response));
        role.setType("API_PERMISSION");
        role.setExternalPermissionIds(ids);
        ResponseEntity<Void> response1 =
            RoleUtility.updateTenantRole(tenantContext, role);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(NameArgs.class)
    public void validation_patch_name(String name, HttpStatus httpStatus) {
        //create role
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(response));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/name");
        patchCommand.setValue(name);
        ResponseEntity<Void> response1 =
            RoleUtility.patchTenantRole(tenantContext, role, patchCommand);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DescriptionArgs.class)
    public void validation_patch_description(String description, HttpStatus httpStatus) {
        //create role
        Role role = RoleUtility.createRandomRoleObj();
        ResponseEntity<Void> response =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(UrlUtility.getId(response));
        PatchCommand patchCommand = new PatchCommand();
        patchCommand.setOp("replace");
        patchCommand.setPath("/description");
        patchCommand.setValue(description);
        ResponseEntity<Void> response1 =
            RoleUtility.patchTenantRole(tenantContext, role, patchCommand);
        Assertions.assertEquals(httpStatus, response1.getStatusCode());
    }
}
