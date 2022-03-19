package com.hw.integration.identityaccess.proxy;

import com.hw.helper.OutgoingReqInterceptor;
import com.hw.helper.SecurityProfile;
import com.hw.helper.SumTotal;
import com.hw.helper.UserAction;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.hw.helper.UserAction.SVC_NAME_AUTH;

/**
 * this integration auth requires oauth2service to be running
 */
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class EndpointTest {
    public static final String ENDPOINTS = "/projects/0P8HE307W6IO/endpoints";
    UUID uuid;
    @Autowired
    private UserAction action;
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            action.saveResult(description, uuid);
            log.error("test failed, method {}, uuid {}", description.getMethodName(), uuid);
        }
    };

    public static ResponseEntity<SumTotal<SecurityProfile>> readProfiles(UserAction action) {
        ResponseEntity<DefaultOAuth2AccessToken> pwdTokenResponse2 = action.getJwtPasswordRoot();
        String bearer1 = pwdTokenResponse2.getBody().getValue();
        String url = UserAction.proxyUrl + SVC_NAME_AUTH + ENDPOINTS;
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer1);
        HttpEntity<SecurityProfile> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return action.restTemplate.exchange(url, HttpMethod.GET, hashMapHttpEntity1, new ParameterizedTypeReference<>() {
        });
    }

    public static ResponseEntity<SecurityProfile> readProfile(UserAction action, String id) {
        ResponseEntity<DefaultOAuth2AccessToken> pwdTokenResponse2 = action.getJwtPasswordRoot();
        String bearer1 = pwdTokenResponse2.getBody().getValue();
        String url = UserAction.proxyUrl + SVC_NAME_AUTH + ENDPOINTS + "/" + id;
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer1);
        HttpEntity<SecurityProfile> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return action.restTemplate.exchange(url, HttpMethod.GET, hashMapHttpEntity1, SecurityProfile.class);
    }

    public static ResponseEntity<String> createProfile(SecurityProfile securityProfile, UserAction action) {
        ResponseEntity<DefaultOAuth2AccessToken> pwdTokenResponse2 = action.getJwtPasswordRoot();
        String bearer1 = pwdTokenResponse2.getBody().getValue();
        String url = UserAction.proxyUrl + SVC_NAME_AUTH + ENDPOINTS;
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer1);
        HttpEntity<SecurityProfile> hashMapHttpEntity1 = new HttpEntity<>(securityProfile, headers1);
        return action.restTemplate.exchange(url, HttpMethod.POST, hashMapHttpEntity1, String.class);
    }

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        action.restTemplate.getRestTemplate().setInterceptors(Collections.singletonList(new OutgoingReqInterceptor(uuid)));
    }

    @Test
    @Ignore
    public void modify_existing_profile_to_prevent_access() {
        String url2 = UserAction.proxyUrl + SVC_NAME_AUTH + "/users/admin";
        /**
         * before modify, admin is able to access resourceOwner apis
         */
        ResponseEntity<DefaultOAuth2AccessToken> pwdTokenResponse = action.getJwtPasswordRoot();
        String bearer1 = pwdTokenResponse.getBody().getValue();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer1);
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<String> exchange1 = action.restTemplate.exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange1.getStatusCode());

        /**
         * modify profile to prevent admin access
         */
        ResponseEntity<SumTotal<SecurityProfile>> listResponseEntity = readProfiles();
        SecurityProfile securityProfile = listResponseEntity.getBody().getData().get(6);
        securityProfile.getUserRoles().remove("ROLE_ADMIN");
        securityProfile.getUserRoles().add("ROLE_ROOT");
//        securityProfile.setExpression("hasRole('ROLE_ROOT') and #oauth2.hasScope('TRUST') and #oauth2.isUser()");

        ResponseEntity<String> stringResponseEntity = updateProfile(securityProfile, securityProfile.getId());
        Assert.assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());

        /**
         * after modify, admin is not able to access resourceOwner apis
         */
        try {
            Thread.sleep(15 * 1000);//wait for cache update
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResponseEntity<String> exchange = action.restTemplate.exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

        /**
         * modify profile to allow access
         */
        securityProfile.getUserRoles().remove("ROLE_ROOT");
        securityProfile.getUserRoles().add("ROLE_ADMIN");
        securityProfile.setVersion(securityProfile.getVersion() + 1);
        ResponseEntity<String> stringResponseEntity1 = updateProfile(securityProfile, securityProfile.getId());
        Assert.assertEquals(HttpStatus.OK, stringResponseEntity1.getStatusCode());
        try {
            Thread.sleep(15 * 1000);//wait for cache update
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResponseEntity<String> exchange2 = action.restTemplate.exchange(url2, HttpMethod.GET, hashMapHttpEntity1, String.class);
        Assert.assertEquals(HttpStatus.OK, exchange2.getStatusCode());
    }

    @Test
    public void create_new_endpoint_then_delete() {
        SecurityProfile securityProfile1 = new SecurityProfile();
        securityProfile1.setResourceId("0C8AZTODP4HT");
        securityProfile1.setUserRoles(new HashSet<>(List.of("ROLE_ADMIN")));
        securityProfile1.setUserOnly(true);
        securityProfile1.setName("test");
        securityProfile1.setMethod("GET");
        securityProfile1.setWebsocket(false);
        securityProfile1.setPath("/test/" + UUID.randomUUID().toString().replace("-", "").replaceAll("\\d", "") + "/abc");
        ResponseEntity<String> profile = createProfile(securityProfile1);
        Assert.assertEquals(HttpStatus.OK, profile.getStatusCode());
        ResponseEntity<String> stringResponseEntity = deleteProfile(profile.getHeaders().getLocation().toString());
        Assert.assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());
    }

    private ResponseEntity<SumTotal<SecurityProfile>> readProfiles() {
        return readProfiles(action);
    }

    private ResponseEntity<String> createProfile(SecurityProfile securityProfile) {
        return createProfile(securityProfile, action);
    }

    private ResponseEntity<String> updateProfile(SecurityProfile securityProfile, String id) {
        ResponseEntity<DefaultOAuth2AccessToken> pwdTokenResponse2 = action.getJwtPasswordRoot();
        String bearer1 = pwdTokenResponse2.getBody().getValue();
        String url = UserAction.proxyUrl + SVC_NAME_AUTH + ENDPOINTS + "/" + id;
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer1);
        HttpEntity<SecurityProfile> hashMapHttpEntity1 = new HttpEntity<>(securityProfile, headers1);
        return action.restTemplate.exchange(url, HttpMethod.PUT, hashMapHttpEntity1, String.class);
    }

    private ResponseEntity<String> deleteProfile(String id) {
        ResponseEntity<DefaultOAuth2AccessToken> pwdTokenResponse2 = action.getJwtPasswordRoot();
        String bearer1 = pwdTokenResponse2.getBody().getValue();
        String url = UserAction.proxyUrl + SVC_NAME_AUTH + ENDPOINTS + "/" + id;
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setBearerAuth(bearer1);
        HttpEntity<Object> hashMapHttpEntity1 = new HttpEntity<>(headers1);
        return action.restTemplate.exchange(url, HttpMethod.DELETE, hashMapHttpEntity1, String.class);
    }
}
