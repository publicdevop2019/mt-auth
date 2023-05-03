package com.hw.integration.single.access.tenant;

import static com.hw.helper.AppConstant.TENANT_PROJECTS_PREFIX;

import com.hw.helper.ProjectAdmin;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import com.hw.helper.utility.RandomUtility;
import com.hw.helper.utility.TenantUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import com.hw.integration.single.access.CommonTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.ParameterizedTypeReference;
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
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<ProjectAdmin>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).getData().size());

    }

    @Test
    public void tenant_can_add_admin() {
        //search user
        String login2 =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login2);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<ProjectAdmin>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/users")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        List<ProjectAdmin> collect = Objects.requireNonNull(exchange.getBody()).getData().stream()
            .filter(e -> !e.getEmail().equalsIgnoreCase(tenantContext.getCreator().getEmail()))
            .collect(
                Collectors.toList());
        Assert.assertNotSame(0, collect.size());
        String userId = collect.get(0).getId();
        //record before add
        ResponseEntity<SumTotal<ProjectAdmin>> exchange3 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Integer previousCount = Objects.requireNonNull(exchange3.getBody()).getTotalItemCount();
        //add admin
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins/" + userId)),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        //record after add
        ResponseEntity<SumTotal<ProjectAdmin>> exchange4 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Integer currentCount = Objects.requireNonNull(exchange4.getBody()).getTotalItemCount();
        Assert.assertNotEquals(currentCount, previousCount);
    }

    @Test
    public void tenant_can_remove_admin() {
        //search user
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<ProjectAdmin>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/users")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        List<ProjectAdmin> collect = Objects.requireNonNull(exchange.getBody()).getData().stream()
            .filter(e -> !e.getEmail().equalsIgnoreCase(tenantContext.getCreator().getEmail()))
            .collect(
                Collectors.toList());
        Assert.assertNotSame(1, collect.size());
        String userId = collect.get(0).getId();
        String userId2 = collect.get(1).getId();
        //add one admin so 2 admin present
        ResponseEntity<Void> exchange6 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins/" + userId2)),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange6.getStatusCode());
        //record before remove
        ResponseEntity<SumTotal<ProjectAdmin>> exchange3 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Integer previousCount = Objects.requireNonNull(exchange3.getBody()).getTotalItemCount();
        //add admin
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins/" + userId)),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        //remove admin
        ResponseEntity<Void> exchange5 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins/" + userId)),
                HttpMethod.DELETE, request,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange5.getStatusCode());
        //record after add
        ResponseEntity<SumTotal<ProjectAdmin>> exchange4 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Integer currentCount = Objects.requireNonNull(exchange4.getBody()).getTotalItemCount();
        Assert.assertEquals(currentCount, previousCount);
    }

    @Test
    public void tenant_cannot_add_user_not_using_project_as_admin() {

        //create new user but not login to created project
        User userObj = UserUtility.createUserObj();
        ResponseEntity<Void> register = UserUtility.register(userObj);
        String userId = Objects.requireNonNull(register.getHeaders().getLocation()).toString();
        //add admin
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins/" + userId)),
                HttpMethod.POST, request,
                Void.class);
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
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins/" + RandomUtility.randomStringWithNum())),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //2. try to remove admin when total admin equal to 2
        List<User> users = new ArrayList<>(tenantContext.getUserSet());
        //add admin
        TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins/" + users.get(0))),
                HttpMethod.POST, request,
                Void.class);
        //remove admin
        ResponseEntity<Void> exchange5 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(),
                        "/admins/" +  users.get(0))),
                HttpMethod.DELETE, request,
                Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
    }
}
