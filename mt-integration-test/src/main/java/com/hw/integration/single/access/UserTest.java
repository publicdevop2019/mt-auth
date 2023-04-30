package com.hw.integration.single.access;

import static com.hw.helper.AppConstant.CLIENT_ID_REGISTER_ID;
import static com.hw.helper.AppConstant.EMPTY_CLIENT_SECRET;
import static com.hw.helper.utility.TestContext.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hw.helper.AppConstant;
import com.hw.helper.ForgetPasswordRequest;
import com.hw.helper.PendingUser;
import com.hw.helper.User;
import com.hw.helper.UserUpdatePwd;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UrlUtility;
import com.hw.helper.utility.UserUtility;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class UserTest  extends CommonTest {
    public static final String USER_MGMT = "/mgmt/users";

    @Test
    public void should_not_able_to_create_user_with_user_name_not_email() {
        User user =
            UserUtility.userCreateDraftObj(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        ResponseEntity<Void> user1 = UserUtility.register(user);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, user1.getStatusCode());

    }

    @Test
    public void cannot_update_user_password_without_current_pwd() throws JsonProcessingException {
        User user = UserUtility.createUserObj();
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
    public void user_forget_password() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(
                AppConstant.CLIENT_ID_REGISTER_ID, AppConstant.EMPTY_CLIENT_SECRET);
        String value = registerTokenResponse.getBody().getValue();
        User user = UserUtility.createUserObj();
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
        User user = UserUtility.createUserObj();
        UserUpdatePwd resourceOwnerUpdatePwd = new UserUpdatePwd();
        resourceOwnerUpdatePwd.setCurrentPwd(user.getPassword());
        resourceOwnerUpdatePwd.setEmail(user.getEmail());
        resourceOwnerUpdatePwd.setPassword(
            "P1!" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        UserUtility.register(user);
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
    public void create_pending_user(){
        User user = UserUtility.createUserObj();
        ResponseEntity<Void> pendingUser = UserUtility.createPendingUser(user);
        Assert.assertEquals(HttpStatus.OK, pendingUser.getStatusCode());
    }

    @Test
    public void register_new_user(){
        User user = UserUtility.createUserObj();
        ResponseEntity<Void> register = UserUtility.register(user);
        Assert.assertEquals(HttpStatus.OK, register.getStatusCode());
    }

    @Test
    public void user_can_update_profile(){
    }
    @Test
    public void user_can_update_avatar(){
    }

}