package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class TenantUtility {

    /**
     * create new tenant with project and 2 new users as admin and 1 sso login client
     *
     * @return TenantContext
     */
    public static TenantContext initTenant() {
        TenantContext tenantContext = new TenantContext();
        User tenant = UserUtility.createUser();
        log.info("created tenant {}", tenant.getEmail());
        tenantContext.creator = tenant;
        Project project = ProjectUtility.tenantCreateProject(tenant);
        tenantContext.project = project;
        Client ssoLoginClient = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, ssoLoginClient);
        ssoLoginClient.setId(tenantClient.getHeaders().getLocation().toString());
        User user1 = UserUtility.userLoginToTenant(project, ssoLoginClient.getId());
        log.info("created user {}", user1.getEmail());
        User user2 = UserUtility.userLoginToTenant(project, ssoLoginClient.getId());
        log.info("created user {}", user2.getEmail());
        tenantContext.loginClientId = ssoLoginClient.getId();
        tenantContext.users = new ArrayList<>();
        tenantContext.users.add(user1);
        tenantContext.users.add(user2);
        AdminUtility.makeAdmin(tenant,project,user1);
        AdminUtility.makeAdmin(tenant,project,user2);
        return tenantContext;
    }

    @Data
    public static class TenantContext {
        private User creator;
        private Project project;
        private String loginClientId;
        private List<User> users;
    }
}
