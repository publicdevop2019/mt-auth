package com.mt.integration.single.access;

import com.mt.helper.AppConstant;
import com.mt.helper.TestHelper;
import com.mt.helper.TestResultLoggerExtension;
import com.mt.helper.utility.JwtUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.UserUtility;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TestResultLoggerExtension.class})
@Slf4j
public class AuthorizationCodeTest {
    @BeforeAll
    public static void beforeAll() {
        TestHelper.beforeAll(log);
    }

    @BeforeEach
    public void beforeEach() {
        TestHelper.beforeEach(log);
    }

    @Test
    public void get_authorize_code_after_pwd_login_for_user() {
        ResponseEntity<String> code = OAuth2Utility
            .getOAuth2AuthorizationCode(AppConstant.CLIENT_ID_OM_ID, UserUtility.getJwtUser(),
                AppConstant.OBJECT_MARKET_REDIRECT_URI);
        String authorizationCode = OAuth2Utility.getAuthorizationCode(code);
        Assertions.assertNotNull(authorizationCode);
    }

    @Test
    public void authorize_token_has_permission() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(AppConstant.CLIENT_ID_OM_ID, UserUtility.getJwtUser(),
                AppConstant.OBJECT_MARKET_REDIRECT_URI);
        String code = OAuth2Utility.getAuthorizationCode(codeResp);

        log.debug("code is {}", code);
        Assertions.assertNotNull(code);

        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken =
            OAuth2Utility
                .getOAuth2AuthorizationToken(code, AppConstant.OBJECT_MARKET_REDIRECT_URI,
                    AppConstant.CLIENT_ID_OM_ID, AppConstant.EMPTY_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.OK, authorizationToken.getStatusCode());
        Assertions.assertNotNull(authorizationToken.getBody());
        log.debug("token is {}", authorizationToken.getBody().getValue());
        DefaultOAuth2AccessToken body = authorizationToken.getBody();
        List<String> authorities = JwtUtility.getPermissions(body.getValue());
        Assertions.assertNotEquals(0, authorities.size());

    }


    @Test
    public void use_wrong_authorize_code_after_user_grant_access() {
        ResponseEntity<String> code = OAuth2Utility
            .getOAuth2AuthorizationCode(AppConstant.CLIENT_ID_OM_ID, UserUtility.getJwtAdmin(),
                AppConstant.OBJECT_MARKET_REDIRECT_URI);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = OAuth2Utility
            .getOAuth2AuthorizationToken(UUID.randomUUID().toString(),
                AppConstant.OBJECT_MARKET_REDIRECT_URI,
                AppConstant.CLIENT_ID_OM_ID, AppConstant.EMPTY_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_redirect_url_during_authorization() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(AppConstant.CLIENT_ID_OM_ID, UserUtility.getJwtAdmin(),
                AppConstant.OBJECT_MARKET_REDIRECT_URI);
        String code = OAuth2Utility.getAuthorizationCode(codeResp);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = OAuth2Utility
            .getOAuth2AuthorizationToken(code, UUID.randomUUID().toString(),
                AppConstant.CLIENT_ID_OM_ID,
                AppConstant.EMPTY_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_grant_type_during_authorization() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(AppConstant.CLIENT_ID_OM_ID, UserUtility.getJwtAdmin(),
                AppConstant.OBJECT_MARKET_REDIRECT_URI);
        String code = OAuth2Utility.getAuthorizationCode(codeResp);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken =
            OAuth2Utility.getOAuth2WithCode(
                AppConstant.GRANT_TYPE_PASSWORD, code, AppConstant.OBJECT_MARKET_REDIRECT_URI,
                AppConstant.CLIENT_ID_OM_ID, AppConstant.EMPTY_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_client_id_during_authorization() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(AppConstant.CLIENT_ID_OM_ID, UserUtility.getJwtAdmin(),
                AppConstant.OBJECT_MARKET_REDIRECT_URI);
        String code = OAuth2Utility.getAuthorizationCode(codeResp);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = OAuth2Utility
            .getOAuth2AuthorizationToken(code, AppConstant.OBJECT_MARKET_REDIRECT_URI,
                AppConstant.CLIENT_ID_LOGIN_ID,
                AppConstant.EMPTY_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_client_id_w_credential_during_authorization() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(AppConstant.CLIENT_ID_OM_ID, UserUtility.getJwtAdmin(),
                AppConstant.OBJECT_MARKET_REDIRECT_URI);
        String code = OAuth2Utility.getAuthorizationCode(codeResp);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = OAuth2Utility
            .getOAuth2AuthorizationToken(code, AppConstant.OBJECT_MARKET_REDIRECT_URI,
                AppConstant.CLIENT_ID_LOGIN_ID,
                UUID.randomUUID().toString());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, authorizationToken.getStatusCode());

    }

    @Test
    public void wrong_client_id_passed_during_authorization_code_call() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(UUID.randomUUID().toString(), UserUtility.getJwtAdmin(),
                AppConstant.OBJECT_MARKET_REDIRECT_URI);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, codeResp.getStatusCode());
    }

}
