package com.hw.helper.utility;

import com.hw.helper.Project;
import com.hw.helper.User;
import java.util.LinkedHashSet;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantUtility {

    /**
     * create new tenant with project and 2 new users as admin and 1 sso login client
     *
     * @return TenantContext
     */
    public static TenantContext initTenant() {
        TenantContext tenantContext = new TenantContext();
        User tenant = UserUtility.createTenant();
        log.info("created tenant {}", tenant.getEmail());
        Project project = ProjectUtility.tenantCreateProject(tenant);
        String ssoLoginClient = ClientUtility.createTenantSsoLoginClient(tenant, project);
        User user1 = UserUtility.userLoginToTenant(project, ssoLoginClient);
        log.info("created user {}", user1.getEmail());
        User user2 = UserUtility.userLoginToTenant(project, ssoLoginClient);
        log.info("created user {}", user2.getEmail());
        tenantContext.creator = tenant;
        tenantContext.project = project;
        tenantContext.loginClientId = ssoLoginClient;
        tenantContext.userSet = new LinkedHashSet<>();
        tenantContext.userSet.add(user1);
        tenantContext.userSet.add(user2);
        AdminUtility.makeAdmin(tenant,project,user1);
        AdminUtility.makeAdmin(tenant,project,user2);
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
