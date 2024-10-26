package com.mt.integration.single.access.tenant;

import com.mt.helper.AppConstant;
import com.mt.helper.TenantContext;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.ProjectAdmin;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.AdminUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.UserUtility;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class TenantAdminTest{
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
    public void tenant_can_view_admin() {
        ResponseEntity<SumTotal<ProjectAdmin>> exchange =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        Assertions.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).getData().size());

    }

    @Test
    public void tenant_can_add_admin() {
        //create new tenant user
        User tenantUser = UserUtility.userLoginToTenant(tenantContext.getProject(),
            tenantContext.getLoginClient());
        //record before add
        ResponseEntity<SumTotal<ProjectAdmin>> exchange3 =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        Integer previousCount = Objects.requireNonNull(exchange3.getBody()).getTotalItemCount();
        //add admin
        ResponseEntity<Void> exchange2 =
            AdminUtility.makeAdmin(tenantContext.getCreator(), tenantContext.getProject(),
                tenantUser);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        //record after add
        ResponseEntity<SumTotal<ProjectAdmin>> exchange4 =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        Integer currentCount = Objects.requireNonNull(exchange4.getBody()).getTotalItemCount();
        Assertions.assertNotEquals(currentCount, previousCount);
    }

    @Test
    public void tenant_can_remove_admin() {
        //create new tenant user
        User tenantUser = UserUtility.userLoginToTenant(tenantContext.getProject(),
            tenantContext.getLoginClient());
        //record before remove
        ResponseEntity<SumTotal<ProjectAdmin>> exchange3 =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        Integer previousCount = Objects.requireNonNull(exchange3.getBody()).getTotalItemCount();
        //add admin
        ResponseEntity<Void> exchange2 =
            AdminUtility.makeAdmin(tenantContext.getCreator(), tenantContext.getProject(),
                tenantUser);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        //remove admin
        ResponseEntity<Void> exchange5 =
            AdminUtility.removeAdmin(tenantContext.getCreator(), tenantContext.getProject(),
                tenantUser);
        Assertions.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
        //record after remove
        ResponseEntity<SumTotal<ProjectAdmin>> exchange4 =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject());
        Integer currentCount = Objects.requireNonNull(exchange4.getBody()).getTotalItemCount();
        Assertions.assertEquals(currentCount, previousCount);
    }

    @Test
    public void tenant_cannot_add_user_not_using_project_as_admin() {
        //create new user but not login to created project
        User userObj = UserUtility.createEmailPwdUser();
        //add admin
        ResponseEntity<Void> exchange2 =
            AdminUtility.makeAdmin(tenantContext.getCreator(), tenantContext.getProject(), userObj);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
    }

    @Test
    public void admin_validation_should_work() {
        //1. add admin with random string
        String login =
            UserUtility.emailPwdLogin(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(
                    HttpUtility.combinePath(
                        AppConstant.TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins/" + RandomUtility.randomStringWithNum())),
                HttpMethod.POST, request,
                Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //2. try to remove admin when total admin equal to 2
        //check admin count
        SumTotal<ProjectAdmin> allAdmins =
            AdminUtility.readAdmin(tenantContext.getCreator(), tenantContext.getProject()).getBody();
        Integer adminCount = allAdmins.getTotalItemCount();
        int numOfAdminToRemove = adminCount - 2;
        List<ProjectAdmin> otherAdmins = allAdmins.getData().stream()
            .filter(e -> !e.getId().equalsIgnoreCase(tenantContext.getCreator().getId()))
            .collect(
                Collectors.toList());
        log.info("num of admin to remove is {}", numOfAdminToRemove);
        IntStream.range(0, numOfAdminToRemove).forEach((in) -> {
            log.info("removing admin {}", otherAdmins.get(in).toUser().getEmail());
            ResponseEntity<Void> exchange5 =
                AdminUtility.removeAdmin(tenantContext.getCreator(), tenantContext.getProject(),
                    otherAdmins.get(in).toUser());
            Assertions.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
    }
}
