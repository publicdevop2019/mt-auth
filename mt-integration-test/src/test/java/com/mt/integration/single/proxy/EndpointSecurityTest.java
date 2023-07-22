package com.mt.integration.single.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mt.helper.AppConstant;
import com.mt.helper.CommonTest;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.PendingUser;
import com.mt.helper.pojo.User;
import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.OAuth2Utility;
import com.mt.helper.utility.TestContext;
import com.mt.helper.utility.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * this integration auth requires oauth2service to be running.
 */

@Slf4j
@ExtendWith(SpringExtension.class)
public class EndpointSecurityTest extends CommonTest {

    @Test
    public void should_not_able_to_create_client_w_admin_account_when_going_through_proxy()
        throws JsonProcessingException {
        Client client = ClientUtility.getClientAsNonResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(UserUtility.getJwtUser());
        String s = TestContext.mapper.writeValueAsString(client);
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate()
                .exchange(AppConstant.CLIENT_MGMT_URL, HttpMethod.POST, request, String.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
    }

    @Test
    public void should_not_able_to_create_user_w_client_missing_right_role() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(
                AppConstant.CLIENT_ID_RIGHT_ROLE_NOT_SUFFICIENT_RESOURCE_ID,
                AppConstant.EMPTY_CLIENT_SECRET);
        String value = registerTokenResponse.getBody().getValue();
        ResponseEntity<Void> pendingUser =
            UserUtility.createPendingUser(user, value, new PendingUser());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, pendingUser.getStatusCode());
    }
}
