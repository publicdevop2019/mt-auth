package com.hw.integration.identityaccess.proxy;

import static com.hw.helper.AppConstant.CLIENT_ID_RIGHT_ROLE_NOT_SUFFICIENT_RESOURCE_ID;
import static com.hw.helper.AppConstant.EMPTY_CLIENT_SECRET;

import com.hw.helper.PendingResourceOwner;
import com.hw.helper.User;
import com.hw.helper.utility.OAuth2Utility;
import com.hw.helper.utility.TestContext;
import com.hw.helper.utility.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * this integration auth requires oauth2service to be running.
 */
@RunWith(SpringRunner.class)
@Slf4j
public class UserApiSecurityTest {
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            log.error("test failed, method {}, id {}", description.getMethodName(),
                TestContext.getTestId());
        }
    };

    @Before
    public void setUp() {
        TestContext.init();
        log.info("test id {}", TestContext.getTestId());
    }

    @Test
    public void should_not_able_to_create_user_w_client_missing_right_role() {
        User user = UserUtility.createUser();
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = OAuth2Utility
            .getOAuth2ClientCredentialToken(CLIENT_ID_RIGHT_ROLE_NOT_SUFFICIENT_RESOURCE_ID,
                EMPTY_CLIENT_SECRET);
        String value = registerTokenResponse.getBody().getValue();
        ResponseEntity<Void> pendingUser =
            UserUtility.createPendingUser(user, value, new PendingResourceOwner());
        Assert.assertEquals(HttpStatus.FORBIDDEN, pendingUser.getStatusCode());
    }


}
