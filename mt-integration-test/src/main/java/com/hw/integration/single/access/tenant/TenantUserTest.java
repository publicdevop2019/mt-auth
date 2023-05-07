package com.hw.integration.single.access.tenant;

import com.hw.helper.AppConstant;
import com.hw.helper.Role;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import com.hw.helper.utility.RandomUtility;
import com.hw.helper.utility.RoleUtility;
import com.hw.helper.utility.UserUtility;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantUserTest extends TenantTest {

    @Test
    public void tenant_can_search_user_by_email() {
        ResponseEntity<SumTotal<User>> sumTotalResponseEntity =
            UserUtility.readTenantUsersByQuery(tenantContext,
                "query=emailLike:" + tenantContext.getUsers().get(0).getEmail());
        Assert.assertEquals(HttpStatus.OK, sumTotalResponseEntity.getStatusCode());
        Assert.assertNotSame(0, sumTotalResponseEntity.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_users() {
        //get user list
        ResponseEntity<SumTotal<User>> sumTotalResponseEntity =
            UserUtility.readTenantUsers(tenantContext);
        Assert.assertNotSame(0, sumTotalResponseEntity.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_user_detail() {
        ResponseEntity<User> userResponseEntity =
            UserUtility.readTenantUser(tenantContext, tenantContext.getUsers().get(1));
        Assert.assertNotSame(0, userResponseEntity.getBody().getRoles().size());
    }

    @Test
    public void tenant_cannot_find_user_not_using_project() {
        ResponseEntity<SumTotal<User>> sumTotalResponseEntity =
            UserUtility.readTenantUsersByQuery(tenantContext,
                "query=emailLike:" + AppConstant.ACCOUNT_USERNAME_MALL_ADMIN);
        Assert.assertEquals(HttpStatus.OK, sumTotalResponseEntity.getStatusCode());
        Assert.assertEquals(0, sumTotalResponseEntity.getBody().getData().size());
    }

    @Test
    public void tenant_can_assign_role_to_user() {
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
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_cannot_add_invalid_role_to_user() {
        //read user
        User user = tenantContext.getUsers().get(0);
        ResponseEntity<User> userResponseEntity = UserUtility.readTenantUser(tenantContext, user);
        User body = userResponseEntity.getBody();
        ArrayList<String> strings = new ArrayList<>();
        strings.add(RandomUtility.randomStringNoNum());
        body.setRoles(strings);
        //assign role
        ResponseEntity<Void> voidResponseEntity = UserUtility.updateTenantUser(tenantContext, body);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
    }

    @Test
    public void tenant_validation_should_work() {
        //PROJECT_USER cannot remove
        //cannot add roles from other project
    }
}
