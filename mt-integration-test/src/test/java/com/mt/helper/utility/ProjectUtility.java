package com.mt.helper.utility;

import com.mt.helper.AppConstant;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.ProjectStatus;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ProjectUtility {
    private static final ParameterizedTypeReference<SumTotal<Project>> reference =
        new ParameterizedTypeReference<>() {
        };

    private static String getUrl() {
        return HttpUtility.getAccessUrl(AppConstant.TENANT_PROJECTS_CREATE);
    }

    public static Project createRandomProjectObj() {
        Project project = new Project();
        project.setName(RandomUtility.randomStringWithNum());
        return project;
    }

    public static ResponseEntity<Void> createTenantProject(Project project, User user) {
        return Utility.createResource(user, getUrl(), project);
    }

    public static ResponseEntity<Void> createTenantProject(Project project, String token) {
        return Utility.createResource(token, getUrl(), project);
    }

    public static ResponseEntity<Void> updateTenantProject(Project project, User user) {
        return Utility.updateResource(user, getUrl(), project, project.getId());
    }

    public static ResponseEntity<Void> patchTenantProject(Project project, User user,
                                                          PatchCommand command) {
        return Utility.patchResource(user, getUrl(), command, project.getId());
    }

    public static ResponseEntity<SumTotal<Project>> readTenantProjects(User user) {
        String accessUrl = HttpUtility.getAccessUrl(AppConstant.TENANT_PROJECTS_LOOKUP);
        return Utility.readResource(user, accessUrl, reference);
    }

    public static ResponseEntity<ProjectStatus> checkProjectReady(User user, Project project) {
        String accessUrl = HttpUtility.getAccessUrl(
            HttpUtility.combinePath(AppConstant.TENANT_PROJECTS_PREFIX, project.getId(), "ready"));
        return Utility.readResource(user, accessUrl, ProjectStatus.class);
    }

    public static ResponseEntity<Project> readTenantProject(User user, Project project) {
        String accessUrl = HttpUtility.getAccessUrl(AppConstant.TENANT_PROJECTS_PREFIX);
        return Utility.readResource(user, accessUrl, project.getId(), Project.class);
    }

    public static Project tenantCreateProject(User tenantUser) {
        //create new project
        Project project = createRandomProjectObj();
        ResponseEntity<Void> tenantProject = createTenantProject(project, tenantUser);
        project.setId(HttpUtility.getId(tenantProject));
        TestUtility.createProjectDefaultWait();
        log.info("after default timeout, check project status");
        ResponseEntity<ProjectStatus> statusCode =
            checkProjectReady(tenantUser, project);
        if (!statusCode.getStatusCode().is2xxSuccessful()) {
            log.info("project status check failed, status {} body {}", statusCode.getStatusCode().value(), statusCode.getBody());
        }
        log.info("project status {}", Objects.requireNonNull(statusCode.getBody()).isStatus());
        Assertions.assertTrue(statusCode.getBody().isStatus());
        return project;
    }
}
