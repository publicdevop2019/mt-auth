package com.hw.integration.identityaccess.oauth2;

import static com.hw.helper.utility.TestContext.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hw.helper.AccessConstant;
import com.hw.helper.AppConstant;
import com.hw.helper.ForgetPasswordRequest;
import com.hw.helper.SumTotal;
import com.hw.helper.User;
import com.hw.helper.UserUpdatePwd;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class UserTest {
    public static final String USER_MGMT = "/mgmt/users";
    private static final String root_index = "0U8AZTODP4H0";
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            log.error("test failed, method {}, id {}", description.getMethodName(),
                TestContext.getTestId());
        }
    };

    @Before
    public void setUp() {
        TestContext.init();
        log.info("test id {}", TestContext.getTestId());
    }

    @Test
    public void should_not_able_to_create_user_with_user_name_not_email() {
        User user =
            UserUtility.userCreateDraft(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        ResponseEntity<DefaultOAuth2AccessToken> user1 = UserUtility.register(user);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, user1.getStatusCode());

    }

    @Test
    public void update_user_password_without_current_pwd() throws JsonProcessingException {
        User user = UserUtility.createUser();
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
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Object.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void forget_password() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(
                AppConstant.CLIENT_ID_REGISTER_ID, AppConstant.EMPTY_CLIENT_SECRET);
        String value = registerTokenResponse.getBody().getValue();
        User user = UserUtility.createUser();
        UserUtility.register(user);
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest();
        forgetPasswordRequest.setEmail(user.getEmail());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(value);
        String s1 = mapper.writeValueAsString(forgetPasswordRequest);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        String url = UrlUtility.getAccessUrl("/users" + "/forgetPwd");
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        forgetPasswordRequest.setToken("123456789");
        forgetPasswordRequest.setNewPassword(
            "P1!" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        String s2 = mapper.writeValueAsString(forgetPasswordRequest);
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(value);
        HttpEntity<String> request2 = new HttpEntity<>(s2, header2);
        String url2 = UrlUtility.getAccessUrl("/users" + "/resetPwd");
        TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //login
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility
            .login(forgetPasswordRequest.getEmail(),
                forgetPasswordRequest.getNewPassword());
        Assert.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());

    }

    @Test
    public void update_user_password_with_current_pwd() throws JsonProcessingException {
        User user = UserUtility.createUser();
        UserUpdatePwd resourceOwnerUpdatePwd = new UserUpdatePwd();
        resourceOwnerUpdatePwd.setCurrentPwd(user.getPassword());
        resourceOwnerUpdatePwd.setEmail(user.getEmail());
        resourceOwnerUpdatePwd.setPassword(
            "P1!" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        ResponseEntity<DefaultOAuth2AccessToken> createResponse = UserUtility.register(user);
        //Location is not used in this case, root/admin/user can only update their password
        String url = UrlUtility.getAccessUrl("/users" + "/pwd");
        //Login
        String oldPassword = user.getPassword();
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            UserUtility.login(user.getEmail(), user.getPassword());
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);

        String s1 = mapper.writeValueAsString(resourceOwnerUpdatePwd);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Object.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> resp3 =
            UserUtility.login(user.getEmail(), oldPassword);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> resp4 =
            UserUtility.login(user.getEmail(), resourceOwnerUpdatePwd.getPassword());

        Assert.assertEquals(HttpStatus.OK, resp4.getStatusCode());

    }

    @Test
    public void read_all_users_with_root_account() {
        String url = UrlUtility.getAccessUrl(USER_MGMT);
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotal<User>> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
            });

        Assert.assertNotSame(0, exchange.getBody().getData().size());
    }

    @Test
    public void should_not_able_to_update_user_authority_to_root_with_user_account()
        throws JsonProcessingException {
        User user = UserUtility.createUser();

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_USER, AppConstant.ACCOUNT_PASSWORD_USER);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);

        user.setGrantedAuthorities(
            List.of(AccessConstant.ADMIN_USER_ID, AccessConstant.USER_USER_ID));
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        String url =
            UrlUtility.getAccessUrl(USER_MGMT + "/" + root_index);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

    }

    @Test
    public void lock_then_unlock_user() {
        User user = UserUtility.createUser();
        ResponseEntity<DefaultOAuth2AccessToken> createResp = UserUtility.register(user);
        String s = createResp.getHeaders().getLocation().toString();

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        user.setGrantedAuthorities(List.of(AccessConstant.USER_USER_ID));
        user.setLocked(true);
        user.setVersion(0);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        String url = UrlUtility.getAccessUrl(USER_MGMT + "/" + s);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //login to verify account has been locked
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 =
            UserUtility.login(user.getEmail(), user.getPassword());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tokenResponse1.getStatusCode());

        user.setLocked(false);
        user.setVersion(1);
        HttpEntity<User> request22 = new HttpEntity<>(user, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange22 = TestContext.getRestTemplate()
            .exchange(url, HttpMethod.PUT, request22, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.OK, exchange22.getStatusCode());
        //login to verify account has been unlocked
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 =
            UserUtility.login(user.getEmail(), user.getPassword());
        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());
    }

    @Test
    public void delete_user() {
        User user = UserUtility.createUser();
        ResponseEntity<DefaultOAuth2AccessToken> user1 = UserUtility.register(user);

        String s = user1.getHeaders().getLocation().toString();
        String url = UrlUtility.getAccessUrl(USER_MGMT + "/" + s);

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 =
            UserUtility.login(user.getEmail(), user.getPassword());

        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getBody().getValue());
        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, Object.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse123 =
            UserUtility.login(user.getEmail(), user.getPassword());

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse123.getStatusCode());

    }

    @Test
    public void should_not_able_to_delete_root_user() {

        String url =
            UrlUtility.getAccessUrl(USER_MGMT + "/" + root_index);

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);

        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());
        //try w root
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility.login(
            AppConstant.ACCOUNT_USERNAME_ADMIN, AppConstant.ACCOUNT_PASSWORD_ADMIN);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getBody().getValue());
        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.DELETE, request, Object.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());

    }

}