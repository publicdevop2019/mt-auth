package com.mt.integration.single.access;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.ForgetPasswordRequest;
import com.mt.helper.pojo.User;
import com.mt.helper.pojo.UserUpdatePwd;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UrlUtility;
import com.mt.helper.utility.UserUtility;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class UserTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach(log);
    }

    @Test
    public void should_not_able_to_create_user_with_user_name_not_email() {
        User user =
            UserUtility.userCreateDraftObj(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        ResponseEntity<Void> user1 = UserUtility.register(user);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, user1.getStatusCode());

    }

    @Test
    public void cannot_update_user_password_without_current_pwd() throws JsonProcessingException {
        User user = UserUtility.createRandomUserObj();
        UserUtility.register(user);
        //Location is not used in this case, root/admin/user can only update their password
        String url = UrlUtility.getAccessUrl("/users" + "/pwd");
        String newPassword = UUID.randomUUID().toString().replace("-", "");
        //Login
        ResponseEntity<DefaultOAuth2AccessToken> login =
            UserUtility.login(user.getEmail(), user.getPassword());
        String bearer = login.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        user.setPassword(newPassword);
        String s1 = TestContext.mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Object.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void user_forget_password() {
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(
                AppConstant.CLIENT_ID_REGISTER_ID, AppConstant.EMPTY_CLIENT_SECRET);
        String value = registerTokenResponse.getBody().getValue();
        User user = UserUtility.createRandomUserObj();
        UserUtility.register(user);
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest();
        forgetPasswordRequest.setEmail(user.getEmail());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(value);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(forgetPasswordRequest, headers);
        String url = UrlUtility.getAccessUrl("/users" + "/forgetPwd");
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        forgetPasswordRequest.setToken("123456789");
        forgetPasswordRequest.setNewPassword(
            "P1!" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(value);
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        String url2 = UrlUtility.getAccessUrl("/users" + "/resetPwd");
        TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //login
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility
            .login(forgetPasswordRequest.getEmail(),
                forgetPasswordRequest.getNewPassword());
        Assertions.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());

    }

    @Test
    public void update_user_password_with_current_pwd() throws JsonProcessingException {
        User user = UserUtility.createRandomUserObj();
        UserUpdatePwd updatePwd = new UserUpdatePwd();
        updatePwd.setCurrentPwd(user.getPassword());
        updatePwd.setEmail(user.getEmail());
        updatePwd.setPassword(RandomUtility.randomPassword());
        UserUtility.register(user);
        //Location is not used in this case, root/admin/user can only update their password
        String url = UrlUtility.getAccessUrl("/users" + "/pwd");
        //Login
        String oldPassword = user.getPassword();
        String bearer = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        HttpEntity<UserUpdatePwd> request = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> resp3 =
            UserUtility.login(user.getEmail(), oldPassword);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> resp4 =
            UserUtility.login(user.getEmail(), updatePwd.getPassword());

        Assertions.assertEquals(HttpStatus.OK, resp4.getStatusCode());

    }

    @Test
    public void create_pending_user() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<Void> pendingUser = UserUtility.createPendingUser(user);
        Assertions.assertEquals(HttpStatus.OK, pendingUser.getStatusCode());
    }

    @Test
    public void register_new_user() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<Void> register = UserUtility.register(user);
        Assertions.assertEquals(HttpStatus.OK, register.getStatusCode());
    }

    @Test
    public void user_can_update_profile() {
        User user = UserUtility.createUser();
        String url = UrlUtility.getAccessUrl("/users" + "/profile");
        String bearer = UserUtility.login(user);
        user.setUsername(RandomUtility.randomStringWithNum().substring(0, 10));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ResponseEntity<User> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, User.class);
        Assertions.assertEquals(user.getUsername(), exchange2.getBody().getUsername());

    }

    @Test
    public void user_can_view_profile() {
        User user = UserUtility.createUser();
        String url = UrlUtility.getAccessUrl("/users" + "/profile");
        String bearer = UserUtility.login(user);
        user.setUsername(RandomUtility.randomStringWithNum());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<User> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, User.class);

        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assertions.assertNotNull(exchange.getBody().getEmail());

    }

    @Test
    public void user_can_update_avatar() throws FileNotFoundException {
        //created user has no avatar by default
        String url = UrlUtility.getAccessUrl("/users" + "/profile/avatar");
        User user = UserUtility.createUser();
        String bearer = UserUtility.login(user);
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


}