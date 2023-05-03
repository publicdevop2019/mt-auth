package com.hw.integration.single.proxy;

import static com.hw.helper.AppConstant.CLIENT_ID_RIGHT_ROLE_NOT_SUFFICIENT_RESOURCE_ID;
import static com.hw.helper.AppConstant.CLIENT_MGMT_URL;
import static com.hw.helper.AppConstant.EMPTY_CLIENT_SECRET;
import static com.hw.helper.utility.TestContext.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hw.helper.AppConstant;
import com.hw.helper.Client;
import com.hw.helper.PendingUser;
import com.hw.helper.User;
import com.hw.helper.utility.ClientUtility;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UserUtility;
import com.hw.integration.single.access.CommonTest;
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

/**
 * this integration auth requires oauth2service to be running.
 */
@Slf4j
@RunWith(SpringRunner.class)
public class EndpointSecurityTest  extends CommonTest {

    @Test
    public void should_not_able_to_create_client_w_admin_account_when_going_through_proxy()
        throws JsonProcessingException {
        Client client = ClientUtility.getClientAsNonResource(AppConstant.CLIENT_ID_RESOURCE_ID);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(UserUtility.getJwtUser());
        String s = mapper.writeValueAsString(client);
        HttpEntity<String> request = new HttpEntity<>(s, headers);
        ResponseEntity<String> exchange =
            TestContext.getRestTemplate()
                .exchange(CLIENT_MGMT_URL, HttpMethod.POST, request, String.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());
    }

    @Test
    public void should_not_able_to_create_user_w_client_missing_right_role() {
        User user = UserUtility.createRandomUserObj();
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(CLIENT_ID_RIGHT_ROLE_NOT_SUFFICIENT_RESOURCE_ID,
                EMPTY_CLIENT_SECRET);
        String value = registerTokenResponse.getBody().getValue();
        ResponseEntity<Void> pendingUser =
            UserUtility.createPendingUser(user, value, new PendingUser());
        Assert.assertEquals(HttpStatus.FORBIDDEN, pendingUser.getStatusCode());
    }
}
