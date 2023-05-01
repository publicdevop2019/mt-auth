package com.hw.integration.single.access.mgmt;

import static com.hw.helper.AppConstant.MGMT_BELL;
import static com.hw.helper.AppConstant.MGMT_EVENT;
import static com.hw.helper.AppConstant.MGMT_EVENT_AUDIT;
import static com.hw.helper.AppConstant.MGMT_JOBS;
import static com.hw.helper.AppConstant.MGMT_NOTIFICATION;
import static com.hw.helper.AppConstant.MGMT_PROXY_CHECK;
import static com.hw.helper.AppConstant.MGMT_PROXY_RELOAD;
import static com.hw.helper.AppConstant.MGMT_TOKENS;

import com.hw.helper.BellNotification;
import com.hw.helper.CheckSum;
import com.hw.helper.Job;
import com.hw.helper.Notification;
import com.hw.helper.RevokeToken;
import com.hw.helper.StoredEvent;
import com.hw.helper.SumTotal;
import com.hw.helper.SystemNotification;
import com.hw.helper.utility.RandomUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import com.hw.integration.single.access.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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
public class MgmtTest extends CommonTest {

    @Test
    public void admin_can_view_system_bell_notification() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<BellNotification>> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(MGMT_BELL),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_job_info() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Job[]> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(MGMT_JOBS), HttpMethod.GET, request,
                Job[].class);
        Assert.assertNotSame(0, exchange.getBody().length);
    }

    @Test
    public void admin_can_reset_job_status() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Job[]> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(MGMT_JOBS), HttpMethod.GET, request,
                Job[].class);
        Job[] body = exchange.getBody();
        int i = RandomUtility.pickRandomFromList(body.length);
        Job job = body[i];
        String id = job.getId();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(UrlUtility.combinePath(MGMT_JOBS, id + "/reset")),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

    }

    @Test
    public void admin_can_rest_data_validation_failure_count() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(
                UrlUtility.getAccessUrl(UrlUtility.combinePath(MGMT_JOBS, "validation/reset")),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_tokens_revoked() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<RevokeToken>> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(MGMT_TOKENS),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_check_proxy_cache_md5() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<CheckSum> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(MGMT_PROXY_CHECK),
                HttpMethod.GET, request,
                CheckSum.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assert.assertNotNull(exchange2.getBody().getHostValue());
    }


    @Test
    public void admin_can_reload_proxy_cache() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(MGMT_PROXY_RELOAD),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_system_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(MGMT_EVENT),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_audit_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(MGMT_EVENT_AUDIT),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_failure_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(
                UrlUtility.getAccessUrl(UrlUtility.appendQuery(MGMT_EVENT, "query=rejected:1")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_unroutable_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(
                UrlUtility.getAccessUrl(UrlUtility.appendQuery(MGMT_EVENT, "query=routable:0")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_system_notification_history() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<SystemNotification>> exchange2 = TestContext.getRestTemplate()
            .exchange(
                UrlUtility.getAccessUrl(MGMT_NOTIFICATION),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }


    private static HttpEntity<String> getAdminHttpEntity() {
        String token =
            UserUtility.getJwtAdmin();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        return request;
    }
}
