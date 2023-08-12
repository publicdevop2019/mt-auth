package com.mt.integration.single.access.mgmt;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.BellNotification;
import com.mt.helper.pojo.CheckSum;
import com.mt.helper.pojo.Job;
import com.mt.helper.pojo.RevokeToken;
import com.mt.helper.pojo.StoredEvent;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.SystemNotification;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.UserUtility;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})

@Slf4j
public class MgmtTest{
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach(log);
    }
    @Test
    public void admin_can_view_system_bell_notification() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<BellNotification>> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_BELL),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_job_info() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Job[]> exchange = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_JOBS), HttpMethod.GET, request,
                Job[].class);
        Assertions.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).length);
    }

    @Test
    public void admin_can_reset_job_status() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Job[]> exchange = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_JOBS), HttpMethod.GET, request,
                Job[].class);
        Job[] body = exchange.getBody();
        assert body != null;
        int i = RandomUtility.pickRandomFromList(body.length);
        Job job = body[i];
        String id = job.getId();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(HttpUtility.combinePath(
                    AppConstant.MGMT_JOBS, id + "/reset")),
                HttpMethod.POST, request,
                Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

    }

    @Test
    public void admin_can_rest_data_validation_failure_count() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(
                HttpUtility.getAccessUrl(HttpUtility.combinePath("mgmt/job", "validation/reset")),
                HttpMethod.POST, request,
                Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_tokens_revoked() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<RevokeToken>> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_TOKENS),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_check_proxy_cache_md5() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<CheckSum> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_PROXY_CHECK),
                HttpMethod.GET, request,
                CheckSum.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assertions.assertNotNull(Objects.requireNonNull(exchange2.getBody()).getHostValue());
    }


    @Test
    public void admin_can_reload_proxy_cache() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_PROXY_RELOAD),
                HttpMethod.POST, request,
                Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_system_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_EVENT),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_audit_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl(AppConstant.MGMT_EVENT_AUDIT),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_failure_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(
                HttpUtility.getAccessUrl(
                    HttpUtility.appendQuery(AppConstant.MGMT_EVENT, "query=rejected:1")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_unroutable_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(
                HttpUtility.getAccessUrl(
                    HttpUtility.appendQuery(AppConstant.MGMT_EVENT, "query=routable:0")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_system_notification_history() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<SystemNotification>> exchange2 = TestContext.getRestTemplate()
            .exchange(
                HttpUtility.getAccessUrl(AppConstant.MGMT_NOTIFICATION),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assertions.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }


    private static HttpEntity<String> getAdminHttpEntity() {
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(null, headers);
    }
}
