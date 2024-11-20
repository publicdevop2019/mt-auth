package com.mt.integration.single.access.user.validation;

import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.RandomUtility;
import com.mt.helper.utility.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Tag("validation")
@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class UserLoginValidationTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        TestHelper.beforeEach(log, testInfo);
    }

    @Test
    public void validation_send_verify_code_invalid_email() {
        User user = UserUtility.randomEmailOnlyUser();
        //null
        user.setEmail(null);
        ResponseEntity<Void> response2 = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //blank
        user.setEmail(" ");
        ResponseEntity<Void> response = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        user.setEmail("");
        ResponseEntity<Void> response1 = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max length
        user.setEmail(RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
            RandomUtility.randomEmail());
        ResponseEntity<Void> response3 = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //invalid format
        user.setEmail(RandomUtility.randomStringWithNum());
        ResponseEntity<Void> response4 = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_send_verify_code_invalid_mobile_number() {
        User user = UserUtility.randomMobileOnlyUser();
        //null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        ResponseEntity<Void> response = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //one of null
        user.setCountryCode("1");
        user.setMobileNumber(null);
        ResponseEntity<Void> response0 = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response0.getStatusCode());
        //blank
        user.setCountryCode("1");
        user.setMobileNumber(" ");
        ResponseEntity<Void> response1 =
            UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setCountryCode("1");
        user.setMobileNumber("");
        ResponseEntity<Void> response2 =
            UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        user.setCountryCode("1");
        user.setMobileNumber("123");
        ResponseEntity<Void> response3 =
            UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        user.setCountryCode("1");
        user.setMobileNumber("1231231234123123");
        ResponseEntity<Void> response4 =
            UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid format
        user.setCountryCode("1");
        user.setMobileNumber("abcabcabcd");
        ResponseEntity<Void> response5 =
            UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_send_verify_code_invalid_country_code() {
        User user = UserUtility.randomMobileOnlyUser();
        //null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        ResponseEntity<Void> response2 = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //blank
        //one of null
        user.setCountryCode(null);
        user.setMobileNumber("1231231234");
        ResponseEntity<Void> response = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        user.setCountryCode(" ");
        ResponseEntity<Void> response1 = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setCountryCode("");
        ResponseEntity<Void> response3 = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //invalid value
        user.setCountryCode("7788");
        user.setMobileNumber("1231231234");
        ResponseEntity<Void> response4 = UserUtility.sendVerifyCode(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_mobile_code_login_invalid_code() {
        User user = UserUtility.randomMobileOnlyUser();
        UserUtility.sendVerifyCode(user);
        //null
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.mobileCodeLogin(user, null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        ResponseEntity<DefaultOAuth2AccessToken> response1 =
            UserUtility.mobileCodeLogin(user, " ");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.mobileCodeLogin(user, "");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        ResponseEntity<DefaultOAuth2AccessToken> response3 =
            UserUtility.mobileCodeLogin(user, "1");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        ResponseEntity<DefaultOAuth2AccessToken> response4 =
            UserUtility.mobileCodeLogin(user,
                "012345678901234567890123456789012345678901234567890123456789");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        ResponseEntity<DefaultOAuth2AccessToken> response5 =
            UserUtility.mobileCodeLogin(user, "abcdef");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //mismatch value
        ResponseEntity<DefaultOAuth2AccessToken> response6 =
            UserUtility.mobileCodeLogin(user, "654322");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_mobile_pwd_login_invalid_country_code() {
        User user = UserUtility.randomMobilePwdUser();
        //both null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //one of null
        user.setCountryCode(null);
        user.setMobileNumber("1231231234");
        ResponseEntity<DefaultOAuth2AccessToken> response0 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response0.getStatusCode());
        //blank
        user.setCountryCode(" ");
        ResponseEntity<DefaultOAuth2AccessToken> response1 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setCountryCode("");
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //invalid value
        user.setCountryCode("7788");
        user.setMobileNumber("1231231234");
        ResponseEntity<DefaultOAuth2AccessToken> response3 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_mobile_pwd_login_invalid_mobile_number() {
        User user = UserUtility.randomMobilePwdUser();
        //null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //one of null
        user.setCountryCode("1");
        user.setMobileNumber(null);
        ResponseEntity<DefaultOAuth2AccessToken> response0 = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response0.getStatusCode());
        //blank
        user.setCountryCode("1");
        user.setMobileNumber(" ");
        ResponseEntity<DefaultOAuth2AccessToken> response1 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setCountryCode("1");
        user.setMobileNumber("");
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        user.setCountryCode("1");
        user.setMobileNumber("123");
        ResponseEntity<DefaultOAuth2AccessToken> response3 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        user.setCountryCode("1");
        user.setMobileNumber("1231231234123123");
        ResponseEntity<DefaultOAuth2AccessToken> response4 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid format
        user.setCountryCode("1");
        user.setMobileNumber("abcabcabcd");
        ResponseEntity<DefaultOAuth2AccessToken> response5 =
            UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_mobile_pwd_login_invalid_pwd() {
        User user = UserUtility.randomMobilePwdUser();
        //null
        user.setPassword(null);
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        user.setPassword(" ");
        ResponseEntity<DefaultOAuth2AccessToken> response2 = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        user.setPassword("");
        ResponseEntity<DefaultOAuth2AccessToken> response3 = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //min length
        user.setPassword("Pa1!");
        ResponseEntity<DefaultOAuth2AccessToken> response4 = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //max length
        user.setPassword("Password1!0123456789012345678901234567890123456789");
        ResponseEntity<DefaultOAuth2AccessToken> response5 = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid format, missing number
        user.setPassword("Password!");
        ResponseEntity<DefaultOAuth2AccessToken> response6 = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //invalid format, missing letter
        user.setPassword("123123123!");
        ResponseEntity<DefaultOAuth2AccessToken> response8 = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
        //invalid format, missing special char
        user.setPassword("Password1");
        ResponseEntity<DefaultOAuth2AccessToken> response9 = UserUtility.mobilePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }

    @Test
    public void validation_mobile_code_login_invalid_country_code() {
        User user = UserUtility.randomMobileOnlyUser();
        UserUtility.sendVerifyCode(user);
        //both null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //one of null
        user.setCountryCode(null);
        user.setMobileNumber("1231231234");
        ResponseEntity<DefaultOAuth2AccessToken> response0 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response0.getStatusCode());
        //blank
        user.setCountryCode(" ");
        ResponseEntity<DefaultOAuth2AccessToken> response1 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setCountryCode("");
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //invalid value
        user.setCountryCode("7788");
        user.setMobileNumber("1231231234");
        ResponseEntity<DefaultOAuth2AccessToken> response3 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    @Test
    public void validation_mobile_code_login_invalid_mobile_number() {
        User user = UserUtility.randomMobileOnlyUser();
        UserUtility.sendVerifyCode(user);
        //null
        user.setCountryCode(null);
        user.setMobileNumber(null);
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //one of null
        user.setCountryCode("1");
        user.setMobileNumber(null);
        ResponseEntity<DefaultOAuth2AccessToken> response0 = UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response0.getStatusCode());
        //blank
        user.setCountryCode("1");
        user.setMobileNumber(" ");
        ResponseEntity<DefaultOAuth2AccessToken> response1 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        user.setCountryCode("1");
        user.setMobileNumber("");
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        user.setCountryCode("1");
        user.setMobileNumber("123");
        ResponseEntity<DefaultOAuth2AccessToken> response3 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        user.setCountryCode("1");
        user.setMobileNumber("1231231234123123");
        ResponseEntity<DefaultOAuth2AccessToken> response4 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid format
        user.setCountryCode("1");
        user.setMobileNumber("abcabcabcd");
        ResponseEntity<DefaultOAuth2AccessToken> response5 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
    }

    @Test
    public void validation_email_code_login_invalid_email() {
        User user = UserUtility.randomEmailOnlyUser();
        UserUtility.sendVerifyCode(user);
        //null
        user.setEmail(null);
        ResponseEntity<DefaultOAuth2AccessToken> response2 = UserUtility.emailCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //blank
        user.setEmail(" ");
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.emailCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        user.setEmail("");
        ResponseEntity<DefaultOAuth2AccessToken> response1 = UserUtility.emailCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max length
        user.setEmail(RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
            RandomUtility.randomEmail());
        ResponseEntity<DefaultOAuth2AccessToken> response3 = UserUtility.emailCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //invalid format
        user.setEmail(RandomUtility.randomStringWithNum());
        ResponseEntity<DefaultOAuth2AccessToken> response4 = UserUtility.emailCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_email_code_login_invalid_code() {
        User user = UserUtility.randomEmailOnlyUser();
        UserUtility.sendVerifyCode(user);
        //null
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.emailCodeLogin(user, null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        ResponseEntity<DefaultOAuth2AccessToken> response1 =
            UserUtility.emailCodeLogin(user, " ");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //empty
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.emailCodeLogin(user, "");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //min length
        ResponseEntity<DefaultOAuth2AccessToken> response3 =
            UserUtility.emailCodeLogin(user, "1");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //max length
        ResponseEntity<DefaultOAuth2AccessToken> response4 =
            UserUtility.emailCodeLogin(user,
                "012345678901234567890123456789012345678901234567890123456789");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //invalid value
        ResponseEntity<DefaultOAuth2AccessToken> response5 =
            UserUtility.emailCodeLogin(user, "abcdef");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //mismatch value
        ResponseEntity<DefaultOAuth2AccessToken> response6 =
            UserUtility.emailCodeLogin(user, "654322");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }

    @Test
    public void validation_email_code_login_expired_code() throws InterruptedException {
        User user = UserUtility.randomEmailOnlyUser();
        UserUtility.sendVerifyCode(user);
        Thread.sleep(5 * 60 * 1000);
        ResponseEntity<DefaultOAuth2AccessToken> response6 =
            UserUtility.emailCodeLogin(user, "123456");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
    }
    @Test
    public void validation_email_code_login_no_wait() {
        User user = UserUtility.randomEmailOnlyUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.emailCodeLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.emailCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void validation_username_pwd_login_invalid_username() {
        User user = UserUtility.randomUsernamePwdUser();
        //null
        user.setUsername(null);
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        user.setUsername(" ");
        ResponseEntity<DefaultOAuth2AccessToken> response2 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        user.setUsername("");
        ResponseEntity<DefaultOAuth2AccessToken> response3 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //min length
        user.setUsername("1");
        ResponseEntity<DefaultOAuth2AccessToken> response4 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //max length
        user.setUsername("username01234567890123456789012345678901234567890123456789");
        ResponseEntity<DefaultOAuth2AccessToken> response5 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid format, email
        user.setUsername("test@sample.com");
        ResponseEntity<DefaultOAuth2AccessToken> response6 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //invalid format, pure number
        user.setUsername("123123123");
        ResponseEntity<DefaultOAuth2AccessToken> response8 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
    }

    @Test
    public void validation_username_pwd_login_invalid_pwd() {
        User user = UserUtility.randomUsernamePwdUser();
        //null
        user.setPassword(null);
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        user.setPassword(" ");
        ResponseEntity<DefaultOAuth2AccessToken> response2 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        user.setPassword("");
        ResponseEntity<DefaultOAuth2AccessToken> response3 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //min length
        user.setPassword("Pa1!");
        ResponseEntity<DefaultOAuth2AccessToken> response4 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //max length
        user.setPassword("Password1!0123456789012345678901234567890123456789");
        ResponseEntity<DefaultOAuth2AccessToken> response5 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid format, missing number
        user.setPassword("Password!");
        ResponseEntity<DefaultOAuth2AccessToken> response6 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //invalid format, missing letter
        user.setPassword("123123123!");
        ResponseEntity<DefaultOAuth2AccessToken> response8 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
        //invalid format, missing special char
        user.setPassword("Password1");
        ResponseEntity<DefaultOAuth2AccessToken> response9 = UserUtility.usernamePwdLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }

    @Test
    public void validation_email_pwd_login_invalid_email() {
        User user = UserUtility.randomEmailPwdUser();
        //null
        user.setEmail(null);
        ResponseEntity<DefaultOAuth2AccessToken> response2 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //blank
        user.setEmail(" ");
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //empty
        user.setEmail("");
        ResponseEntity<DefaultOAuth2AccessToken> response1 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        //max length
        user.setEmail(RandomUtility.randomStringWithNum() + RandomUtility.randomStringWithNum() +
            RandomUtility.randomEmail());
        ResponseEntity<DefaultOAuth2AccessToken> response3 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //invalid format
        user.setEmail(RandomUtility.randomStringWithNum());
        ResponseEntity<DefaultOAuth2AccessToken> response4 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    @Test
    public void validation_email_pwd_login_invalid_pwd() {
        User user = UserUtility.randomEmailPwdUser();
        //null
        user.setPassword(null);
        ResponseEntity<DefaultOAuth2AccessToken> response = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //blank
        user.setPassword(" ");
        ResponseEntity<DefaultOAuth2AccessToken> response2 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        //empty
        user.setPassword("");
        ResponseEntity<DefaultOAuth2AccessToken> response3 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        //min length
        user.setPassword("Pa1!");
        ResponseEntity<DefaultOAuth2AccessToken> response4 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
        //max length
        user.setPassword("Password1!0123456789012345678901234567890123456789");
        ResponseEntity<DefaultOAuth2AccessToken> response5 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response5.getStatusCode());
        //invalid format, missing number
        user.setPassword("Password!");
        ResponseEntity<DefaultOAuth2AccessToken> response6 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response6.getStatusCode());
        //invalid format, missing letter
        user.setPassword("123123123!");
        ResponseEntity<DefaultOAuth2AccessToken> response8 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response8.getStatusCode());
        //invalid format, missing special char
        user.setPassword("Password1");
        ResponseEntity<DefaultOAuth2AccessToken> response9 = UserUtility.emailPwdLoginRaw(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response9.getStatusCode());
    }


    @Test
    public void validation_mobile_code_login_no_wait() {
        User user = UserUtility.randomMobileOnlyUser();
        ResponseEntity<DefaultOAuth2AccessToken> response =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<DefaultOAuth2AccessToken> response2 =
            UserUtility.mobileCodeLogin(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }
}