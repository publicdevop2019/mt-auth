package com.mt.test_case.integration.single.access;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.CommonTest;
import com.mt.test_case.helper.utility.OAuth2Utility;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
public class ClientCredentialsTest extends CommonTest {

    @Test
    public void use_client_with_secret() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithClient(AppConstant.GRANT_TYPE_CLIENT_CREDENTIALS,
                AppConstant.CLIENT_ID_OAUTH2_ID,
                AppConstant.COMMON_CLIENT_SECRET);
        Assertions.assertNotNull(tokenResponse.getBody().getValue());
    }

    @Test
    public void use_client_with_empty_secret() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithClient(AppConstant.GRANT_TYPE_CLIENT_CREDENTIALS,
                AppConstant.CLIENT_ID_REGISTER_ID,
                AppConstant.EMPTY_CLIENT_SECRET);
        Assertions.assertNotNull(tokenResponse.getBody().getValue());

    }

    @Test
    public void use_client_with_wrong_credentials() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithClient(AppConstant.GRANT_TYPE_CLIENT_CREDENTIALS,
                AppConstant.CLIENT_ID_OAUTH2_ID,
                AppConstant.EMPTY_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());

    }

    @Test
    public void use_client_with_wrong_grant_type() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithClient(AppConstant.GRANT_TYPE_PASSWORD,
                AppConstant.CLIENT_ID_OAUTH2_ID,
                AppConstant.COMMON_CLIENT_SECRET);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse.getStatusCode());

    }

    @Test
    public void trying_to_login_with_not_exist_client() {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse =
            OAuth2Utility.getOAuth2WithClient(AppConstant.GRANT_TYPE_PASSWORD,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        Assertions.assertEquals(tokenResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);

    }
}
