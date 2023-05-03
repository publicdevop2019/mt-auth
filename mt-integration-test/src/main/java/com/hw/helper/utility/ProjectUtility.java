package com.hw.helper.utility;

import static com.hw.helper.AppConstant.TENANT_PROJECTS_CREATE;
import static com.hw.helper.AppConstant.TENANT_PROJECTS_LOOKUP;

import com.hw.helper.Project;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ProjectUtility {
    public static void createTenantProject(String name, User user) {
        String login =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request2 =
            new HttpEntity<>("{\"name\":\"" + name + "\"}", headers);
        TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(TENANT_PROJECTS_CREATE), HttpMethod.POST, request2,
                Void.class);
    }
    public static SumTotal<Project> getTenantProjects(User user){
        String login =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<Project>> exchange = TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(TENANT_PROJECTS_LOOKUP), HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
        return exchange.getBody();
    }
}
