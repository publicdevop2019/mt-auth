package com.hw.integration.single.access.tenant;

import static com.hw.helper.AppConstant.TENANT_PROJECTS_PREFIX;
import static com.hw.helper.AppConstant.TEST_REDIRECT_URL;

import com.hw.helper.SumTotal;
import com.hw.helper.User;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.TenantUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import com.hw.integration.single.access.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantUserTest extends CommonTest {
    private TenantUtility.TenantContext tenantContext;

    @Before
    public void setUp() {
        TestContext.init();
        log.info("test id {}", TestContext.getTestId());
        tenantContext = TenantUtility.initTenant();
    }

    @Test
    public void tenant_can_search_user_by_email() {

    }

    @Test
    public void tenant_can_view_users() {
        //get user list
        String login =
            UserUtility.login(tenantContext.getCreator());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<User>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, tenantContext.getProject().getId(), "/users")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertNotSame(0, exchange.getBody().getData().size());
    }

    @Test
    public void tenant_can_view_user_detail() {

    }

    @Test
    public void tenant_cannot_find_user_not_using_project() {

    }

    @Test
    public void tenant_can_assign_role_to_user() {

    }

    @Test
    public void tenant_cannot_add_invalid_role_to_user() {

    }
}
