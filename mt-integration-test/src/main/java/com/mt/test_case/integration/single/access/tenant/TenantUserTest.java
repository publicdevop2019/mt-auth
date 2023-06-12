package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.pojo.Role;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.User;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.RoleUtility;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        Assert.assertEquals(HttpStatus.OK, voidResponseEntity.getStatusCode());
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
        Assert.assertEquals(HttpStatus.BAD_REQUEST, voidResponseEntity.getStatusCode());
    }

    @Test
    public void validation_update_user_() {
        //PROJECT_USER cannot remove
        //cannot add roles from other project
    }

    @Test
    public void validation_update_user_role_ids() {
        User user = tenantContext.getUsers().get(0);
        ResponseEntity<User> userResponseEntity = UserUtility.readTenantUser(tenantContext, user);
        User body = userResponseEntity.getBody();
        //null
        body.setRoles(null);
        ResponseEntity<Void> response = UserUtility.updateTenantUser(tenantContext, body);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        body.setRoles(List.of(" "));
        ResponseEntity<Void> response1 = UserUtility.updateTenantUser(tenantContext, body);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        body.setRoles(List.of(""));
        ResponseEntity<Void> response2 = UserUtility.updateTenantUser(tenantContext, body);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty list
        body.setRoles(Collections.emptyList());
        ResponseEntity<Void> response3 = UserUtility.updateTenantUser(tenantContext, body);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //too many elements
        body.setRoles(
            List.of(AppConstant.MT_ACCESS_ROLE_ID, "0Z8HHJ489S00", "0Z8HHJ489S01", "0Z8HHJ489S02",
                "0Z8HHJ489S03", "0Z8HHJ489S04", "0Z8HHJ489S05", "0Z8HHJ489S06", "0Z8HHJ489S07",
                "0Z8HHJ489S08", "0Z8HHJ489S09", "0Z8HHJ489S10", "0Z8HHJ489S11"));
        ResponseEntity<Void> response4 = UserUtility.updateTenantUser(tenantContext, body);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        ArrayList<String> strings = new ArrayList<>();
        strings.add(RandomUtility.randomStringNoNum());
        body.setRoles(strings);
        ResponseEntity<Void> response5 = UserUtility.updateTenantUser(tenantContext, body);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //other tenant's id
        ArrayList<String> strings1 = new ArrayList<>();
        strings1.add(AppConstant.MT_ACCESS_ROLE_ID);
        body.setRoles(strings1);
        ResponseEntity<Void> response6 = UserUtility.updateTenantUser(tenantContext, body);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }
}
