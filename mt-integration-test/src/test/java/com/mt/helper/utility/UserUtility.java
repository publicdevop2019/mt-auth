package com.mt.helper.utility;


import static com.mt.helper.AppConstant.USER_MGMT;

import com.mt.helper.AppConstant;
import com.mt.helper.TenantContext;
import com.mt.helper.pojo.AssignRoleReq;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.PatchCommand;
import com.mt.helper.pojo.Project;
import com.mt.helper.pojo.SumTotal;
import com.mt.helper.pojo.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

@Slf4j
public class UserUtility {
    private static final ParameterizedTypeReference<SumTotal<User>> reference =
        new ParameterizedTypeReference<>() {
        };

    private static String getUrl(Project project) {
        return HttpUtility.appendPath(TenantUtility.getTenantUrl(project), "users");
    }

    public static User randomEmailPwdUser() {
        return createEmailPwdUser(RandomUtility.randomEmail(),
            RandomUtility.randomStringWithNum());
    }

    public static User randomUsernamePwdUser() {
        return createUsernamePwdUser(RandomUtility.randomStringWithNum(),
            RandomUtility.randomPassword());
    }

    public static User randomEmailOnlyUser() {
        return createEmailOnlyUser(RandomUtility.randomEmail());
    }

    public static User randomMobileOnlyUser() {
        return createMobileOnlyUser(RandomUtility.randomMobileNumber());
    }

    public static User createEmailPwdUser(String email, String pwd) {
        User user = new User();
        user.setEmail(email);
        //uncomment below for random password
        //user.setPassword("P1!" + password.substring(0, 10));
        user.setPassword("Password1!");
        return user;
    }

    public static User createUsernamePwdUser(String username, String pwd) {
        User user = new User();
        user.setUsername(username);
        //uncomment below for random password
        //user.setPassword("P1!" + password.substring(0, 10));
        user.setPassword("Password1!");
        return user;
    }

    public static User createEmailOnlyUser(String email) {
        User user = new User();
        user.setEmail(email);
        return user;
    }

    public static User createMobileOnlyUser(String mobileNum) {
        User user = new User();
        user.setMobileNumber(mobileNum);
        user.setCountryCode("86");
        return user;
    }

    public static ResponseEntity<Void> sendVerifyCode(User user, String registerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(registerToken);
        headers.set("changeId", UUID.randomUUID().toString());
        Map<String, String> body = new HashMap<>();
        body.put("email", user.getEmail());
        body.put("countryCode", user.getCountryCode());
        body.put("mobileNumber", user.getMobileNumber());
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        return TestContext.getRestTemplate()
            .exchange(HttpUtility.getAccessUrl("/verification-code"), HttpMethod.POST, request,
                Void.class);
    }

    public static ResponseEntity<Void> sendVerifyCode(User user) {
        ResponseEntity<DefaultOAuth2AccessToken> token = OAuth2Utility
            .getClientCredentialToken(
                AppConstant.CLIENT_ID_NONE_LOGIN_ID, AppConstant.COMMON_CLIENT_SECRET);
        return sendVerifyCode(user, token.getBody().getValue());
    }

    public static ResponseEntity<Void> lockUser(String userIdToLock, User user,
                                                PatchCommand command) {
        String url = HttpUtility.getAccessUrl(USER_MGMT);
        return Utility.patchResource(user, url, command, userIdToLock);
    }

    /**
     * register new user then login.
     *
     * @return login token
     */
    public static String registerNewUserThenLogin() {
        User randomUser = randomEmailPwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            emailPwdLogin(randomUser.getEmail(), randomUser.getPassword());
        return Objects.requireNonNull(response.getBody()).getValue();
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> emailPwdLogin(String email,
                                                                         String userPwd) {
        return OAuth2Utility
            .getPasswordFlowEmailPwdToken(AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.COMMON_CLIENT_SECRET,
                email,
                userPwd);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> emailCodeLogin(User user) {
        sendVerifyCode(user);
        user.setCode("123456");
        return OAuth2Utility
            .getPasswordFlowEmailCodeToken(AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.COMMON_CLIENT_SECRET,
                user.getEmail(), user.getCode()
            );
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> emailCodeLogin(User user, String code) {
        user.setCode(code);
        return OAuth2Utility
            .getPasswordFlowEmailCodeToken(AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.COMMON_CLIENT_SECRET,
                user.getEmail(), user.getCode()
            );
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> mobileCodeLogin(User user) {
        sendVerifyCode(user);
        user.setCode("123456");
        return OAuth2Utility
            .getPasswordFlowMobileCodeToken(AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.COMMON_CLIENT_SECRET,
                user.getCountryCode(), user.getMobileNumber(), user.getCode()
            );
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> mobileCodeLogin(User user, String code) {
        user.setCode(code);
        return OAuth2Utility
            .getPasswordFlowMobileCodeToken(AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.COMMON_CLIENT_SECRET,
                user.getCountryCode(), user.getMobileNumber(), user.getCode()
            );
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> usernamePwdLogin(User user) {
        return OAuth2Utility
            .getPasswordFlowUsernamePwdToken(AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.COMMON_CLIENT_SECRET,
                user.getUsername(), user.getPassword()
            );
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> mobilePwdLogin(User user) {
        return OAuth2Utility
            .getPasswordFlowMobilePwdToken(AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.COMMON_CLIENT_SECRET,
                user.getCountryCode(), user.getMobileNumber(), user.getPassword()
            );
    }

    public static String emailPwdLogin(User user) {
        ResponseEntity<DefaultOAuth2AccessToken> oAuth2PasswordToken =
            emailPwdLogin(user.getEmail(), user.getPassword());
        String token = oAuth2PasswordToken.getBody().getValue();
        log.info("login token {}", token);
        if (token == null) {
            log.info("login token error with response {}",
                oAuth2PasswordToken.getStatusCode().value());
        }
        return token;
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> emailPwdLoginRaw(User user) {
        return emailPwdLogin(user.getEmail(), user.getPassword());
    }

    public static String getJwtUser() {
        return emailPwdLogin(AppConstant.ACCOUNT_USERNAME_USER,
            AppConstant.ACCOUNT_PASSWORD_USER).getBody()
            .getValue();
    }

    public static String getJwtAdmin() {
        ResponseEntity<DefaultOAuth2AccessToken> login =
            emailPwdLogin(AppConstant.ACCOUNT_EMAIL_ADMIN,
                AppConstant.ACCOUNT_PASSWORD_ADMIN);
        if (!login.getStatusCode().is2xxSuccessful()) {
            log.info("jwt admin token failed with status {} body {}", login.getStatusCode().value(),
                login.getBody());
        }
        return login.getBody().getValue();
    }

    /**
     * create new user with user id
     *
     * @return created user
     */
    public static User createEmailPwdUser() {
        User user = randomEmailPwdUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            emailPwdLogin(user.getEmail(), user.getPassword());
        String s = HttpUtility.getId(response);
        user.setId(s);
        return user;
    }

    /**
     * create user whom login to tenant project
     *
     * @param project tenant project
     * @param client  sso client
     * @return user logged in
     */
    public static User userLoginToTenant(Project project, Client client) {
        //create new user then login to created project
        User tenantUser = createEmailPwdUser();
        String user1Token = emailPwdLogin(tenantUser);
        return loginTenant(project, client, tenantUser, user1Token);
    }

    /**
     * create mobile code user whom login to tenant project
     *
     * @param project tenant project
     * @param client  sso client
     * @return user logged in
     */
    public static User userMobileCodeLoginToTenant(Project project, Client client) {
        //create new user then login to created project
        User tenantUser = randomMobileOnlyUser();
        String user1Token = mobileCodeLogin(tenantUser).getBody().getValue();
        return loginTenant(project, client, tenantUser, user1Token);
    }

    /**
     * create username pwd user whom login to tenant project
     *
     * @param project tenant project
     * @param client  sso client
     * @return user logged in
     */
    public static User userUsernamePwdLoginToTenant(Project project, Client client) {
        //create new user then login to created project
        User tenantUser = randomUsernamePwdUser();
        String user1Token = usernamePwdLogin(tenantUser).getBody().getValue();
        return loginTenant(project, client, tenantUser, user1Token);
    }

    private static User loginTenant(Project project, Client client, User tenantUser,
                                    String user1Token) {
        ResponseEntity<String> codeResponse =
            OAuth2Utility.authorizeLogin(project.getId(), client.getId(), user1Token,
                AppConstant.TEST_REDIRECT_URL);
        if (!codeResponse.getStatusCode().is2xxSuccessful()) {
            log.info("authorize failed with status code {}", codeResponse.getStatusCode().value());
        }
        Assertions.assertEquals(HttpStatus.OK, codeResponse.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> oAuth2AuthorizationToken =
            OAuth2Utility.getAuthorizationToken(
                OAuth2Utility.getAuthorizationCode(codeResponse),
                AppConstant.TEST_REDIRECT_URL, client.getId(), client.getClientSecret());
        if (!oAuth2AuthorizationToken.getStatusCode().is2xxSuccessful()) {
            log.info("get token failed with status code {}",
                oAuth2AuthorizationToken.getStatusCode().value());
        }
        Assertions.assertEquals(HttpStatus.OK, oAuth2AuthorizationToken.getStatusCode());
        return tenantUser;
    }

    public static ResponseEntity<Void> assignTenantUserRole(TenantContext tenantContext, User user,
                                                            AssignRoleReq req) {
        String userIdUrl = HttpUtility.appendPath(getUrl(tenantContext.getProject()), user.getId());
        String url = HttpUtility.appendPath(userIdUrl, "roles");
        return Utility.createResource(tenantContext.getCreator(), url, req);
    }

    public static ResponseEntity<Void> removeTenantUserRole(TenantContext tenantContext, User user,
                                                            String roleId) {
        String userIdUrl = HttpUtility.appendPath(getUrl(tenantContext.getProject()), user.getId());
        String userRoleUrl = HttpUtility.appendPath(userIdUrl, "roles");
        return Utility.deleteResource(tenantContext.getCreator(), userRoleUrl, roleId);
    }

    public static ResponseEntity<User> readTenantUser(TenantContext tenantContext,
                                                      User user) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, user.getId(), User.class);
    }

    public static ResponseEntity<SumTotal<User>> readTenantUsers(
        TenantContext tenantContext) {
        String url = getUrl(tenantContext.getProject());
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static ResponseEntity<SumTotal<User>> readTenantUsersByQuery(
        TenantContext tenantContext, String query) {
        String accessUrl = getUrl(tenantContext.getProject());
        String url = HttpUtility.appendQuery(accessUrl, query);
        return Utility.readResource(tenantContext.getCreator(), url, reference);
    }

    public static User randomMobilePwdUser() {
        return createMobilePwdUser(RandomUtility.randomMobileNumber(),
            RandomUtility.randomPassword());
    }

    private static User createMobilePwdUser(String mobileNum, String pwd) {
        User user = new User();
        user.setMobileNumber(mobileNum);
        user.setCountryCode("86");
        //uncomment below for random password
        //user.setPassword("P1!" + password.substring(0, 10));
        user.setPassword("Password1!");
        return user;
    }
}
