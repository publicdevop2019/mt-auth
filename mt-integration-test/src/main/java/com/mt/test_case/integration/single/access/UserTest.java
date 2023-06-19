package com.mt.test_case.integration.single.access;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.CommonTest;
import com.mt.test_case.helper.pojo.ForgetPasswordRequest;
import com.mt.test_case.helper.pojo.User;
import com.mt.test_case.helper.pojo.UserUpdatePwd;
import com.mt.test_case.helper.utility.OAuth2Utility;
import com.mt.test_case.helper.utility.RandomUtility;
import com.mt.test_case.helper.utility.TestContext;
import com.mt.test_case.helper.utility.UrlUtility;
import com.mt.test_case.helper.utility.UserUtility;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

@RunWith(SpringRunner.class)
@Slf4j
public class UserTest extends CommonTest {

    @Test
    public void should_not_able_to_create_user_with_user_name_not_email() {
        User user =
            UserUtility.userCreateDraftObj(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        ResponseEntity<Void> user1 = UserUtility.register(user);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, user1.getStatusCode());

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

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //login
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = UserUtility
            .login(forgetPasswordRequest.getEmail(),
                forgetPasswordRequest.getNewPassword());
        Assert.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());

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

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> resp3 =
            UserUtility.login(user.getEmail(), oldPassword);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> resp4 =
            UserUtility.login(user.getEmail(), updatePwd.getPassword());

        Assert.assertEquals(HttpStatus.OK, resp4.getStatusCode());

    }

    @Test
    public void create_pending_user() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<Void> pendingUser = UserUtility.createPendingUser(user);
        Assert.assertEquals(HttpStatus.OK, pendingUser.getStatusCode());
    }

    @Test
    public void register_new_user() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<Void> register = UserUtility.register(user);
        Assert.assertEquals(HttpStatus.OK, register.getStatusCode());
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

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ResponseEntity<User> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.GET, request, User.class);
        Assert.assertEquals(user.getUsername(), exchange2.getBody().getUsername());

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

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertNotNull(exchange.getBody().getEmail());

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
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //get avatar
        ResponseEntity<String> exchange23 =
            TestContext.getRestTemplate()
                .exchange(url, HttpMethod.GET, objectHttpEntity, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange23.getStatusCode());
    }

    @Test
    public void validation_create_avatar() throws FileNotFoundException {
        //created user
        String url = UrlUtility.getAccessUrl("/users" + "/profile/avatar");
        User user = UserUtility.createUser();
        String bearer = UserUtility.login(user);
        //type
        File file = ResourceUtils.getFile("classpath:test-avatar.txt");
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
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //max size
        File file1 = ResourceUtils.getFile("classpath:test-avatar-1m-plus.jpg");
        MultiValueMap<String, Object> body1
            = new LinkedMultiValueMap<>();
        FileSystemResource fileSystemResource1 = new FileSystemResource(file1);
        body.add("file", fileSystemResource1);
        HttpEntity<MultiValueMap<String, Object>> request1 = new HttpEntity<>(body1, headers);
        ResponseEntity<Void> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request1, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        body.add("file", "");
        HttpEntity<MultiValueMap<String, Object>> request2 = new HttpEntity<>(body1, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request2, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //null
        body.add("file", null);
        HttpEntity<MultiValueMap<String, Object>> request3 = new HttpEntity<>(body1, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request3, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
    }

    @Test
    public void validation_pending_user_email() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<Void> pendingUser = UserUtility.createPendingUser(user);
        Assert.assertEquals(HttpStatus.OK, pendingUser.getStatusCode());
        //blank
        user.setEmail(" ");
        ResponseEntity<Void> response = UserUtility.createPendingUser(user);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        user.setEmail("");
        ResponseEntity<Void> response1 = UserUtility.createPendingUser(user);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max length
        user.setEmail(RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
            RandomUtility.randomEmail());
        ResponseEntity<Void> response3 = UserUtility.createPendingUser(user);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //invalid format
        user.setEmail(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> response4 = UserUtility.createPendingUser(user);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_update_user_profile_name() {
        User user = UserUtility.createUser();
        String url = UrlUtility.getAccessUrl("/users" + "/profile");
        String bearer = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        //null
        user.setUsername(null);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //blank
        user.setUsername(" ");
        HttpEntity<User> request2 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request2, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        user.setUsername("");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //min length
        user.setUsername("1");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //max length
        user.setUsername(
            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //invalid char
        user.setUsername(
            "<0123456789");
        HttpEntity<User> request6 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange6 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request6, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
    }

    @Test
    public void validation_update_user_profile_country_code() {
        User user = UserUtility.createUser();
        String url = UrlUtility.getAccessUrl("/users" + "/profile");
        String bearer = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        //both null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //one of is null
        user.setCountryCode("1");
        user.setMobileNumber(null);
        HttpEntity<User> request2 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request2, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        user.setCountryCode("");
        user.setMobileNumber("1231231234");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //blank
        user.setCountryCode(" ");
        user.setMobileNumber("1231231234");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid value
        user.setCountryCode("123123123");
        user.setMobileNumber("1231231234");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
    }

    @Test
    public void validation_update_user_profile_mobile() {
        User user = UserUtility.createUser();
        String url = UrlUtility.getAccessUrl("/users" + "/profile");
        String bearer = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        //both null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //one of is null
        user.setCountryCode(null);
        user.setMobileNumber("1231231234");
        HttpEntity<User> request2 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request2, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        user.setCountryCode("1");
        user.setMobileNumber("");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //blank
        user.setCountryCode("1");
        user.setMobileNumber(" ");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid format
        user.setCountryCode("1");
        user.setMobileNumber("123");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
    }

    @Test
    public void validation_update_user_profile_language() {
        User user = UserUtility.createUser();
        String url = UrlUtility.getAccessUrl("/users" + "/profile");
        String bearer = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        //null
        user.setLanguage(null);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //empty
        user.setLanguage("");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //blank
        user.setLanguage(" ");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid value
        user.setLanguage("KOREAN");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //valid number value
        user.setLanguage(0);
        HttpEntity<User> request6 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> request7 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request6, Void.class);
        Assert.assertEquals(HttpStatus.OK, request7.getStatusCode());
    }

    @Test
    public void validation_create_user_email() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(
                AppConstant.CLIENT_ID_REGISTER_ID, AppConstant.EMPTY_CLIENT_SECRET);
        UserUtility.createPendingUser(user);
        //null
        user.setEmail(null);
        ResponseEntity<Void> response =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        user.setEmail(" ");
        ResponseEntity<Void> response1 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setEmail("");
        ResponseEntity<Void> response2 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //max length
        user.setEmail(RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
            RandomUtility.randomEmail());
        ResponseEntity<Void> response4 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid format
        user.setEmail(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> response5 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //mismatch from pending
        user.setEmail(RandomUtility.randomEmail());
        ResponseEntity<Void> response6 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_user_activation_code() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(
                AppConstant.CLIENT_ID_REGISTER_ID, AppConstant.EMPTY_CLIENT_SECRET);
        UserUtility.createPendingUser(user);
        //null
        ResponseEntity<Void> response =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(), null);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        ResponseEntity<Void> response1 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(), " ");
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        ResponseEntity<Void> response2 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(), "");
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        ResponseEntity<Void> response3 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(), "1");
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        ResponseEntity<Void> response4 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(),
                "012345678901234567890123456789012345678901234567890123456789");
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        ResponseEntity<Void> response5 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(),
                "abcdef");
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //mismatch value
        ResponseEntity<Void> response6 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(),
                "654322");
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_create_user_country_code() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(
                AppConstant.CLIENT_ID_REGISTER_ID, AppConstant.EMPTY_CLIENT_SECRET);
        UserUtility.createPendingUser(user);
        //null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        ResponseEntity<Void> response =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //one of null
        user.setCountryCode(null);
        user.setMobileNumber("1231231234");
        ResponseEntity<Void> response0 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response0.getStatusCode());
        //blank
        user.setCountryCode(" ");
        ResponseEntity<Void> response1 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setCountryCode("");
        ResponseEntity<Void> response2 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //invalid value
        user.setCountryCode("7788");
        user.setMobileNumber("1231231234");
        ResponseEntity<Void> response3 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_create_user_mobile() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(
                AppConstant.CLIENT_ID_REGISTER_ID, AppConstant.EMPTY_CLIENT_SECRET);
        UserUtility.createPendingUser(user);
        //null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        ResponseEntity<Void> response =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //one of null
        user.setCountryCode("1");
        user.setMobileNumber(null);
        ResponseEntity<Void> response0 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response0.getStatusCode());
        //blank
        user.setCountryCode("1");
        user.setMobileNumber(" ");
        ResponseEntity<Void> response1 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setCountryCode("1");
        user.setMobileNumber("");
        ResponseEntity<Void> response2 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        user.setCountryCode("1");
        user.setMobileNumber("123");
        ResponseEntity<Void> response3 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        user.setCountryCode("1");
        user.setMobileNumber("1231231234123123");
        ResponseEntity<Void> response4 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid format
        user.setCountryCode("1");
        user.setMobileNumber("abcabcabcd");
        ResponseEntity<Void> response5 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_create_password() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(
                AppConstant.CLIENT_ID_REGISTER_ID, AppConstant.EMPTY_CLIENT_SECRET);
        UserUtility.createPendingUser(user);
        //null
        user.setPassword(null);
        ResponseEntity<Void> response =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        user.setPassword(" ");
        ResponseEntity<Void> response2 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        user.setPassword("");
        ResponseEntity<Void> response3 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //min length
        user.setPassword("Pa1!");
        ResponseEntity<Void> response4 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //max length
        user.setPassword("Password1!0123456789012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid format, missing number
        user.setPassword("Password!");
        ResponseEntity<Void> response6 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //invalid format, missing letter
        user.setPassword("123123123!");
        ResponseEntity<Void> response8 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
        //invalid format, missing special char
        user.setPassword("Password1");
        ResponseEntity<Void> response9 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }


    @Test
    public void validation_forget_pwd_email() {
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(
                AppConstant.CLIENT_ID_REGISTER_ID, AppConstant.EMPTY_CLIENT_SECRET);
        String value = registerTokenResponse.getBody().getValue();
        String url = UrlUtility.getAccessUrl("/users" + "/forgetPwd");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(value);
        User user = UserUtility.createRandomUserObj();
        UserUtility.register(user);
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest();
        //null
        forgetPasswordRequest.setEmail(null);
        HttpEntity<ForgetPasswordRequest> request =
            new HttpEntity<>(forgetPasswordRequest, headers);
        ResponseEntity<Object> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //blank
        forgetPasswordRequest.setEmail(" ");
        HttpEntity<ForgetPasswordRequest> request1 =
            new HttpEntity<>(forgetPasswordRequest, headers);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request1, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        forgetPasswordRequest.setEmail("");
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(forgetPasswordRequest, headers);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request2, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //max length
        forgetPasswordRequest.setEmail(
            RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
                RandomUtility.randomEmail());
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, headers);
        ResponseEntity<Object> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request3, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //invalid format
        forgetPasswordRequest.setEmail(
            RandomUtility.randomStringWithNum());
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, headers);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request4, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
    }

    @Test
    public void validation_reset_pwd_email() {
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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String url2 = UrlUtility.getAccessUrl("/users" + "/resetPwd");
        forgetPasswordRequest.setToken("123456789");
        forgetPasswordRequest.setNewPassword(
            "P1!" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(value);

        //null
        forgetPasswordRequest.setEmail(null);
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //blank
        forgetPasswordRequest.setEmail(" ");
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request3, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        forgetPasswordRequest.setEmail("");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange3 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //max length
        forgetPasswordRequest.setEmail(
            RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
                RandomUtility.randomEmail());
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request5, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid format
        forgetPasswordRequest.setEmail(
            RandomUtility.randomStringWithNum());
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
    }

    @Test
    public void validation_reset_pwd_token() {
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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String url2 = UrlUtility.getAccessUrl("/users" + "/resetPwd");
        forgetPasswordRequest.setToken("123456789");
        forgetPasswordRequest.setNewPassword(
            "P1!" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(value);

        //null
        forgetPasswordRequest.setToken(null);
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //blank
        forgetPasswordRequest.setToken(" ");
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request3, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        forgetPasswordRequest.setToken("");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange3 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //min length
        forgetPasswordRequest.setToken("1");
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request5, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //max length
        forgetPasswordRequest.setToken("01234567890123456789");
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //invalid value
        forgetPasswordRequest.setToken("987654321");
        HttpEntity<ForgetPasswordRequest> request7 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange6 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request7, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
        //invalid value
        forgetPasswordRequest.setToken("abcdefghij");
        HttpEntity<ForgetPasswordRequest> request8 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange7 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request8, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
    }

    @Test
    public void validation_reset_pwd_password() {
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
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        String url2 = UrlUtility.getAccessUrl("/users" + "/resetPwd");
        forgetPasswordRequest.setToken("123456789");
        forgetPasswordRequest.setNewPassword(
            "P1!" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(value);

        //null
        forgetPasswordRequest.setNewPassword(null);
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request2, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //blank
        forgetPasswordRequest.setNewPassword(" ");
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request3, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        forgetPasswordRequest.setNewPassword("");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //min length
        forgetPasswordRequest.setNewPassword("Pa1!");
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request5, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //max length
        user.setPassword("Password1!0123456789012345678901234567890123456789");
        forgetPasswordRequest.setNewPassword("Pa1!");
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange6 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
        //invalid format, missing number
        user.setPassword("Password!");
        HttpEntity<ForgetPasswordRequest> request7 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange7 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request7, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
        //invalid format, missing letter
        user.setPassword("123123123!");
        HttpEntity<ForgetPasswordRequest> request9 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange9 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request9, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange9.getStatusCode());
        //invalid format, missing special char
        user.setPassword("Password1");
        HttpEntity<ForgetPasswordRequest> request10 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange10 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request10, Object.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange10.getStatusCode());
    }

    @Test
    public void validation_update_pwd_new_password() {
        String url = UrlUtility.getAccessUrl("/users" + "/pwd");
        User user = UserUtility.createRandomUserObj();
        UserUtility.register(user);
        UserUpdatePwd updatePwd = new UserUpdatePwd();
        updatePwd.setCurrentPwd(user.getPassword());
        //login
        String bearer = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        //null
        updatePwd.setPassword(null);
        HttpEntity<UserUpdatePwd> request = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //blank
        updatePwd.setPassword(" ");
        HttpEntity<UserUpdatePwd> request1 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request1, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        updatePwd.setPassword("");
        HttpEntity<UserUpdatePwd> request2 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request2, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //min length
        updatePwd.setPassword("Pa1!");
        HttpEntity<UserUpdatePwd> request3 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //max length
        updatePwd.setPassword("Password1!01234567890123456789");
        HttpEntity<UserUpdatePwd> request4 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid format, missing number
        updatePwd.setPassword("Password!");
        HttpEntity<UserUpdatePwd> request5 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //invalid format, missing letter
        updatePwd.setPassword("0123456789!");
        HttpEntity<UserUpdatePwd> request7 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange7 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request7, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
        //invalid format, missing special char
        updatePwd.setPassword("Password1");
        HttpEntity<UserUpdatePwd> request8 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange8 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request8, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange8.getStatusCode());
    }

    @Test
    public void validation_update_pwd_current_password() {
        String url = UrlUtility.getAccessUrl("/users" + "/pwd");
        User user = UserUtility.createRandomUserObj();
        UserUtility.register(user);
        UserUpdatePwd updatePwd = new UserUpdatePwd();
        updatePwd.setCurrentPwd(user.getPassword());
        //login
        String bearer = UserUtility.login(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        //null
        updatePwd.setCurrentPwd(null);
        HttpEntity<UserUpdatePwd> request = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //blank
        updatePwd.setCurrentPwd(" ");
        HttpEntity<UserUpdatePwd> request1 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request1, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        updatePwd.setCurrentPwd("");
        HttpEntity<UserUpdatePwd> request2 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request2, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //min length
        updatePwd.setCurrentPwd("Pa1!");
        HttpEntity<UserUpdatePwd> request3 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //max length
        updatePwd.setCurrentPwd("Password1!01234567890123456789");
        HttpEntity<UserUpdatePwd> request4 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid format, missing number
        updatePwd.setCurrentPwd("Password!");
        HttpEntity<UserUpdatePwd> request5 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //invalid format, missing letter
        updatePwd.setCurrentPwd("0123456789!");
        HttpEntity<UserUpdatePwd> request7 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange7 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request7, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
        //invalid format, missing special char
        updatePwd.setCurrentPwd("Password1");
        HttpEntity<UserUpdatePwd> request8 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange8 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request8, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange8.getStatusCode());
    }
}