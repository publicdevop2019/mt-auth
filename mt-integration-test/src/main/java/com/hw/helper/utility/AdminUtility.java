package com.hw.helper.utility;

import static com.hw.helper.AppConstant.TENANT_PROJECTS_PREFIX;

import com.hw.helper.Project;
import com.hw.helper.ProjectAdmin;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class AdminUtility {
    public static ResponseEntity<SumTotal<ProjectAdmin>> readAdmin(User creator, Project project) {
        String login =
            UserUtility.login(creator);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, project.getId(),
                        "/admins")),
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
    }

    public static ResponseEntity<Void> makeAdmin(User creator, Project project, User user) {
        String login2 =
            UserUtility.login(creator);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login2);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, project.getId(),
                        "/admins/" + user.getId())),
                HttpMethod.POST, request,
                Void.class);
    }
    public static ResponseEntity<Void> removeAdmin(User creator, Project project, User user) {
        String login2 =
            UserUtility.login(creator);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login2);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(TENANT_PROJECTS_PREFIX, project.getId(),
                        "/admins/" + user.getId())),
                HttpMethod.DELETE, request,
                Void.class);
    }
}
