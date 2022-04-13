package com.hw.helper.utility;

import static com.hw.helper.AppConstant.ACCOUNT_PASSWORD_ADMIN;
import static com.hw.helper.AppConstant.ACCOUNT_PASSWORD_USER;
import static com.hw.helper.AppConstant.ACCOUNT_USERNAME_ADMIN;
import static com.hw.helper.AppConstant.ACCOUNT_USERNAME_USER;
import static com.hw.helper.AppConstant.CLIENT_ID_LOGIN_ID;
import static com.hw.helper.AppConstant.CLIENT_ID_REGISTER_ID;
import static com.hw.helper.AppConstant.EMPTY_CLIENT_SECRET;

import com.hw.helper.PendingResourceOwner;
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


    public static User createUser() {
        return userCreateDraft(UUID.randomUUID().toString().replace("-", "") + "@gmail.com",
            UUID.randomUUID().toString().replace("-", ""));
    }

    public static User userCreateDraft(String username, String password) {
        User user = new User();
        user.setEmail(username);
        user.setPassword(password);
        return user;
    }

    public static User randomRegisterAnUser() {
        User random = createUser();
        register(random);
        return random;
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> register(User user) {
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(CLIENT_ID_REGISTER_ID, EMPTY_CLIENT_SECRET);
        PendingResourceOwner pendingResourceOwner = new PendingResourceOwner();
        createPendingUser(user, registerTokenResponse.getBody().getValue(), pendingResourceOwner);
        return enterActivationCode(user, registerTokenResponse.getBody().getValue(),
            pendingResourceOwner);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> enterActivationCode(User user,
                                                                               String registerToken,
                                                                               PendingResourceOwner pendingResourceOwner) {
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.setBearerAuth(registerToken);
        headers1.set("changeId", UUID.randomUUID().toString());
        pendingResourceOwner.setPassword(user.getPassword());
        pendingResourceOwner.setActivationCode("123456");
        HttpEntity<PendingResourceOwner> request1 =
            new HttpEntity<>(pendingResourceOwner, headers1);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl("/users"), HttpMethod.POST, request1,
                DefaultOAuth2AccessToken.class);
    }

    public static ResponseEntity<Void> createPendingUser(User user, String registerToken,
                                                         PendingResourceOwner pendingResourceOwner) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(registerToken);
        headers.set("changeId", UUID.randomUUID().toString());
        pendingResourceOwner.setEmail(user.getEmail());
        HttpEntity<PendingResourceOwner> request = new HttpEntity<>(pendingResourceOwner, headers);
        return TestContext.getRestTemplate()
            .exchange(UrlUtility.getAccessUrl("/pending-users"), HttpMethod.POST, request,
                Void.class);
    }

    /**
     * register new user then login.
     *
     * @return login token
     */
    public static String registerNewUserThenLogin() {
        User randomUser = createUser();
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

    public static ResponseEntity<DefaultOAuth2AccessToken> getJwtPasswordAdmin() {
        return login(ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
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

}
