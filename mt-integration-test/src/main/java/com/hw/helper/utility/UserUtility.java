package com.hw.helper.utility;

import static com.hw.helper.AppConstant.ACCOUNT_PASSWORD_ADMIN;
import static com.hw.helper.AppConstant.ACCOUNT_PASSWORD_MALL_ADMIN;
import static com.hw.helper.AppConstant.ACCOUNT_PASSWORD_USER;
import static com.hw.helper.AppConstant.ACCOUNT_USERNAME_ADMIN;
import static com.hw.helper.AppConstant.ACCOUNT_USERNAME_MALL_ADMIN;
import static com.hw.helper.AppConstant.ACCOUNT_USERNAME_USER;
import static com.hw.helper.AppConstant.CLIENT_ID_LOGIN_ID;
import static com.hw.helper.AppConstant.CLIENT_ID_REGISTER_ID;
import static com.hw.helper.AppConstant.EMPTY_CLIENT_SECRET;
import static com.hw.helper.AppConstant.TEST_REDIRECT_URL;

import com.hw.helper.PendingUser;
import com.hw.helper.Project;
import com.hw.helper.User;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

public class UserUtility {
//    public void initTestUser() {
//        if (testUser.size() == 0) {
//            log.debug("start of creating test users");
//            ResourceOwner resourceOwner1 = randomRegisterAnUser();
//            ResourceOwner resourceOwner2 = randomRegisterAnUser();
//            ResourceOwner resourceOwner3 = randomRegisterAnUser();
//            ResourceOwner resourceOwner4 = randomRegisterAnUser();
//            ResourceOwner resourceOwner5 = randomRegisterAnUser();
//            ResourceOwner resourceOwner6 = randomRegisterAnUser();
//            ResourceOwner resourceOwner7 = randomRegisterAnUser();
//            ResourceOwner resourceOwner8 = randomRegisterAnUser();
//            ResourceOwner resourceOwner9 = randomRegisterAnUser();
//            ResourceOwner resourceOwner10 = randomRegisterAnUser();
//            testUser.add(resourceOwner1);
//            testUser.add(resourceOwner2);
//            testUser.add(resourceOwner3);
//            testUser.add(resourceOwner4);
//            testUser.add(resourceOwner5);
//            testUser.add(resourceOwner6);
//            testUser.add(resourceOwner7);
//            testUser.add(resourceOwner8);
//            testUser.add(resourceOwner9);
//            testUser.add(resourceOwner10);
//            log.debug("end of creating test users");
//        } else {
//            log.debug("test users already exist");
//
//        }
//    }


    public static User createRandomUserObj() {
        return userCreateDraftObj(RandomUtility.randomStringWithNum() + "@gmail.com",
            RandomUtility.randomStringWithNum());
    }

    public static User userCreateDraftObj(String username, String password) {
        User user = new User();
        user.setEmail(username);
//        user.setPassword("P1!" + password.substring(0, 10));
        user.setPassword("Password1!");
        user.setMobileNumber("1231231234");
        user.setCountryCode("1");
        return user;
    }

    public static ResponseEntity<Void> register(User user) {
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(CLIENT_ID_REGISTER_ID, EMPTY_CLIENT_SECRET);
        createPendingUser(user);
        return enterActivationCode(user, registerTokenResponse.getBody().getValue());
    }

    public static ResponseEntity<Void> enterActivationCode(User user,
                                                           String registerToken) {
        PendingUser pendingUser = new PendingUser();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.setBearerAuth(registerToken);
        headers1.set("changeId", UUID.randomUUID().toString());
        pendingUser.setEmail(user.getEmail());
        pendingUser.setCountryCode(user.getCountryCode());
        pendingUser.setMobileNumber(user.getMobileNumber());
        pendingUser.setPassword(user.getPassword());
        pendingUser.setActivationCode("123456");
        HttpEntity<PendingUser> request1 =
            new HttpEntity<>(pendingUser, headers1);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl("/users"), HttpMethod.POST, request1,
                Void.class);
    }

    public static ResponseEntity<Void> createPendingUser(User user, String registerToken,
                                                         PendingUser pendingUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(registerToken);
        headers.set("changeId", UUID.randomUUID().toString());
        pendingUser.setEmail(user.getEmail());
        pendingUser.setMobileNumber(user.getMobileNumber());
        pendingUser.setCountryCode(user.getCountryCode());

        HttpEntity<PendingUser> request = new HttpEntity<>(pendingUser, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl("/pending-users"), HttpMethod.POST, request,
                Void.class);
    }

    public static ResponseEntity<Void> createPendingUser(User user) {
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(CLIENT_ID_REGISTER_ID, EMPTY_CLIENT_SECRET);
        PendingUser pendingUser = new PendingUser();
        return createPendingUser(user, registerTokenResponse.getBody().getValue(), pendingUser);
    }

    /**
     * register new user then login.
     *
     * @return login token
     */
    public static String registerNewUserThenLogin() {
        User randomUser = createRandomUserObj();
        register(randomUser);
        ResponseEntity<DefaultOAuth2AccessToken> loginTokenResponse =
            login(randomUser.getEmail(), randomUser.getPassword());
        return Objects.requireNonNull(loginTokenResponse.getBody()).getValue();
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> login(String username,
                                                                 String userPwd) {
        return OAuth2Utility
            .getOAuth2PasswordToken(CLIENT_ID_LOGIN_ID, EMPTY_CLIENT_SECRET, username,
                userPwd);
    }

    public static String login(User user) {
        return OAuth2Utility
            .getOAuth2PasswordToken(CLIENT_ID_LOGIN_ID, EMPTY_CLIENT_SECRET, user.getEmail(),
                user.getPassword()).getBody().getValue();
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getJwtPasswordAdmin() {
        return login(ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getJwtPasswordMallTenant() {
        return login(ACCOUNT_USERNAME_MALL_ADMIN, ACCOUNT_PASSWORD_MALL_ADMIN);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getJwtPasswordUser() {
        return login(ACCOUNT_USERNAME_USER, ACCOUNT_PASSWORD_USER);
    }

    public static String getJwtUser() {
        return login(ACCOUNT_USERNAME_USER, ACCOUNT_PASSWORD_USER).getBody().getValue();
    }

    public static String getJwtAdmin() {
        return login(ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN).getBody().getValue();
    }

    /**
     * create new user without any login to tenant project
     * @return created user
     */
    public static User createNewUser() {
        User randomUser = createRandomUserObj();
        ResponseEntity<Void> register = register(randomUser);
        String s = register.getHeaders().getLocation().toString();
        randomUser.setId(s);
        return randomUser;
    }

    public static User createTenant() {
        //create new tenant user
        User tenantUser = createRandomUserObj();
        register(tenantUser);
        return tenantUser;
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
        User tenantUser = createNewUser();
        String user1Token = login(tenantUser);
        ResponseEntity<String> codeResponse =
            OAuth2Utility.authorizeLogin(project.getId(), clientId, user1Token, TEST_REDIRECT_URL);
        OAuth2Utility.getOAuth2AuthorizationToken(OAuth2Utility.getAuthorizationCode(codeResponse),
            TEST_REDIRECT_URL, clientId, "");
        return tenantUser;
    }
}
