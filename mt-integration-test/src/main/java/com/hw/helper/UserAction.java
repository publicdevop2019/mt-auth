package com.hw.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

@Component
@Slf4j
public class UserAction {
    public static final String TEST_TEST_VALUE = "3T8BRPK17W8A:S";
    public static final String TEST_TEST_VALUE_2 = "3T8BRPK17W94:上装";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    public static final String CLIENT_ID_LOGIN_ID = "0C8AZZ16LZB4";
    public static final String CLIENT_ID_OAUTH2_ID = "0C8AZTODP4HT";
    public static final String CLIENT_ID_REGISTER_ID = "0C8B00098WLD";
    public static final String CLIENT_ID_OM_ID = "0C8HPGMON9J5";
    public static final String CLIENT_ID_RIGHT_ROLE_NOT_SUFFICIENT_RESOURCE_ID = "0C8AZTODP4H8";
    public static final String CLIENT_ID_RESOURCE_ID = "0C8AZTODP4I0";
    public static final String CLIENT_ID_USER_PROFILE_ID = "0C8AZTODP4H7";
    public static final String CLIENT_ID_SAGA_ID = "0C8AZTODP4H1";
    public static final String CLIENT_ID_TEST_ID = "0C8B00CSATJ6";
    public static final String CLIENT_ID_BBS_ID = "bbs-ui";
    public static final String COMMON_CLIENT_SECRET = "root";
    public static final String EMPTY_CLIENT_SECRET = "";
    public static final String AUTHORIZE_STATE = "login";
    public static final String AUTHORIZE_RESPONSE_TYPE = "code";
    public static final String ACCOUNT_USERNAME_ROOT = "superAdmin@duoshu.org";
    public static final String ACCOUNT_PASSWORD_ROOT = "root";
    public static final String ACCOUNT_USERNAME_USER = "user1@duoshu.org";
    public static final String ACCOUNT_PASSWORD_USER = "root";
    public static final String SVC_NAME_AUTH = "/auth-svc";
    public static final String SVC_NAME_PRODUCT = "/product-svc";
    public static final String SVC_NAME_FILE_UPLOAD = "/file-upload-svc";
    public static final String SVC_NAME_PROFILE = "/profile-svc";
    public static final String SVC_NAME_BBS = "/bbs-svc";
    public static final String SVC_NAME_TEST = "/test-svc";
    public static final String OBJECT_MARKET_REDIRECT_URI = "http://localhost:4200/account";
    public static final String BBS_REDIRECT_URI = "http://localhost:3000/account";
    public static final String CLIENTS = "/projects/0P8HE307W6IO/clients";
    public static final String FILES = "/files";
    public static final String PROJECT_ID = "0P8HPG99R56P";
    //        public static String proxyUrl = "http://api.manytreetechnology.com:" + 8111;
    public static String proxyUrl = "http://192.168.2.23:" + 8111;
    public static final String URL = UserAction.proxyUrl + SVC_NAME_AUTH + "/oauth/token";
    public static final String URL2 = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + CLIENTS;
    public static String PROXY_URL_TOKEN = proxyUrl + SVC_NAME_AUTH + "/oauth/token";
    public List<ResourceOwner> testUser = new ArrayList<>();
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    public TestRestTemplate restTemplate = new TestRestTemplate();
    @Autowired
    private TestHelper helper;

    public UserAction() {
    }

    /**
     * copied from https://www.planetgeek.ch/2009/08/25/how-to-find-a-concurrency-bug-with-java/
     *
     * @param message
     * @param runnables
     * @param maxTimeoutSeconds
     * @throws InterruptedException
     */
    public static void assertConcurrent(final String message, final List<? extends Runnable> runnables, final int maxTimeoutSeconds) throws InterruptedException {
        final int numThreads = runnables.size();
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
        final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        try {
            final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
            final CountDownLatch afterInitBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numThreads);
            for (final Runnable submittedTestRunnable : runnables) {
                threadPool.submit(new Runnable() {
                    public void run() {
                        allExecutorThreadsReady.countDown();
                        try {
                            afterInitBlocker.await();
                            submittedTestRunnable.run();
                        } catch (final Throwable e) {
                            exceptions.add(e);
                        } finally {
                            allDone.countDown();
                        }
                    }
                });
            }
            // wait until all threads are ready
            assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent", allExecutorThreadsReady.await(runnables.size() * 10, TimeUnit.MILLISECONDS));
            // start all test runners
            afterInitBlocker.countDown();
            assertTrue(message + " timeout! More than" + maxTimeoutSeconds + "seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
        } finally {
            threadPool.shutdownNow();
        }
        assertTrue(message + "failed with exception(s)" + exceptions, exceptions.isEmpty());
    }

    public void saveResult(Description description, UUID uuid) {
//        FailedRecord failedRecord = new FailedRecord();
//        failedRecord.setFailedTestMethod(description.getMethodName());
//        failedRecord.setUuid(uuid.toString());
//        failedRecordRepo.save(failedRecord);
    }

    public void initTestUser() {
        if (testUser.size() == 0) {
            log.debug("start of creating test users");
            ResourceOwner resourceOwner1 = randomRegisterAnUser();
            ResourceOwner resourceOwner2 = randomRegisterAnUser();
            ResourceOwner resourceOwner3 = randomRegisterAnUser();
            ResourceOwner resourceOwner4 = randomRegisterAnUser();
            ResourceOwner resourceOwner5 = randomRegisterAnUser();
            ResourceOwner resourceOwner6 = randomRegisterAnUser();
            ResourceOwner resourceOwner7 = randomRegisterAnUser();
            ResourceOwner resourceOwner8 = randomRegisterAnUser();
            ResourceOwner resourceOwner9 = randomRegisterAnUser();
            ResourceOwner resourceOwner10 = randomRegisterAnUser();
            testUser.add(resourceOwner1);
            testUser.add(resourceOwner2);
            testUser.add(resourceOwner3);
            testUser.add(resourceOwner4);
            testUser.add(resourceOwner5);
            testUser.add(resourceOwner6);
            testUser.add(resourceOwner7);
            testUser.add(resourceOwner8);
            testUser.add(resourceOwner9);
            testUser.add(resourceOwner10);
            log.debug("end of creating test users");
        } else {
            log.debug("test users already exist");

        }
    }

    /**
     * @return different GRANT_TYPE_PASSWORD client obj
     */
    public Client getClientAsNonResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        client.setResourceIndicator(false);
        Set<ClientType> strings = new HashSet<>();
        strings.add(ClientType.BACKEND_APP);
        strings.add(ClientType.FIRST_PARTY);
        client.setTypes(strings);
        return client;
    }

    public Client getClientAsResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        client.setResourceIndicator(true);
        Set<ClientType> strings = new HashSet<>();
        strings.add(ClientType.BACKEND_APP);
        strings.add(ClientType.FIRST_PARTY);
        client.setTypes(strings);
        return client;
    }

    public Client getInvalidClientAsResource(String... resourceIds) {
        Client client = getClientRaw(resourceIds);
        Set<ClientType> strings = new HashSet<>();
        strings.add(ClientType.FRONTEND_APP);
        strings.add(ClientType.FIRST_PARTY);
        client.setTypes(strings);
        client.setResourceIndicator(true);
        return client;
    }

    public Client getClientRaw(String... resourceIds) {
        Client client = new Client();
        client.setName(UUID.randomUUID().toString().replace("-", ""));
        client.setClientSecret(UUID.randomUUID().toString().replace("-", ""));
        client.setGrantTypeEnums(new HashSet<>(Arrays.asList(GrantTypeEnum.PASSWORD)));
        client.setAccessTokenValiditySeconds(1800);
        client.setRefreshTokenValiditySeconds(null);
        client.setHasSecret(true);
        client.setResourceIds(new HashSet<>(Arrays.asList(resourceIds)));
        return client;
    }


    public ResponseEntity<DefaultOAuth2AccessToken> getTokenResponse(String grantType, String username, String userPwd, String clientId, String clientSecret) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("password", userPwd);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(UserAction.PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }


    public ResponseEntity<String> createClient(Client client) {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, CLIENT_ID_LOGIN_ID, EMPTY_CLIENT_SECRET);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        HttpEntity<Client> request = new HttpEntity<>(client, headers);
        return restTemplate.exchange(URL2, HttpMethod.POST, request, String.class);
    }

    public ResponseEntity<String> createClient(Client client, String changeId) {
        ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = getTokenResponse(GRANT_TYPE_PASSWORD, ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT, CLIENT_ID_LOGIN_ID, EMPTY_CLIENT_SECRET);
        String bearer = tokenResponse.getBody().getValue();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearer);
        headers.set("changeId", changeId);
        headers.set("X-XSRF-TOKEN", "123");
        headers.add(HttpHeaders.COOKIE, "XSRF-TOKEN=123");
        HttpEntity<Client> request = new HttpEntity<>(client, headers);
        return restTemplate.exchange(URL2, HttpMethod.POST, request, String.class);
    }

    public ResourceOwner randomCreateUserDraft() {
        return userCreateDraft(UUID.randomUUID().toString().replace("-", "") + "@gmail.com", UUID.randomUUID().toString().replace("-", ""));
    }

    public ResourceOwner userCreateDraft(String username, String password) {
        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setEmail(username);
        resourceOwner.setPassword(password);
        return resourceOwner;
    }

    public ResourceOwner randomRegisterAnUser() {
        ResourceOwner random = randomCreateUserDraft();
        registerAnUser(random);
        return random;
    }

    public ResponseEntity<DefaultOAuth2AccessToken> getRegisterToken() {
        return getJwtClientCredential(CLIENT_ID_REGISTER_ID, EMPTY_CLIENT_SECRET);
    }

    public ResponseEntity<DefaultOAuth2AccessToken> registerAnUser(ResourceOwner user) {
        ResponseEntity<DefaultOAuth2AccessToken> registerTokenResponse = getRegisterToken();
        PendingResourceOwner pendingResourceOwner = new PendingResourceOwner();
        createPendingUser(user, registerTokenResponse.getBody().getValue(), pendingResourceOwner);
        return enterActivationCode(user, registerTokenResponse.getBody().getValue(), pendingResourceOwner);
    }

    public ResponseEntity<DefaultOAuth2AccessToken> enterActivationCode(ResourceOwner user, String registerToken, PendingResourceOwner pendingResourceOwner) {
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.setBearerAuth(registerToken);
        headers1.set("changeId", UUID.randomUUID().toString());
        pendingResourceOwner.setPassword(user.getPassword());
        pendingResourceOwner.setActivationCode("123456");
        HttpEntity<PendingResourceOwner> request1 = new HttpEntity<>(pendingResourceOwner, headers1);
        return restTemplate.exchange(helper.getAccessUrl("/users"), HttpMethod.POST, request1, DefaultOAuth2AccessToken.class);
    }

    public ResponseEntity<Void> createPendingUser(ResourceOwner user, String registerToken, PendingResourceOwner pendingResourceOwner) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(registerToken);
        headers.set("changeId", UUID.randomUUID().toString());
        pendingResourceOwner.setEmail(user.getEmail());
        HttpEntity<PendingResourceOwner> request = new HttpEntity<>(pendingResourceOwner, headers);
        return restTemplate.exchange(helper.getAccessUrl("/pending-users"), HttpMethod.POST, request, Void.class);
    }

    public String registerResourceOwnerThenLogin() {
        ResourceOwner randomResourceOwner = randomCreateUserDraft();
        registerAnUser(randomResourceOwner);
        ResponseEntity<DefaultOAuth2AccessToken> loginTokenResponse = getJwtPassword(randomResourceOwner.getEmail(), randomResourceOwner.getPassword());
        return loginTokenResponse.getBody().getValue();
    }

    public ResponseEntity<DefaultOAuth2AccessToken> getJwtPassword(String username, String userPwd) {
        return getJwtPasswordWithClient(CLIENT_ID_LOGIN_ID, EMPTY_CLIENT_SECRET, username, userPwd);
    }

    public ResponseEntity<DefaultOAuth2AccessToken> getJwtPasswordWithClient(String clientId, String clientSecret, String username, String userPwd) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE_PASSWORD);
        params.add("username", username);
        params.add("password", userPwd);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }

    public ResponseEntity<DefaultOAuth2AccessToken> getJwtPasswordRoot() {
        return getJwtPassword(ACCOUNT_USERNAME_ROOT, ACCOUNT_PASSWORD_ROOT);
    }

    public ResponseEntity<DefaultOAuth2AccessToken> getJwtPasswordUser() {
        return getJwtPassword(ACCOUNT_USERNAME_USER, ACCOUNT_PASSWORD_USER);
    }

    public ResponseEntity<DefaultOAuth2AccessToken> getJwtClientCredential(String clientId, String clientSecret) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE_CLIENT_CREDENTIALS);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(URL, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }

    public String getAuthorizationCodeTokenForUserAndClient(String username, String pwd, String clientId, String redirectUri) {
        ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = getJwtPassword(username, pwd);
        String accessToken = defaultOAuth2AccessTokenResponseEntity.getBody().getValue();
        ResponseEntity<String> codeResp = getCodeResp(clientId, accessToken, redirectUri);
        String code = JsonPath.read(codeResp.getBody(), "$.authorize_code");

        ResponseEntity<DefaultOAuth2AccessToken> authorizationToken = getAuthorizationToken(code, redirectUri, clientId, EMPTY_CLIENT_SECRET);

        DefaultOAuth2AccessToken body = authorizationToken.getBody();
        return body.getValue();
    }

    public ResponseEntity<String> getCodeResp(String clientId, String bearerToken, String redirectUri) {
        String url = UserAction.proxyUrl + UserAction.SVC_NAME_AUTH + "/authorize";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("response_type", AUTHORIZE_RESPONSE_TYPE);
        params.add("client_id", clientId);
        params.add("state", AUTHORIZE_STATE);
        params.add("redirect_uri", redirectUri);
        params.add("project_id", PROJECT_ID);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    public ResponseEntity<DefaultOAuth2AccessToken> getAuthorizationToken(String code, String redirect_uri, String clientId, String clientSecret) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE_AUTHORIZATION_CODE);
        params.add("code", code);
        params.add("redirect_uri", redirect_uri);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(UserAction.PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }


    public HttpEntity getHttpRequest(String authorizeToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        headers.setBearerAuth(authorizeToken);
        return new HttpEntity<>(headers);
    }


}
