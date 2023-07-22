package com.mt.helper.utility;

import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.ProjectAdmin;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class AdminUtility {
    private static final ParameterizedTypeReference<SumTotal<ProjectAdmin>> reference =
        new ParameterizedTypeReference<>() {
        };

    private static String getUrl(Project project) {
        return UrlUtility.appendPath(TenantUtility.getTenantUrl(project), "admins");
    }


    public static ResponseEntity<SumTotal<ProjectAdmin>> readAdmin(User creator, Project project) {
        String adminUrl = getUrl(project);
        return Utility.readResource(creator, adminUrl, reference);
    }

    public static ResponseEntity<Void> makeAdmin(User creator, Project project, User user) {
        String adminUrl = getUrl(project);
        return Utility.createResource(creator, UrlUtility.appendPath(adminUrl, user.getId()));
    }

    public static ResponseEntity<Void> removeAdmin(User creator, Project project, User user) {
        String adminUrl = getUrl(project);
        return Utility.deleteResource(creator, adminUrl, user.getId());
    }
}
