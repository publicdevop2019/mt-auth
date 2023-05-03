package com.hw.helper.utility;

import static com.hw.helper.AppConstant.TEST_REDIRECT_URL;

import com.hw.helper.Client;
import com.hw.helper.Project;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import java.util.LinkedHashSet;
import lombok.Data;
import org.springframework.http.ResponseEntity;

public class TenantUtility {
    public static User createTenant() {
        //create new tenant user
        User tenantUser = UserUtility.createUserObj();
        UserUtility.register(tenantUser);
        return tenantUser;
    }

    public static Project tenantCreateProject(User tenantUser) {
        //create new project
        ProjectUtility.createTenantProject(RandomUtility.randomStringWithNum(), tenantUser);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SumTotal<Project> tenantProjects = ProjectUtility.getTenantProjects(tenantUser);
        return tenantProjects.getData().get(0);
    }

    /**
     * create sso login client for tenant project
     *
     * @param tenantUser tenant user
     * @param project    project obj
     * @return new client id
     */
    public static String createSsoLoginClient(User tenantUser, Project project) {
        //create sso login client
        Client client = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantUser, client, project.getId());
        return tenantClient.getHeaders().getLocation().toString();
    }

    /**
     * create user whom login to tenant project
     *
     * @param project  tenant project
     * @param clientId sso client id
     * @return user logged in
     */
    public static User userLoginToTenant(Project project, String clientId) {
        //create new user then login to created project
        User user1 = UserUtility.createUserObj();
        UserUtility.register(user1);
        String user1Token = UserUtility.login(user1);
        ResponseEntity<String> codeResponse =
            OAuth2Utility.authorizeLogin(project.getId(), clientId, user1Token, TEST_REDIRECT_URL);
        OAuth2Utility.getOAuth2AuthorizationToken(OAuth2Utility.getAuthorizationCode(codeResponse),
            TEST_REDIRECT_URL, clientId, "");
        return user1;
    }

    /**
     * create new tenant with project and 2 new users
     * @return TenantContext
     */
    public static TenantContext initTenant() {
        TenantContext tenantContext = new TenantContext();
        User tenant = createTenant();
        Project project = tenantCreateProject(tenant);
        String ssoLoginClient = createSsoLoginClient(tenant, project);
        User user1 = userLoginToTenant(project, ssoLoginClient);
        User user2 = userLoginToTenant(project, ssoLoginClient);
        tenantContext.creator = tenant;
        tenantContext.project = project;
        tenantContext.loginClientId = ssoLoginClient;
        tenantContext.userSet = new LinkedHashSet<>();
        tenantContext.userSet.add(user1);
        tenantContext.userSet.add(user2);
        return tenantContext;
    }

    @Data
    public static class TenantContext {
        private User creator;
        private Project project;
        private String loginClientId;
        private LinkedHashSet<User> userSet;
    }
}
