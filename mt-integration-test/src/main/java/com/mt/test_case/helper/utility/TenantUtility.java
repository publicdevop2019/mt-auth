package com.mt.test_case.helper.utility;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.Project;
import com.mt.test_case.helper.pojo.User;
import java.util.ArrayList;
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
        tenantContext.setCreator(tenant);
        Project project = ProjectUtility.tenantCreateProject(tenant);
        tenantContext.setProject(project);
        Client ssoLoginClient = ClientUtility.createAuthorizationClientObj();
        ResponseEntity<Void> tenantClient =
            ClientUtility.createTenantClient(tenantContext, ssoLoginClient);
        ssoLoginClient.setId(UrlUtility.getId(tenantClient));
        User user1 = UserUtility.userLoginToTenant(project, ssoLoginClient.getId());
        log.info("created user {}", user1.getEmail());
        User user2 = UserUtility.userLoginToTenant(project, ssoLoginClient.getId());
        log.info("created user {}", user2.getEmail());
        tenantContext.setLoginClientId(ssoLoginClient.getId());
        tenantContext.setUsers(new ArrayList<>());
        tenantContext.getUsers().add(user1);
        tenantContext.getUsers().add(user2);
        AdminUtility.makeAdmin(tenant, project, user1);
        AdminUtility.makeAdmin(tenant, project, user2);
        return tenantContext;
    }

    public static String getTenantUrl(Project project) {
        return UrlUtility.getAccessUrl(
            UrlUtility.combinePath(AppConstant.TENANT_PROJECTS_PREFIX, project.getId()));
    }

}
