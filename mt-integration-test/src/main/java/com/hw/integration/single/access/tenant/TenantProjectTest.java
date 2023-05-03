package com.hw.integration.single.access.tenant;

import static com.hw.helper.AppConstant.TENANT_PROJECTS_CREATE;
import static com.hw.helper.AppConstant.TENANT_PROJECTS_LOOKUP;

import com.hw.helper.Project;
import com.hw.helper.ProjectTenantView;
import com.hw.helper.SumTotal;
import com.hw.helper.utility.RandomUtility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import com.hw.integration.single.access.CommonTest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class TenantProjectTest extends CommonTest {
    @Test
    public void tenant_can_create_project() throws InterruptedException {
        String jwtUser = UserUtility.getJwtUser();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUser);
        headers.setContentType(MediaType.APPLICATION_JSON);
        //get current project list
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<Project>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(TENANT_PROJECTS_LOOKUP), HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        Integer previousCount = exchange.getBody().getTotalItemCount();
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String s = RandomUtility.randomStringWithNum();
        HttpEntity<String> request2 =
            new HttpEntity<>("{\"name\":\"" + s + "\"}", headers);
        TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(TENANT_PROJECTS_CREATE), HttpMethod.POST, request2,
                Void.class);
        Thread.sleep(20000);
        //get updated project list
        String jwtUserNext = UserUtility.getJwtUser();
        HttpHeaders headersNext = new HttpHeaders();
        headersNext.setBearerAuth(jwtUserNext);
        HttpEntity<String> requestNext =
            new HttpEntity<>(null, headersNext);
        ResponseEntity<SumTotal<Project>> exchange3 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(TENANT_PROJECTS_LOOKUP), HttpMethod.GET, requestNext,
                new ParameterizedTypeReference<>() {
                });
        Assert.assertEquals(HttpStatus.OK, exchange3.getStatusCode());
        Integer currentCount = exchange3.getBody().getTotalItemCount();
        Assert.assertNotSame(previousCount,
            currentCount);
    }

    @Test
    public void tenant_can_view_project_detail() throws InterruptedException {
        //create new project
        String jwtUser = UserUtility.getJwtUser();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUser);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String s = RandomUtility.randomStringWithNum();
        HttpEntity<String> request2 =
            new HttpEntity<>("{\"name\":\"" + s + "\"}", headers);
        TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(TENANT_PROJECTS_CREATE), HttpMethod.POST, request2,
                Void.class);
        Thread.sleep(20000);
        //get project list
        String jwtUserNext = UserUtility.getJwtUser();
        HttpHeaders headersNext = new HttpHeaders();
        headersNext.setBearerAuth(jwtUserNext);
        HttpEntity<String> requestNext =
            new HttpEntity<>(null, headersNext);
        ResponseEntity<SumTotal<Project>> exchange3 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(TENANT_PROJECTS_LOOKUP), HttpMethod.GET, requestNext,
                new ParameterizedTypeReference<>() {
                });
        List<Project> data = exchange3.getBody().getData();
        Project project = data.get(RandomUtility.pickRandomFromList(data.size()));
        String id = project.getId();
        //get project detail
        ResponseEntity<ProjectTenantView> exchange4 = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(UrlUtility.combinePath(TENANT_PROJECTS_CREATE, id)),
                HttpMethod.GET, requestNext,
                ProjectTenantView.class);
        Assert.assertEquals(HttpStatus.OK, exchange4.getStatusCode());
        Assert.assertNotSame(0, exchange4.getBody().getTotalUserOwned());
    }
}
