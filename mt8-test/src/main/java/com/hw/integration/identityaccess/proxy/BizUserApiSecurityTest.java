package com.hw.integration.identityaccess.proxy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.OutgoingReqInterceptor;
import com.hw.helper.PendingResourceOwner;
import com.hw.helper.ResourceOwner;
import com.hw.helper.UserAction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.UUID;

import static com.hw.helper.UserAction.CLIENT_ID_RIGHT_ROLE_NOT_SUFFICIENT_RESOURCE_ID;
import static com.hw.helper.UserAction.EMPTY_CLIENT_SECRET;

/**
 * this integration auth requires oauth2service to be running
 */
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class BizUserApiSecurityTest {
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    @Autowired
    private UserAction action;
    UUID uuid;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            action.saveResult(description, uuid);
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    @Test
    public void should_not_able_to_create_user_w_client_missing_right_role() throws JsonProcessingException {
        ResourceOwner user = action.randomCreateUserDraft();
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = action.getJwtClientCredential(CLIENT_ID_RIGHT_ROLE_NOT_SUFFICIENT_RESOURCE_ID, EMPTY_CLIENT_SECRET);
        String value = registerTokenResponse.getBody().getValue();
        ResponseEntity<Void> pendingUser = action.createPendingUser(user, value, new PendingResourceOwner());
        Assert.assertEquals(HttpStatus.FORBIDDEN, pendingUser.getStatusCode());
    }


}
