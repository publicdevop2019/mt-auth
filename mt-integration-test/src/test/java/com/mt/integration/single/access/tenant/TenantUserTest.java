package com.mt.integration.single.access.tenant;

import com.mt.helper.AppConstant;
import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.args.UserRoleArgs;
import com.mt.helper.pojo.Role;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.RoleUtility;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.UserUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantUserTest{
    private static TenantContext tenantContext;
    @BeforeAll
    public static void beforeAll() {
        tenantContext = TestHelper.beforeAllTenant(log);
    }
    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }
    @Test
    public void tenant_can_search_user_by_email() {
        ResponseEntity<SumTotal<User>> sumTotalResponseEntity =
            UserUtility.readTenantUsersByQuery(tenantContext,
                "query=emailLike:" + tenantContext.getUsers().get(0).getEmail());
        Assertions.assertEquals(HttpStatus.OK, sumTotalResponseEntity.getStatusCode());
        Assertions.assertNotSame(0, sumTotalResponseEntity.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_users() {
        //get user list
        ResponseEntity<SumTotal<User>> sumTotalResponseEntity =
            UserUtility.readTenantUsers(tenantContext);
        Assertions.assertNotSame(0, sumTotalResponseEntity.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_user_detail() {
        ResponseEntity<User> userResponseEntity =
            UserUtility.readTenantUser(tenantContext, tenantContext.getUsers().get(1));
        Assertions.assertNotSame(0, userResponseEntity.getBody().getRoles().size());
    }

    @Test
    public void tenant_cannot_find_user_not_using_project() {
        ResponseEntity<SumTotal<User>> sumTotalResponseEntity =
            UserUtility.readTenantUsersByQuery(tenantContext,
                "query=emailLike:" + AppConstant.ACCOUNT_EMAIL_MALL_ADMIN);
        Assertions.assertEquals(HttpStatus.OK, sumTotalResponseEntity.getStatusCode());
        Assertions.assertEquals(0, sumTotalResponseEntity.getBody().getData().size());
    }

    @Test
    public void tenant_can_assign_role_to_user() {
        //create role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(tenantRole));
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
    }

    @Test
    public void tenant_cannot_add_invalid_role_to_user() {
        //read user
        User user = tenantContext.getUsers().get(0);
        ResponseEntity<User> userResponseEntity = UserUtility.readTenantUser(tenantContext, user);
        User body = userResponseEntity.getBody();
        //assign role
        ArrayList<String> strings = new ArrayList<>();
        strings.add(RandomUtility.randomStringNoNum());
        body.setRoles(strings);
        ResponseEntity<Void> voidResponseEntity = UserUtility.updateTenantUser(tenantContext, body);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
    }

    @ParameterizedTest
    @ArgumentsSource(UserRoleArgs.class)
    public void validation_update_user_role_ids(List<String> roles, HttpStatus httpStatus) {
        User user = tenantContext.getUsers().get(0);
        ResponseEntity<User> userResponseEntity = UserUtility.readTenantUser(tenantContext, user);
        User body = userResponseEntity.getBody();
        Objects.requireNonNull(body).setRoles(roles);
        ResponseEntity<Void> response = UserUtility.updateTenantUser(tenantContext, body);
        Assertions.assertEquals(httpStatus, response.getStatusCode());
    }
}
