package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.pojo.PatchCommand;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.SumTotal;
import com.mt.test_case.helper.pojo.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class ProjectUtility {
    private static final ParameterizedTypeReference<SumTotal<Project>> reference =
        new ParameterizedTypeReference<>() {
        };

    private static String getUrl() {
        return UrlUtility.getAccessUrl(AppConstant.TENANT_PROJECTS_CREATE);
    }

    public static Project createRandomProjectObj() {
        Project project = new Project();
        project.setName(RandomUtility.randomStringWithNum());
        return project;
    }

    public static ResponseEntity<Void> createTenantProject(Project project, User user) {
        return Utility.createResource(user, getUrl(), project);
    }

    public static ResponseEntity<Void> updateTenantProject(Project project, User user) {
        return Utility.updateResource(user, getUrl(), project, project.getId());
    }

    public static ResponseEntity<Void> patchTenantProject(Project project, User user,
                                                          PatchCommand command) {
        return Utility.patchResource(user, getUrl(), command, project.getId());
    }

    public static ResponseEntity<SumTotal<Project>> readTenantProjects(User user) {
        String accessUrl = UrlUtility.getAccessUrl(AppConstant.TENANT_PROJECTS_LOOKUP);
        return Utility.readResource(user, accessUrl, reference);
    }

    public static ResponseEntity<Project> readTenantProject(User user, Project project) {
        String accessUrl = UrlUtility.getAccessUrl(AppConstant.TENANT_PROJECTS_PREFIX);
        return Utility.readResource(user, accessUrl, project.getId(), Project.class);
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
