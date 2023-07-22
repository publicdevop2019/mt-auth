package com.mt.integration.single.access.validation;

import com.mt.helper.AppConstant;
import com.mt.helper.CommonTest;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
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
@Tag("validation")

@ExtendWith(SpringExtension.class)
@Slf4j
public class UserValidationTest extends CommonTest {
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //max size
        File file1 = ResourceUtils.getFile("classpath:test-avatar-1m-plus.jpg");
        MultiValueMap<String, Object> body1
            = new LinkedMultiValueMap<>();
        FileSystemResource fileSystemResource1 = new FileSystemResource(file1);
        body.add("file", fileSystemResource1);
        HttpEntity<MultiValueMap<String, Object>> request1 = new HttpEntity<>(body1, headers);
        ResponseEntity<Void> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request1, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        body.add("file", "");
        HttpEntity<MultiValueMap<String, Object>> request2 = new HttpEntity<>(body1, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request2, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //null
        body.add("file", null);
        HttpEntity<MultiValueMap<String, Object>> request3 = new HttpEntity<>(body1, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request3, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
    }

    @Test
    public void validation_pending_user_email() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<Void> pendingUser = UserUtility.createPendingUser(user);
        Assertions.assertEquals(HttpStatus.OK, pendingUser.getStatusCode());
        //blank
        user.setEmail(" ");
        ResponseEntity<Void> response = UserUtility.createPendingUser(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        user.setEmail("");
        ResponseEntity<Void> response1 = UserUtility.createPendingUser(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max length
        user.setEmail(RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
            RandomUtility.randomEmail());
        ResponseEntity<Void> response3 = UserUtility.createPendingUser(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //invalid format
        user.setEmail(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> response4 = UserUtility.createPendingUser(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //blank
        user.setUsername(" ");
        HttpEntity<User> request2 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request2, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        user.setUsername("");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //min length
        user.setUsername("1");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //max length
        user.setUsername(
            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //invalid char
        user.setUsername(
            "<0123456789");
        HttpEntity<User> request6 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange6 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request6, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //one of is null
        user.setCountryCode("1");
        user.setMobileNumber(null);
        HttpEntity<User> request2 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request2, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        user.setCountryCode("");
        user.setMobileNumber("1231231234");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //blank
        user.setCountryCode(" ");
        user.setMobileNumber("1231231234");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid value
        user.setCountryCode("123123123");
        user.setMobileNumber("1231231234");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //one of is null
        user.setCountryCode(null);
        user.setMobileNumber("1231231234");
        HttpEntity<User> request2 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request2, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        user.setCountryCode("1");
        user.setMobileNumber("");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //blank
        user.setCountryCode("1");
        user.setMobileNumber(" ");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid format
        user.setCountryCode("1");
        user.setMobileNumber("123");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        //empty
        user.setLanguage("");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //blank
        user.setLanguage(" ");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid value
        user.setLanguage("KOREAN");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //valid number value
        user.setLanguage(0);
        HttpEntity<User> request6 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> request7 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request6, Void.class);
        Assertions.assertEquals(HttpStatus.OK, request7.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        user.setEmail(" ");
        ResponseEntity<Void> response1 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setEmail("");
        ResponseEntity<Void> response2 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //max length
        user.setEmail(RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
            RandomUtility.randomEmail());
        ResponseEntity<Void> response4 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid format
        user.setEmail(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> response5 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //mismatch from pending
        user.setEmail(RandomUtility.randomEmail());
        ResponseEntity<Void> response6 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        ResponseEntity<Void> response1 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(), " ");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        ResponseEntity<Void> response2 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(), "");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        ResponseEntity<Void> response3 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(), "1");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        ResponseEntity<Void> response4 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(),
                "012345678901234567890123456789012345678901234567890123456789");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        ResponseEntity<Void> response5 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(),
                "abcdef");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //mismatch value
        ResponseEntity<Void> response6 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue(),
                "654322");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //one of null
        user.setCountryCode(null);
        user.setMobileNumber("1231231234");
        ResponseEntity<Void> response0 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response0.getStatusCode());
        //blank
        user.setCountryCode(" ");
        ResponseEntity<Void> response1 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setCountryCode("");
        ResponseEntity<Void> response2 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //invalid value
        user.setCountryCode("7788");
        user.setMobileNumber("1231231234");
        ResponseEntity<Void> response3 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //one of null
        user.setCountryCode("1");
        user.setMobileNumber(null);
        ResponseEntity<Void> response0 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response0.getStatusCode());
        //blank
        user.setCountryCode("1");
        user.setMobileNumber(" ");
        ResponseEntity<Void> response1 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setCountryCode("1");
        user.setMobileNumber("");
        ResponseEntity<Void> response2 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        user.setCountryCode("1");
        user.setMobileNumber("123");
        ResponseEntity<Void> response3 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        user.setCountryCode("1");
        user.setMobileNumber("1231231234123123");
        ResponseEntity<Void> response4 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid format
        user.setCountryCode("1");
        user.setMobileNumber("abcabcabcd");
        ResponseEntity<Void> response5 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        user.setPassword(" ");
        ResponseEntity<Void> response2 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        user.setPassword("");
        ResponseEntity<Void> response3 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //min length
        user.setPassword("Pa1!");
        ResponseEntity<Void> response4 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //max length
        user.setPassword("Password1!0123456789012345678901234567890123456789");
        ResponseEntity<Void> response5 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid format, missing number
        user.setPassword("Password!");
        ResponseEntity<Void> response6 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //invalid format, missing letter
        user.setPassword("123123123!");
        ResponseEntity<Void> response8 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
        //invalid format, missing special char
        user.setPassword("Password1");
        ResponseEntity<Void> response9 =
            UserUtility.enterActivationCode(user, registerTokenResponse.getBody().getValue());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //blank
        forgetPasswordRequest.setEmail(" ");
        HttpEntity<ForgetPasswordRequest> request1 =
            new HttpEntity<>(forgetPasswordRequest, headers);
        ResponseEntity<Object> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request1, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        forgetPasswordRequest.setEmail("");
        HttpEntity<ForgetPasswordRequest> request2 =
            new HttpEntity<>(forgetPasswordRequest, headers);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request2, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //max length
        forgetPasswordRequest.setEmail(
            RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
                RandomUtility.randomEmail());
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, headers);
        ResponseEntity<Object> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request3, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //invalid format
        forgetPasswordRequest.setEmail(
            RandomUtility.randomStringWithNum());
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, headers);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //blank
        forgetPasswordRequest.setEmail(" ");
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request3, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        forgetPasswordRequest.setEmail("");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange3 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //max length
        forgetPasswordRequest.setEmail(
            RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
                RandomUtility.randomEmail());
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request5, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid format
        forgetPasswordRequest.setEmail(
            RandomUtility.randomStringWithNum());
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //blank
        forgetPasswordRequest.setToken(" ");
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request3, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        forgetPasswordRequest.setToken("");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange3 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //min length
        forgetPasswordRequest.setToken("1");
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request5, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //max length
        forgetPasswordRequest.setToken("01234567890123456789");
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //invalid value
        forgetPasswordRequest.setToken("987654321");
        HttpEntity<ForgetPasswordRequest> request7 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange6 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request7, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
        //invalid value
        forgetPasswordRequest.setToken("abcdefghij");
        HttpEntity<ForgetPasswordRequest> request8 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange7 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request8, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //blank
        forgetPasswordRequest.setNewPassword(" ");
        HttpEntity<ForgetPasswordRequest> request3 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange2 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request3, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        forgetPasswordRequest.setNewPassword("");
        HttpEntity<ForgetPasswordRequest> request4 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange4 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request4, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //min length
        forgetPasswordRequest.setNewPassword("Pa1!");
        HttpEntity<ForgetPasswordRequest> request5 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange5 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request5, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //max length
        user.setPassword("Password1!0123456789012345678901234567890123456789");
        forgetPasswordRequest.setNewPassword("Pa1!");
        HttpEntity<ForgetPasswordRequest> request6 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange6 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request6, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
        //invalid format, missing number
        user.setPassword("Password!");
        HttpEntity<ForgetPasswordRequest> request7 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange7 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request7, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
        //invalid format, missing letter
        user.setPassword("123123123!");
        HttpEntity<ForgetPasswordRequest> request9 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange9 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request9, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange9.getStatusCode());
        //invalid format, missing special char
        user.setPassword("Password1");
        HttpEntity<ForgetPasswordRequest> request10 =
            new HttpEntity<>(forgetPasswordRequest, header2);
        ResponseEntity<Object> exchange10 =
            TestContext.getRestTemplate().exchange(url2, HttpMethod.POST, request10, Object.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange10.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //blank
        updatePwd.setPassword(" ");
        HttpEntity<UserUpdatePwd> request1 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request1, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        updatePwd.setPassword("");
        HttpEntity<UserUpdatePwd> request2 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request2, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //min length
        updatePwd.setPassword("Pa1!");
        HttpEntity<UserUpdatePwd> request3 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //max length
        updatePwd.setPassword("Password1!01234567890123456789");
        HttpEntity<UserUpdatePwd> request4 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid format, missing number
        updatePwd.setPassword("Password!");
        HttpEntity<UserUpdatePwd> request5 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //invalid format, missing letter
        updatePwd.setPassword("0123456789!");
        HttpEntity<UserUpdatePwd> request7 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange7 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request7, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
        //invalid format, missing special char
        updatePwd.setPassword("Password1");
        HttpEntity<UserUpdatePwd> request8 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange8 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request8, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange8.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //blank
        updatePwd.setCurrentPwd(" ");
        HttpEntity<UserUpdatePwd> request1 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange1 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request1, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange1.getStatusCode());
        //empty
        updatePwd.setCurrentPwd("");
        HttpEntity<UserUpdatePwd> request2 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request2, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //min length
        updatePwd.setCurrentPwd("Pa1!");
        HttpEntity<UserUpdatePwd> request3 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request3, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //max length
        updatePwd.setCurrentPwd("Password1!01234567890123456789");
        HttpEntity<UserUpdatePwd> request4 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request4, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid format, missing number
        updatePwd.setCurrentPwd("Password!");
        HttpEntity<UserUpdatePwd> request5 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request5, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //invalid format, missing letter
        updatePwd.setCurrentPwd("0123456789!");
        HttpEntity<UserUpdatePwd> request7 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange7 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request7, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange7.getStatusCode());
        //invalid format, missing special char
        updatePwd.setCurrentPwd("Password1");
        HttpEntity<UserUpdatePwd> request8 = new HttpEntity<>(updatePwd, headers);
        ResponseEntity<Void> exchange8 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request8, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange8.getStatusCode());
    }
}