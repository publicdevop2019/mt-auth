package com.mt.integration.single.access.tenant;

import com.mt.helper.AppConstant;
import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.AssignRoleReq;
import com.mt.helper.pojo.Role;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.RoleUtility;
import com.mt.helper.utility.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantUserTest {
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
                "query=email:" + tenantContext.getUsers().get(0).getEmail());
        Assertions.assertEquals(HttpStatus.OK, sumTotalResponseEntity.getStatusCode());
        Assertions.assertNotSame(0, sumTotalResponseEntity.getBody().getData().size());
    }

    @Test
    public void tenant_can_search_user_by_mobile() {
        User user = UserUtility.userMobileCodeLoginToTenant(tenantContext.getProject(),
            tenantContext.getLoginClient());
        String mobileNumber = user.getMobileNumber();
        ResponseEntity<SumTotal<User>> sumTotalResponseEntity =
            UserUtility.readTenantUsersByQuery(tenantContext,
                "query=mobile:" + mobileNumber);
        Assertions.assertEquals(HttpStatus.OK, sumTotalResponseEntity.getStatusCode());
        Assertions.assertNotSame(0, sumTotalResponseEntity.getBody().getData().size());
    }

    @Test
    public void tenant_can_search_user_by_username() {
        User user = UserUtility.userUsernamePwdLoginToTenant(tenantContext.getProject(),
            tenantContext.getLoginClient());
        String username = user.getUsername();
        ResponseEntity<SumTotal<User>> sumTotalResponseEntity =
            UserUtility.readTenantUsersByQuery(tenantContext,
                "query=username:" + username);
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
        Assertions.assertNotSame(0, userResponseEntity.getBody().getRoleDetails().size());
    }

    @Test
    public void tenant_cannot_find_user_not_using_project() {
        ResponseEntity<SumTotal<User>> sumTotalResponseEntity =
            UserUtility.readTenantUsersByQuery(tenantContext,
                "query=email:" + AppConstant.ACCOUNT_EMAIL_MALL_ADMIN);
        Assertions.assertEquals(HttpStatus.OK, sumTotalResponseEntity.getStatusCode());
        Assertions.assertEquals(0, sumTotalResponseEntity.getBody().getData().size());
    }

    @Test
    public void tenant_cannot_add_invalid_role_to_user() {
        //read user
        User user = tenantContext.getUsers().get(0);
        //assign role
        AssignRoleReq assignRoleReq = new AssignRoleReq();
        assignRoleReq.getRoleIds().add(RandomUtility.randomStringNoNum());
        ResponseEntity<Void> voidResponseEntity =
            UserUtility.assignTenantUserRole(tenantContext, user, assignRoleReq);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_can_assigned_role() {
        //create role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(tenantRole));
        //read user
        User user = tenantContext.getUsers().get(0);
        AssignRoleReq assignRoleReq = new AssignRoleReq();
        assignRoleReq.getRoleIds().add(role.getId());
        //assign role
        ResponseEntity<Void> response =
            UserUtility.assignTenantUserRole(tenantContext, user, assignRoleReq);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<User> userResponseEntity =
            UserUtility.readTenantUser(tenantContext, user);
        Assertions.assertSame(2, userResponseEntity.getBody().getRoleDetails().size());
    }

    @Test
    public void tenant_can_remove_role() {
        //create role
        Role role = RoleUtility.createRandomValidRoleObj();
        ResponseEntity<Void> tenantRole =
            RoleUtility.createTenantRole(tenantContext, role);
        role.setId(HttpUtility.getId(tenantRole));
        //read user
        User user = tenantContext.getUsers().get(0);
        AssignRoleReq assignRoleReq = new AssignRoleReq();
        assignRoleReq.getRoleIds().add(role.getId());
        //assign role
        ResponseEntity<Void> response =
            UserUtility.assignTenantUserRole(tenantContext, user, assignRoleReq);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        //remove role
        ResponseEntity<Void> response2 =
            UserUtility.removeTenantUserRole(tenantContext, user, role.getId());
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        //read again
        ResponseEntity<User> userResponseEntity =
            UserUtility.readTenantUser(tenantContext, user);
        Assertions.assertNotSame(1, userResponseEntity.getBody().getRoleDetails().size());
    }
}
