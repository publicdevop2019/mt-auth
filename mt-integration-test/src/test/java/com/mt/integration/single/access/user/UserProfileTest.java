package com.mt.integration.single.access.user;

import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.User;
import com.mt.helper.pojo.UserUpdatePwd;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.io.File;
import java.io.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class UserProfileTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    private static ResponseEntity<User> getMyProfile(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        String url = HttpUtility.getAccessUrl("/users" + "/profile");
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<User> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, User.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        return exchange;
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void update_pwd_w_email_pwd_user() {
        User user = UserUtility.randomEmailPwdUser();
        String newPassword = RandomUtility.randomPassword();
        String oldPassword = user.getPassword();
        String token = UserUtility.emailPwdLogin(user);
        update_pwd_common(user, newPassword, token);

        //old password stop working
        ResponseEntity<DefaultOAuth2AccessToken> resp3 =
            UserUtility.emailPwdLogin(user.getEmail(), oldPassword);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());
        //new password should work
        ResponseEntity<DefaultOAuth2AccessToken> resp4 =
            UserUtility.emailPwdLogin(user.getEmail(), newPassword);
        Assertions.assertEquals(HttpStatus.OK, resp4.getStatusCode());

    }

    @Test
    public void update_pwd_w_email_code_user() {
        User user = UserUtility.randomEmailOnlyUser();
        String newPassword = RandomUtility.randomPassword();
        String token = UserUtility.emailCodeLogin(user).getBody().getValue();
        update_pwd_common(user, newPassword, token);
        //new password should work
        ResponseEntity<DefaultOAuth2AccessToken> resp4 =
            UserUtility.emailPwdLogin(user.getEmail(), newPassword);
        Assertions.assertEquals(HttpStatus.OK, resp4.getStatusCode());
    }

    @Test
    public void update_pwd_w_mobile_pwd_user() {
        User user = UserUtility.randomMobilePwdUser();
        String newPassword = RandomUtility.randomPassword();
        String oldPassword = user.getPassword();
        String token = UserUtility.mobilePwdLogin(user).getBody().getValue();

        update_pwd_common(user, newPassword, token);

        //old password stop working
        user.setPassword(oldPassword);
        ResponseEntity<DefaultOAuth2AccessToken> resp3 =
            UserUtility.mobilePwdLogin(user);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());
        //new password should work
        user.setPassword(newPassword);
        ResponseEntity<DefaultOAuth2AccessToken> resp4 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, resp4.getStatusCode());

    }

    @Test
    public void update_pwd_w_mobile_code_user() {
        User user = UserUtility.randomMobileOnlyUser();
        String newPassword = RandomUtility.randomPassword();
        String token = UserUtility.mobileCodeLogin(user).getBody().getValue();

        update_pwd_common(user, newPassword, token);
        //new password should work
        user.setPassword(newPassword);
        ResponseEntity<DefaultOAuth2AccessToken> resp4 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, resp4.getStatusCode());

    }

    @Test
    public void update_pwd_w_username_pwd_user() {
        User user = UserUtility.randomUsernamePwdUser();
        String newPassword = RandomUtility.randomPassword();
        String oldPassword = user.getPassword();
        String token = UserUtility.usernamePwdLogin(user).getBody().getValue();

        update_pwd_common(user, newPassword, token);

        //old password stop working
        user.setPassword(oldPassword);
        ResponseEntity<DefaultOAuth2AccessToken> resp3 =
            UserUtility.usernamePwdLogin(user);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());
        //new password should work
        user.setPassword(newPassword);
        ResponseEntity<DefaultOAuth2AccessToken> resp4 =
            UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.OK, resp4.getStatusCode());
    }

    private void update_pwd_common(User user, String newPassword, String token) {
        UserUpdatePwd updatePwd = new UserUpdatePwd();
        updatePwd.setCurrentPwd(user.getPassword());
        updatePwd.setPassword(newPassword);
        String url = HttpUtility.getAccessUrl("/users" + "/pwd");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<UserUpdatePwd> request = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    @Test
    public void user_can_update_prefer_language() {
        User user = UserUtility.createEmailPwdUser();
        String token = UserUtility.emailPwdLogin(user);
        user.setLanguage("MANDARIN");

        HttpHeaders headers = new HttpHeaders();
        String url = HttpUtility.getAccessUrl("/users" + "/profile" + "/language");
        String profileUrl = HttpUtility.getAccessUrl("/users" + "/profile");
        headers.setBearerAuth(token);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ResponseEntity<User> exchange2 =
            TestContext.getRestTemplate().exchange(profileUrl, HttpMethod.GET, request, User.class);
        Assertions.assertEquals(user.getLanguage(), exchange2.getBody().getLanguage());

    }

    @Test
    public void user_can_view_profile_email_pwd() {
        User user = UserUtility.randomEmailPwdUser();
        String token = UserUtility.emailPwdLogin(user);
        ResponseEntity<User> exchange = getMyProfile(token);
        Assertions.assertNotNull(exchange.getBody().getEmail());
    }

    @Test
    public void user_can_view_profile_email_code() {
        User user = UserUtility.randomEmailOnlyUser();
        String token = UserUtility.emailCodeLogin(user).getBody().getValue();
        ResponseEntity<User> exchange = getMyProfile(token);
        Assertions.assertNotNull(exchange.getBody().getEmail());
    }

    @Test
    public void user_can_view_profile_mobile_pwd() {
        User user = UserUtility.randomMobilePwdUser();
        String token = UserUtility.mobilePwdLogin(user).getBody().getValue();
        ResponseEntity<User> exchange = getMyProfile(token);
        Assertions.assertNotNull(exchange.getBody().getCountryCode());
        Assertions.assertNotNull(exchange.getBody().getMobileNumber());
    }

    @Test
    public void user_can_view_profile_mobile_code() {
        User user = UserUtility.randomMobileOnlyUser();
        String token = UserUtility.mobileCodeLogin(user).getBody().getValue();
        ResponseEntity<User> exchange = getMyProfile(token);
        Assertions.assertNotNull(exchange.getBody().getCountryCode());
        Assertions.assertNotNull(exchange.getBody().getMobileNumber());
    }

    @Test
    public void user_can_view_profile_username_pwd() {
        User user = UserUtility.randomUsernamePwdUser();
        String token = UserUtility.usernamePwdLogin(user).getBody().getValue();
        ResponseEntity<User> exchange = getMyProfile(token);
        Assertions.assertNotNull(exchange.getBody().getUsername());
    }

    @Test
    public void user_can_update_avatar() throws FileNotFoundException {
        //created user has no avatar by default
        String url = HttpUtility.getAccessUrl("/users" + "/profile/avatar");
        User user = UserUtility.createEmailPwdUser();
        String bearer = UserUtility.emailPwdLogin(user);
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(bearer);
        HttpEntity<Void> objectHttpEntity = new HttpEntity<>(headers2);
        ResponseEntity<String> exchange2 =
            TestContext.getRestTemplate()
                .exchange(url, HttpMethod.GET, objectHttpEntity, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //add avatar
        File file = ResourceUtils.getFile("classpath:test-avatar.jpg");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body
            = new LinkedMultiValueMap<>();
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        body.add("file", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //get avatar
        ResponseEntity<String> exchange23 =
            TestContext.getRestTemplate()
                .exchange(url, HttpMethod.GET, objectHttpEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, exchange23.getStatusCode());
    }

    @Test
    public void user_can_add_then_remove_email() {
        User user = UserUtility.randomUsernamePwdUser();
        String token = UserUtility.usernamePwdLogin(user).getBody().getValue();
        String url = HttpUtility.getAccessUrl("/users" + "/profile" + "/email");
        String profileUrl = HttpUtility.getAccessUrl("/users" + "/profile");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        user.setEmail(RandomUtility.randomEmail());
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Void.class);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<User> exchange2 =
            TestContext.getRestTemplate().exchange(profileUrl, HttpMethod.GET, request, User.class);
        Assertions.assertEquals(user.getEmail(), exchange2.getBody().getEmail());

        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange3.getStatusCode());

        ResponseEntity<User> exchange4 =
            TestContext.getRestTemplate().exchange(profileUrl, HttpMethod.GET, request, User.class);
        Assertions.assertNull(exchange4.getBody().getEmail());
    }

    @Test
    public void user_can_add_then_remove_mobile() {
        User user = UserUtility.randomUsernamePwdUser();
        String token = UserUtility.usernamePwdLogin(user).getBody().getValue();
        String url = HttpUtility.getAccessUrl("/users" + "/profile" + "/mobile");
        String profileUrl = HttpUtility.getAccessUrl("/users" + "/profile");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        user.setMobileNumber(RandomUtility.randomMobileNumber());
        user.setCountryCode("86");
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Void.class);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<User> exchange2 =
            TestContext.getRestTemplate().exchange(profileUrl, HttpMethod.GET, request, User.class);
        Assertions.assertEquals(user.getMobileNumber(), exchange2.getBody().getMobileNumber());


        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, Void.class);
        Assertions.assertEquals(HttpStatus.OK, exchange3.getStatusCode());

        ResponseEntity<User> exchange4 =
            TestContext.getRestTemplate().exchange(profileUrl, HttpMethod.GET, request, User.class);
        Assertions.assertNull(exchange4.getBody().getMobileNumber());

    }


    @Test
    public void user_can_add_then_remove_username() {
        User user = UserUtility.randomMobileOnlyUser();
        String token = UserUtility.mobileCodeLogin(user).getBody().getValue();
        String url = HttpUtility.getAccessUrl("/users" + "/profile" + "/username");
        String profileUrl = HttpUtility.getAccessUrl("/users" + "/profile");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        user.setUsername(RandomUtility.randomUsername());
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Void.class);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<User> exchange2 =
            TestContext.getRestTemplate().exchange(profileUrl, HttpMethod.GET, request, User.class);
        Assertions.assertEquals(user.getUsername(), exchange2.getBody().getUsername());

        ResponseEntity<User> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, User.class);
        Assertions.assertEquals(HttpStatus.OK, exchange3.getStatusCode());

        ResponseEntity<User> exchange4 =
            TestContext.getRestTemplate().exchange(profileUrl, HttpMethod.GET, request, User.class);
        Assertions.assertNull(exchange4.getBody().getUsername());

    }


}