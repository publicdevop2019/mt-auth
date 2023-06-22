package com.mt.test_case.integration.single.access.tenant;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantTest;
import com.mt.test_case.helper.pojo.ProjectAdmin;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.User;
import com.mt.test_case.helper.utility.AdminUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantAdminTest extends TenantTest {

    @Test
    public void tenant_can_view_admin() {
        ResponseEntity<SumTotal<ProjectAdmin>> exchange =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        Assert.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).getData().size());

    }

    @Test
    public void tenant_can_add_admin() {
        //create new tenant user
        User tenantUser = UserUtility.userLoginToTenant(tenantContext.getProject(),
            tenantContext.getLoginClientId());
        //record before add
        ResponseEntity<SumTotal<ProjectAdmin>> exchange3 =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        Integer previousCount = Objects.requireNonNull(exchange3.getBody()).getTotalItemCount();
        //add admin
        ResponseEntity<Void> exchange2 =
            AdminUtility.makeAdmin(tenantContext.getCreator(), tenantContext.getProject(),
                tenantUser);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        //record after add
        ResponseEntity<SumTotal<ProjectAdmin>> exchange4 =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        Integer currentCount = Objects.requireNonNull(exchange4.getBody()).getTotalItemCount();
        Assert.assertNotEquals(currentCount, previousCount);
    }

    @Test
    public void tenant_can_remove_admin() {
        //create new tenant user
        User tenantUser = UserUtility.userLoginToTenant(tenantContext.getProject(),
            tenantContext.getLoginClientId());
        //record before remove
        ResponseEntity<SumTotal<ProjectAdmin>> exchange3 =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        Integer previousCount = Objects.requireNonNull(exchange3.getBody()).getTotalItemCount();
        //add admin
        ResponseEntity<Void> exchange2 =
            AdminUtility.makeAdmin(tenantContext.getCreator(), tenantContext.getProject(),
                tenantUser);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        //remove admin
        ResponseEntity<Void> exchange5 =
            AdminUtility.removeAdmin(tenantContext.getCreator(), tenantContext.getProject(),
                tenantUser);
        Assert.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
        //record after remove
        ResponseEntity<SumTotal<ProjectAdmin>> exchange4 =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        Integer currentCount = Objects.requireNonNull(exchange4.getBody()).getTotalItemCount();
        Assert.assertEquals(currentCount, previousCount);
    }

    @Test
    public void tenant_cannot_add_user_not_using_project_as_admin() {
        //create new user but not login to created project
        User userObj = UserUtility.createUser();
        //add admin
        ResponseEntity<Void> exchange2 =
            AdminUtility.makeAdmin(tenantContext.getCreator(), tenantContext.getProject(), userObj);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
    }

    @Test
    public void admin_validation_should_work() {
        //1. add admin with random string
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins/" + RandomUtility.randomStringWithNum())),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //2. try to remove admin when total admin equal to 2
        //check admin count
        ResponseEntity<SumTotal<ProjectAdmin>> exchange =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        SumTotal<ProjectAdmin> body = exchange.getBody();
        Integer adminCount = body.getTotalItemCount();
        int numOfAdminToRemove = adminCount - 2;
        List<ProjectAdmin> otherAdmins = body.getData().stream()
            .filter(e -> !e.getEmail().equalsIgnoreCase(tenantContext.getCreator().getEmail()))
            .collect(
                Collectors.toList());
        log.info("num of admin to remove is {}", numOfAdminToRemove);
        IntStream.range(0, numOfAdminToRemove).forEach((in) -> {
            log.info("removing admin {}", otherAdmins.get(in).toUser().getEmail());
            ResponseEntity<Void> exchange5 =
                AdminUtility.removeAdmin(tenantContext.getCreator(), tenantContext.getProject(),
                    otherAdmins.get(in).toUser());
            Assert.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
        });
        int index;
        if (numOfAdminToRemove == 0) {
            index = 0;
        } else {
            index = otherAdmins.size() - 1;
        }
        //remove admin
        log.info("removing admin {}", otherAdmins.get(index).toUser().getEmail());
        ResponseEntity<Void> exchange5 =
            AdminUtility.removeAdmin(tenantContext.getCreator(), tenantContext.getProject(),
                otherAdmins.get(index).toUser());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
    }
}
