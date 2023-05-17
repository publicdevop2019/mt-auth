package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.User;
import com.mt.test_case.helper.AppConstant;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ProjectUtility {
    public static Project createRandomProjectObj() {
        Project project = new Project();
        project.setName(RandomUtility.randomStringWithNum());
        return project;
    }

    public static ResponseEntity<Void> createTenantProject(Project project, User user) {
        String login =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        HttpEntity<Project> request2 =
            new HttpEntity<>(project, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.TENANT_PROJECTS_CREATE), HttpMethod.POST, request2,
                Void.class);
    }

    public static ResponseEntity<SumTotal<Project>> readTenantProjects(User user) {
        String login =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(AppConstant.TENANT_PROJECTS_LOOKUP), HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {
                });
    }

    public static ResponseEntity<Project> readTenantProject(User user, Project project) {
        String login =
            UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request =
            new HttpEntity<>(null, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl(
                    UrlUtility.combinePath(AppConstant.TENANT_PROJECTS_PREFIX, project.getId())), HttpMethod.GET,
                request,
                Project.class);
    }

    public static Project tenantCreateProject(User tenantUser) {
        //create new project
        Project randomProjectObj = createRandomProjectObj();
        createTenantProject(randomProjectObj, tenantUser);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SumTotal<Project> tenantProjects = readTenantProjects(tenantUser).getBody();
        return tenantProjects.getData().get(0);
    }
}
