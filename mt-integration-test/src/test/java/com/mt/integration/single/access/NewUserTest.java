package com.mt.integration.single.access;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.ForgetPasswordRequest;
import com.mt.helper.pojo.User;
import com.mt.helper.pojo.UserUpdatePwd;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;
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
public class NewUserTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void register_new_user_with_mobile_and_code() {

    }
    @Test
    public void should_not_register_new_user_with_invalid_mobile_and_code() {

    }
    @Test
    public void should_not_register_new_user_with_invalid_email_and_code() {

    }

    @Test
    public void register_new_user_with_email_and_code() {

    }
    @Test
    public void login_with_mobile_and_code() {

    }
    @Test
    public void login_with_email_and_code() {

    }
    @Test
    public void login_with_mobile_and_pwd() {

    }
    @Test
    public void login_with_email_and_pwd() {

    }
    @Test
    public void login_with_username_and_pwd() {

    }
    @Test
    public void login_with_username_and_pwd_but_case_sensitive() {

    }

    @Test
    public void register_new_user_with_mobile_and_pwd() {

    }

    @Test
    public void register_new_user_with_email_and_pwd() {

    }

    @Test
    public void register_new_user_with_username_and_pwd() {

    }

    @Test
    public void should_not_able_to_send_activation_code_when_not_email() {

    }
    @Test
    public void should_not_able_to_send_activation_code_when_not_mobile() {

    }

    @Test
    public void cannot_update_user_password_without_current_pwd() throws JsonProcessingException {
        User user = UserUtility.createRandomUserObj();
        UserUtility.register(user);
        //Location is not used in this case, root/admin/user can only update their password
        String url = HttpUtility.getAccessUrl("/users" + "/pwd");
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
    public void user_with_mobile_no_pwd_forget_password() {

    }
    @Test
    public void user_with_email_no_pwd_forget_password() {

    }
    @Test
    public void user_with_mobile_and_pwd_forget_password() {

    }
    @Test
    public void user_with_email_and_pwd_forget_password() {

    }
    @Test
    public void user_with_username_and_pwd_forget_password() {

    }

    @Test
    public void update_user_password_with_current_pwd() {
        User user = UserUtility.createRandomUserObj();
        UserUpdatePwd updatePwd = new UserUpdatePwd();
        updatePwd.setCurrentPwd(user.getPassword());
        updatePwd.setEmail(user.getEmail());
        updatePwd.setPassword(RandomUtility.randomPassword());
        UserUtility.register(user);
        //Location is not used in this case, root/admin/user can only update their password
        String url = HttpUtility.getAccessUrl("/users" + "/pwd");
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
    public void send_mobile_verification_code() {
    }

    @Test
    public void send_email_verification_code() {
    }

    @Test
    public void user_can_add_or_update_profile_mobile() {

    }
    @Test
    public void user_can_add_or_update_profile_email() {

    }
    @Test
    public void user_can_add_or_update_profile_username() {

    }
    @Test
    public void user_can_update_preference_when_email_and_mobile_both_exist() {

    }

    @Test
    public void user_can_view_profile() {
        User user = UserUtility.createUser();
        String url = HttpUtility.getAccessUrl("/users" + "/profile");
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
        String url = HttpUtility.getAccessUrl("/users" + "/profile/avatar");
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