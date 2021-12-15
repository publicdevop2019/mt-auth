package com.hw.integration.identityaccess.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.helper.*;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.hw.helper.UserAction.*;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class BIzUserTest {
    public static final String RESOURCE_OWNER = "/users";
    private static final String root_index = "0U8AZTODP4H0";
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
    public void create_user_with_right_authority_is_user_only() throws JsonProcessingException {
        ResourceOwner user = action.randomCreateUserDraft();
        ResponseEntity<DefaultOAuth2AccessToken> user1 = action.registerAnUser(user);

        Assert.assertEquals(HttpStatus.OK, user1.getStatusCode());

        Assert.assertNotNull(user1.getHeaders().getLocation());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 = action.getJwtPassword(user.getEmail(), user.getPassword());

        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());

        List<String> authorities = ServiceUtility.getAuthority(tokenResponse12.getBody().getValue());

        Assert.assertEquals(1, authorities.stream().filter(e -> e.equals(AccessConstant.USER_USER_ID)).count());
        Assert.assertEquals(0, authorities.stream().filter(e -> e.equals(AccessConstant.ADMIN_USER_ID)).count());
        Assert.assertEquals(0, authorities.stream().filter(e -> e.equals(AccessConstant.ROOT_USER_ID)).count());

    }

    @Test
    public void should_not_able_to_create_user_with_user_name_not_email() throws JsonProcessingException {
        ResourceOwner user = action.userCreateDraft(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        ResponseEntity<DefaultOAuth2AccessToken> user1 = action.registerAnUser(user);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, user1.getStatusCode());

    }

    @Test
    public void update_user_password_without_current_pwd() throws JsonProcessingException {
        ResourceOwner user = action.randomCreateUserDraft();
        action.registerAnUser(user);
        /** Location is not used in this case, root/admin/user can only update their password */
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/pwd";
        String newPassword = UUID.randomUUID().toString().replace("-", "");
        /** Login */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(user.getEmail(), user.getPassword());
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        user.setPassword(newPassword);
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<Object> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, Object.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }

    @Test
    public void forget_password() throws JsonProcessingException {
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = action.getJwtClientCredential(CLIENT_ID_REGISTER_ID, EMPTY_CLIENT_SECRET);
        String value = registerTokenResponse.getBody().getValue();
        ResourceOwner user = action.randomCreateUserDraft();
        action.registerAnUser(user);
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/forgetPwd";
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest();
        forgetPasswordRequest.setEmail(user.getEmail());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(value);
        String s1 = mapper.writeValueAsString(forgetPasswordRequest);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<Object> exchange = action.restTemplate.exchange(url, HttpMethod.POST, request, Object.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        String url2 = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/resetPwd";
        forgetPasswordRequest.setToken("123456789");
        forgetPasswordRequest.setNewPassword(UUID.randomUUID().toString().replaceAll("-",""));
        String s2 = mapper.writeValueAsString(forgetPasswordRequest);
        HttpHeaders header2 = new HttpHeaders();
        header2.setContentType(MediaType.APPLICATION_JSON);
        header2.setBearerAuth(value);
        HttpEntity<String> request2 = new HttpEntity<>(s2, header2);

        action.restTemplate.exchange(url2, HttpMethod.POST, request2, Object.class);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        /**login */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(forgetPasswordRequest.getEmail(), forgetPasswordRequest.getNewPassword());
        Assert.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());

    }

    @Test
    public void update_user_password_with_current_pwd() throws JsonProcessingException {
        ResourceOwner user = action.randomCreateUserDraft();
        ResourceOwnerUpdatePwd resourceOwnerUpdatePwd = new ResourceOwnerUpdatePwd();
        resourceOwnerUpdatePwd.setCurrentPwd(user.getPassword());
        resourceOwnerUpdatePwd.setEmail(user.getEmail());
        resourceOwnerUpdatePwd.setPassword(UUID.randomUUID().toString().replaceAll("-",""));
        ResponseEntity<DefaultOAuth2AccessToken> createResponse = action.registerAnUser(user);
        /** Location is not used in this case, root/admin/user can only update their password */
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/pwd";
        /** Login */
        String oldPassword = user.getPassword();
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(user.getEmail(), user.getPassword());
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);

        String s1 = mapper.writeValueAsString(resourceOwnerUpdatePwd);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<Object> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, Object.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> resp3 = action.getJwtPassword(user.getEmail(), oldPassword);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> resp4 = action.getJwtPassword(user.getEmail(), resourceOwnerUpdatePwd.getPassword());

        Assert.assertEquals(HttpStatus.OK, resp4.getStatusCode());

    }

    @Test
    public void read_all_users_with_root_account() {
        ParameterizedTypeReference<List<ResourceOwner>> responseType = new ParameterizedTypeReference<>() {
        };
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER ;
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getBody().getValue());
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<SumTotalUser> exchange = action.restTemplate.exchange(url, HttpMethod.GET, request, SumTotalUser.class);

        Assert.assertNotSame(0, exchange.getBody().getData().size());
    }


    @Test
    public void update_user_authority_to_admin_with_root_account() throws JsonProcessingException {
        ResourceOwner user = action.randomCreateUserDraft();
        ResponseEntity<DefaultOAuth2AccessToken> createResp = action.registerAnUser(user);
        String s = createResp.getHeaders().getLocation().toString();
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/" + s;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        user.setGrantedAuthorities(List.of(AccessConstant.ADMIN_USER_ID, AccessConstant.USER_USER_ID));
        user.setVersion(0);
        HttpEntity<ResourceOwner> request = new HttpEntity<>(user, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        /**
         * login to verify grantedAuthorities has been changed
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = action.getJwtPassword(user.getEmail(), user.getPassword());
        List<String> authorities = ServiceUtility.getAuthority(tokenResponse1.getBody().getValue());
        Assert.assertEquals(1, authorities.stream().filter(e -> e.equals(AccessConstant.USER_USER_ID)).count());
        Assert.assertEquals(1, authorities.stream().filter(e -> e.equals(AccessConstant.ADMIN_USER_ID)).count());
        Assert.assertEquals(0, authorities.stream().filter(e -> e.equals(AccessConstant.ROOT_USER_ID)).count());
    }

    @Test
    public void should_not_able_to_update_user_authority_to_root_with_user_account() throws JsonProcessingException {
        ResourceOwner user = action.randomCreateUserDraft();
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/" + root_index;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_USER, ACCOUNT_PASSWORD_USER);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);

        user.setGrantedAuthorities(List.of(AccessConstant.ADMIN_USER_ID, AccessConstant.USER_USER_ID));
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

    }

    @Test
    public void should_not_able_to_update_user_authority_to_root_with_root_account() throws JsonProcessingException {
        ResourceOwner user = action.randomCreateUserDraft();
        ResponseEntity<DefaultOAuth2AccessToken> createResp = action.registerAnUser(user);
        String s = createResp.getHeaders().getLocation().toString();
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/" + s;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        user.setGrantedAuthorities(List.of(AccessConstant.ADMIN_USER_ID, AccessConstant.USER_USER_ID, AccessConstant.ROOT_USER_ID));
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());

    }

    @Test
    public void should_not_able_to_update_user_authority_to_admin_with_admin_account() throws JsonProcessingException {
        ResourceOwner user = action.randomCreateUserDraft();
        ResponseEntity<DefaultOAuth2AccessToken> createResp = action.registerAnUser(user);
        String s = createResp.getHeaders().getLocation().toString();
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/" + s;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        user.setGrantedAuthorities(List.of(AccessConstant.ADMIN_USER_ID, AccessConstant.USER_USER_ID));
        String s1 = mapper.writeValueAsString(user);
        HttpEntity<String> request = new HttpEntity<>(s1, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange.getStatusCode());

    }

    @Test
    public void lock_then_unlock_user() throws JsonProcessingException {
        ResourceOwner user = action.randomCreateUserDraft();
        ResponseEntity<DefaultOAuth2AccessToken> createResp = action.registerAnUser(user);
        String s = createResp.getHeaders().getLocation().toString();
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/" + s;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        user.setGrantedAuthorities(List.of(AccessConstant.USER_USER_ID));
        user.setLocked(true);
        user.setVersion(0);
        HttpEntity<ResourceOwner> request = new HttpEntity<>(user, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = action.restTemplate.exchange(url, HttpMethod.PUT, request, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        /**
         * login to verify account has been locked
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse1 = action.getJwtPassword(user.getEmail(), user.getPassword());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, tokenResponse1.getStatusCode());

        user.setLocked(false);
        user.setVersion(1);
        HttpEntity<ResourceOwner> request22 = new HttpEntity<>(user, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange22 = action.restTemplate.exchange(url, HttpMethod.PUT, request22, DefaultOAuth2AccessToken.class);

        Assert.assertEquals(HttpStatus.OK, exchange22.getStatusCode());

        /**
         * login to verify account has been unlocked
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 = action.getJwtPassword(user.getEmail(), user.getPassword());
        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());
    }

    @Test
    public void delete_user() {
        ResourceOwner user = action.randomCreateUserDraft();
        ResponseEntity<DefaultOAuth2AccessToken> user1 = action.registerAnUser(user);

        String s = user1.getHeaders().getLocation().toString();
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/" + s;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 = action.getJwtPassword(user.getEmail(), user.getPassword());

        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getBody().getValue());
        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        ResponseEntity<Object> exchange = action.restTemplate.exchange(url, HttpMethod.DELETE, request, Object.class);

        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse123 = action.getJwtPassword(user.getEmail(), user.getPassword());

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, tokenResponse123.getStatusCode());

    }

    @Test
    public void should_not_able_to_delete_root_user() {

        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + RESOURCE_OWNER  + "/" + root_index;

        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse12 = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);

        Assert.assertEquals(HttpStatus.OK, tokenResponse12.getStatusCode());

        /**
         * try w root
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = action.getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenResponse.getBody().getValue());
        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        ResponseEntity<Object> exchange = action.restTemplate.exchange(url, HttpMethod.DELETE, request, Object.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
        /**
         * try w admin, admin can not delete user
         */
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse2 = action.getJwtPassword(ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(tokenResponse2.getBody().getValue());
        HttpEntity<Object> request2 = new HttpEntity<>(null, headers2);
        ResponseEntity<Object> exchange2 = action.restTemplate.exchange(url, HttpMethod.DELETE, request2, Object.class);
        /**
         * forbidden if using proxy instead of HttpStatus.BAD_REQUEST
         */
        Assert.assertEquals(HttpStatus.FORBIDDEN, exchange2.getStatusCode());

    }

}