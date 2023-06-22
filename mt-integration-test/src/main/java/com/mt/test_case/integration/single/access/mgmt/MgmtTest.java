package com.mt.test_case.integration.single.access.mgmt;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.CommonTest;
import com.mt.test_case.helper.pojo.BellNotification;
import com.mt.test_case.helper.pojo.CheckSum;
import com.mt.test_case.helper.pojo.Job;
import com.mt.test_case.helper.pojo.RevokeToken;
import com.mt.test_case.helper.pojo.StoredEvent;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.SystemNotification;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
import java.util.Objects;
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
            .exchange(UrlUtility.getAccessUrl(AppConstant.MGMT_BELL),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_job_info() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Job[]> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.MGMT_JOBS), HttpMethod.GET, request,
                Job[].class);
        Assert.assertNotSame(0, Objects.requireNonNull(exchange.getBody()).length);
    }

    @Test
    public void admin_can_reset_job_status() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Job[]> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.MGMT_JOBS), HttpMethod.GET, request,
                Job[].class);
        Job[] body = exchange.getBody();
        assert body != null;
        int i = RandomUtility.pickRandomFromList(body.length);
        Job job = body[i];
        String id = job.getId();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(UrlUtility.combinePath(
                    AppConstant.MGMT_JOBS, id + "/reset")),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());

    }

    @Test
    public void admin_can_rest_data_validation_failure_count() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(
                UrlUtility.getAccessUrl(UrlUtility.combinePath("mgmt/job", "validation/reset")),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_tokens_revoked() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<RevokeToken>> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.MGMT_TOKENS),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_check_proxy_cache_md5() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<CheckSum> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.MGMT_PROXY_CHECK),
                HttpMethod.GET, request,
                CheckSum.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
        Assert.assertNotNull(Objects.requireNonNull(exchange2.getBody()).getHostValue());
    }


    @Test
    public void admin_can_reload_proxy_cache() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<Void> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.MGMT_PROXY_RELOAD),
                HttpMethod.POST, request,
                Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_system_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.MGMT_EVENT),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void admin_can_view_audit_events() {
        HttpEntity<String> request = getAdminHttpEntity();
        ResponseEntity<SumTotal<StoredEvent>> exchange2 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.MGMT_EVENT_AUDIT),
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
                UrlUtility.getAccessUrl(UrlUtility.appendQuery(AppConstant.MGMT_EVENT, "query=rejected:1")),
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
                UrlUtility.getAccessUrl(UrlUtility.appendQuery(AppConstant.MGMT_EVENT, "query=routable:0")),
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
                UrlUtility.getAccessUrl(AppConstant.MGMT_NOTIFICATION),
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
        return new HttpEntity<>(null, headers);
    }
}
