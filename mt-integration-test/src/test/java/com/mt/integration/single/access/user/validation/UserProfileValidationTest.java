package com.mt.integration.single.access.user.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.User;
import com.mt.helper.pojo.UserUpdatePwd;
import com.mt.helper.utility.HttpUtility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

@Tag("validation")

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class UserProfileValidationTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void validation_create_avatar() throws FileNotFoundException {
        //created user
        String url = HttpUtility.getAccessUrl("/users" + "/profile/avatar");
        User user = UserUtility.createEmailPwdUser();
        String bearer = UserUtility.emailPwdLogin(user);
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
    public void validation_update_user_profile_username() {
        User user = UserUtility.createEmailPwdUser();
        String url = HttpUtility.getAccessUrl("/users" + "/profile" + "/username");
        String bearer = UserUtility.emailPwdLogin(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        //null
        user.setUsername(null);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //blank
        user.setUsername(" ");
        HttpEntity<User> request2 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request2, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        user.setUsername("");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request3, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //min length
        user.setUsername("1");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request4, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //max length
        user.setUsername(
            "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request5, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
        //invalid char
        user.setUsername(
            "<0123456789");
        HttpEntity<User> request6 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange6 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request6, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange6.getStatusCode());
    }

    @Test
    public void validation_update_user_profile_country_code() {
        User user = UserUtility.createEmailPwdUser();
        String url = HttpUtility.getAccessUrl("/users" + "/profile" + "/mobile");
        String bearer = UserUtility.emailPwdLogin(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        //both null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //one of is null
        user.setCountryCode("1");
        user.setMobileNumber(null);
        HttpEntity<User> request2 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request2, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        user.setCountryCode("");
        user.setMobileNumber("1231231234");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request3, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //blank
        user.setCountryCode(" ");
        user.setMobileNumber("1231231234");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request4, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid value
        user.setCountryCode("123123123");
        user.setMobileNumber("1231231234");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request5, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
    }

    @Test
    public void validation_update_user_profile_mobile() {
        User user = UserUtility.createEmailPwdUser();
        String url = HttpUtility.getAccessUrl("/users" + "/profile" + "/mobile");
        String bearer = UserUtility.emailPwdLogin(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        //both null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        //one of is null
        user.setCountryCode(null);
        user.setMobileNumber("1231231234");
        HttpEntity<User> request2 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange2 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request2, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange2.getStatusCode());
        //empty
        user.setCountryCode("1");
        user.setMobileNumber("");
        HttpEntity<User> request3 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange3 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request3, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange3.getStatusCode());
        //blank
        user.setCountryCode("1");
        user.setMobileNumber(" ");
        HttpEntity<User> request4 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange4 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request4, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange4.getStatusCode());
        //invalid format
        user.setCountryCode("1");
        user.setMobileNumber("123");
        HttpEntity<User> request5 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange5 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request5, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange5.getStatusCode());
    }

    @Test
    public void validation_update_user_profile_language() {
        User user = UserUtility.createEmailPwdUser();
        String url = HttpUtility.getAccessUrl("/users" + "/profile" + "/language");
        String bearer = UserUtility.emailPwdLogin(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        //null
        user.setLanguage(null);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> exchange =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
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
        //do not accept numeric enum value
        user.setLanguage(0);
        HttpEntity<User> request6 = new HttpEntity<>(user, headers);
        ResponseEntity<Void> request7 =
            TestContext.getRestTemplate().exchange(url, HttpMethod.PUT, request6, Void.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, request7.getStatusCode());
    }

    @Test
    public void validation_update_pwd_new_password() {
        String url = HttpUtility.getAccessUrl("/users" + "/pwd");
        User user = UserUtility.randomEmailPwdUser();
        String bearer = UserUtility.emailPwdLogin(user);

        UserUpdatePwd updatePwd = new UserUpdatePwd();
        updatePwd.setCurrentPwd(user.getPassword());
        //login
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

    //TODO merge it with other pwd tests
    @Test
    public void validation_update_pwd_current_password() {
        String url = HttpUtility.getAccessUrl("/users" + "/pwd");
        User user = UserUtility.randomEmailPwdUser();
        String bearer = UserUtility.emailPwdLogin(user);

        UserUpdatePwd updatePwd = new UserUpdatePwd();
        updatePwd.setCurrentPwd(user.getPassword());
        //login
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

    @Test
    public void validate_update_pwd_no_current_pwd() throws JsonProcessingException {
        User user = UserUtility.randomEmailPwdUser();
        String url = HttpUtility.getAccessUrl("/users" + "/pwd");
        String newPassword = UUID.randomUUID().toString().replace("-", "");
        //Login
        String bearer = UserUtility.emailPwdLogin(user);
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
}