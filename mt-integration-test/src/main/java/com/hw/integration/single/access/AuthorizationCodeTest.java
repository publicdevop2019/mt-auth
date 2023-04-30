package com.hw.integration.single.access;

import static com.hw.helper.AppConstant.CLIENT_ID_LOGIN_ID;
import static com.hw.helper.AppConstant.CLIENT_ID_OM_ID;
import static com.hw.helper.AppConstant.EMPTY_CLIENT_SECRET;
import static com.hw.helper.AppConstant.GRANT_TYPE_PASSWORD;
import static com.hw.helper.AppConstant.OBJECT_MARKET_REDIRECT_URI;

import com.hw.helper.utility.JwtUtility;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.UserUtility;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class AuthorizationCodeTest  extends CommonTest {

    @Test
    public void get_authorize_code_after_pwd_login_for_user() {
        ResponseEntity<String> code = OAuth2Utility
            .getOAuth2AuthorizationCode(CLIENT_ID_OM_ID, UserUtility.getJwtUser(),
                OBJECT_MARKET_REDIRECT_URI);
        String authorizationCode = OAuth2Utility.getAuthorizationCode(code);
        Assert.assertNotNull(authorizationCode);
    }

    @Test
    public void authorize_token_has_permission() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(CLIENT_ID_OM_ID, UserUtility.getJwtUser(),
                OBJECT_MARKET_REDIRECT_URI);
        String code = OAuth2Utility.getAuthorizationCode(codeResp);

        Assert.assertNotNull(code);

        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken =
            OAuth2Utility
                .getOAuth2AuthorizationToken(code, OBJECT_MARKET_REDIRECT_URI,
                    CLIENT_ID_OM_ID, EMPTY_CLIENT_SECRET);

        Assert.assertEquals(HttpStatus.OK, authorizationToken.getStatusCode());
        Assert.assertNotNull(authorizationToken.getBody());
        DefaultOAuth2AccessToken body = authorizationToken.getBody();
        List<String> authorities = JwtUtility.getPermissions(body.getValue());
        Assert.assertNotEquals(0, authorities.size());

    }


    @Test
    public void use_wrong_authorize_code_after_user_grant_access() {
        ResponseEntity<String> code = OAuth2Utility
            .getOAuth2AuthorizationCode(CLIENT_ID_OM_ID, UserUtility.getJwtAdmin(),
                OBJECT_MARKET_REDIRECT_URI);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = OAuth2Utility
            .getOAuth2AuthorizationToken(UUID.randomUUID().toString(), OBJECT_MARKET_REDIRECT_URI,
                CLIENT_ID_OM_ID, EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_redirect_url_during_authorization() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(CLIENT_ID_OM_ID, UserUtility.getJwtAdmin(),
                OBJECT_MARKET_REDIRECT_URI);
        String code = OAuth2Utility.getAuthorizationCode(codeResp);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = OAuth2Utility
            .getOAuth2AuthorizationToken(code, UUID.randomUUID().toString(), CLIENT_ID_OM_ID,
                EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_grant_type_during_authorization() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(CLIENT_ID_OM_ID, UserUtility.getJwtAdmin(),
                OBJECT_MARKET_REDIRECT_URI);
        String code = OAuth2Utility.getAuthorizationCode(codeResp);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken =
            OAuth2Utility.getOAuth2WithCode(GRANT_TYPE_PASSWORD, code, OBJECT_MARKET_REDIRECT_URI,
                CLIENT_ID_OM_ID, EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_client_id_during_authorization() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(CLIENT_ID_OM_ID, UserUtility.getJwtAdmin(),
                OBJECT_MARKET_REDIRECT_URI);
        String code = OAuth2Utility.getAuthorizationCode(codeResp);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = OAuth2Utility
            .getOAuth2AuthorizationToken(code, OBJECT_MARKET_REDIRECT_URI, CLIENT_ID_LOGIN_ID,
                EMPTY_CLIENT_SECRET);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, authorizationToken.getStatusCode());

    }

    @Test
    public void client_use_wrong_client_id_w_credential_during_authorization() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(CLIENT_ID_OM_ID, UserUtility.getJwtAdmin(),
                OBJECT_MARKET_REDIRECT_URI);
        String code = OAuth2Utility.getAuthorizationCode(codeResp);
        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = OAuth2Utility
            .getOAuth2AuthorizationToken(code, OBJECT_MARKET_REDIRECT_URI, CLIENT_ID_LOGIN_ID,
                UUID.randomUUID().toString());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, authorizationToken.getStatusCode());

    }

    @Test
    public void wrong_client_id_passed_during_authorization_code_call() {
        ResponseEntity<String> codeResp = OAuth2Utility
            .getOAuth2AuthorizationCode(UUID.randomUUID().toString(), UserUtility.getJwtAdmin(),
                OBJECT_MARKET_REDIRECT_URI);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, codeResp.getStatusCode());
    }

}
