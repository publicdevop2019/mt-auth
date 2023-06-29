package com.mt.test_case.helper.utility;

import com.jayway.jsonpath.JsonPath;
import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.TenantContext;
import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.User;
import java.util.Objects;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * utility for oauth2.
 */
public class OAuth2Utility {

    public static final String MFA_CODE = "654321";

    /**
     * get oauth2 password response.
     *
     * @param clientId     client id
     * @param clientSecret client secret
     * @param username     login username
     * @param userPwd      login password
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getOAuth2PasswordToken(
        String clientId,
        String clientSecret,
        String username,
        String userPwd
    ) {
        return getOAuth2WithUser(AppConstant.GRANT_TYPE_PASSWORD, clientId, clientSecret, username, userPwd);
    }

    /**
     * get oauth2 password response.
     *
     * @param client  client
     * @param user    user
     * @param context tenant context
     * @return oauth2 password token
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getTenantPasswordToken(
        Client client,
        User user,
        TenantContext context
    ) {
        return getOAuth2WithUser(AppConstant.GRANT_TYPE_PASSWORD, client.getId(), client.getClientSecret(),
            user.getEmail(), user.getPassword(),
            context.getProject().getId());
    }

    /**
     * get oauth2 client credential response.
     *
     * @param clientId     client id
     * @param clientSecret client secret
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getOAuth2ClientCredentialToken(
        String clientId, String clientSecret
    ) {
        return getOAuth2WithClient(AppConstant.GRANT_TYPE_CLIENT_CREDENTIALS, clientId, clientSecret);
    }

    /**
     * get code value for authorization flow.
     *
     * @param clientId    client id
     * @param bearerToken user jwt bearer token
     * @param redirectUri redirect uri
     * @return code raw response
     */
    public static ResponseEntity<String> getOAuth2AuthorizationCode(
        String clientId,
        String bearerToken,
        String redirectUri
    ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("response_type", AppConstant.AUTHORIZE_RESPONSE_TYPE);
        params.add("client_id", clientId);
        params.add("state", AppConstant.AUTHORIZE_STATE);
        params.add("redirect_uri", redirectUri);
        params.add("project_id", AppConstant.MT_ACCESS_PROJECT_ID);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        String url = AppConstant.PROXY_URL + AppConstant.SVC_NAME_AUTH + "/authorize";
        return TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, String.class);
    }

    /**
     * single sign on with authorization flow.
     *
     * @param projectId       project id
     * @param clientId        client id
     * @param userBearerToken user jwt bearer token
     * @param redirectUri     redirect uri
     * @return code raw response
     */
    public static ResponseEntity<String> authorizeLogin(
        String projectId,
        String clientId,
        String userBearerToken,
        String redirectUri
    ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("response_type", AppConstant.AUTHORIZE_RESPONSE_TYPE);
        params.add("client_id", clientId);
        params.add("state", AppConstant.AUTHORIZE_STATE);
        params.add("redirect_uri", redirectUri);
        params.add("project_id", projectId);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userBearerToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        String url = AppConstant.PROXY_URL + AppConstant.SVC_NAME_AUTH + "/authorize";
        return TestContext.getRestTemplate().exchange(url, HttpMethod.POST, request, String.class);
    }

    /**
     * get token for authorization flow after code generated.
     *
     * @param code         generated code
     * @param redirectUri  redirect uri
     * @param clientId     client id
     * @param clientSecret client secret
     * @return token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getOAuth2AuthorizationToken(
        String code,
        String redirectUri,
        String clientId,
        String clientSecret
    ) {
        return getOAuth2WithCode(AppConstant.GRANT_TYPE_AUTHORIZATION_CODE, code, redirectUri, clientId,
            clientSecret);
    }

    /**
     * get token for specific grant after code generated.
     *
     * @param grantType    grant type
     * @param code         generated code
     * @param redirectUri  redirect uri
     * @param clientId     client id
     * @param clientSecret client secret
     * @return token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getOAuth2WithCode(
        String grantType,
        String code,
        String redirectUri,
        String clientId,
        String clientSecret) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return TestContext.getRestTemplate()
            .exchange(AppConstant.PROXY_URL_TOKEN, HttpMethod.POST, request,
                DefaultOAuth2AccessToken.class);
    }

    /**
     * get oauth2 response for specific grant type.
     *
     * @param grantType    grant type
     * @param clientId     client id
     * @param clientSecret client secret
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getOAuth2WithClient(
        String grantType, String clientId, String clientSecret
    ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return TestContext.getRestTemplate()
            .exchange(AppConstant.PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }

    /**
     * get oauth2 password response, also taken care of mfa if required.
     *
     * @param grantType    grant type
     * @param clientId     client id
     * @param clientSecret client secret
     * @param username     login username
     * @param userPwd      login password
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getOAuth2WithUser(
        String grantType,
        String clientId,
        String clientSecret,
        String username,
        String userPwd
    ) {
        return getOAuth2WithUser(grantType, clientId, clientSecret, username, userPwd, "not_used");
    }

    /**
     * get oauth2 password response, also taken care of mfa if required.
     *
     * @param grantType    grant type
     * @param clientId     client id
     * @param clientSecret client secret
     * @param username     login username
     * @param userPwd      login password
     * @param tenantId     tenant project id
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getOAuth2WithUser(
        String grantType,
        String clientId,
        String clientSecret,
        String username,
        String userPwd,
        String tenantId
    ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("password", userPwd);
        params.add("scope", tenantId);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = TestContext.getRestTemplate()
            .exchange(AppConstant.PROXY_URL_TOKEN, HttpMethod.POST, request,
                DefaultOAuth2AccessToken.class);
        //avoid duplicate calls when request error or server error
        if (exchange.getStatusCode().is4xxClientError()) {
            return exchange;
        }
        if (exchange.getStatusCode().is5xxServerError()) {
            return exchange;
        }
        if (Objects.requireNonNull(exchange.getBody()).getValue() != null) {
            return exchange;
        } else {
            String mfaId = (String) exchange.getBody().getAdditionalInformation().get("mfaId");
            return getOAuth2WithUserMfa(grantType, clientId, clientSecret, username, userPwd,
                mfaId, MFA_CODE);
        }
    }

    /**
     * get oauth2 password response with mfa info.
     *
     * @param grantType    grant type
     * @param clientId     client id
     * @param clientSecret client secret
     * @param username     login username
     * @param userPwd      login password
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getOAuth2WithUserMfa(
        String grantType,
        String clientId,
        String clientSecret,
        String username,
        String userPwd,
        String mfaId,
        String mfaCode
    ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("password", userPwd);
        params.add("scope", "not_used");
        params.add("mfa_id", mfaId);
        params.add("mfa_code", mfaCode);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return TestContext.getRestTemplate()
            .exchange(AppConstant.PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }

    /**
     * parse code string after authorization call.
     *
     * @param responseEntity authorization call response
     * @return code
     */
    public static String getAuthorizationCode(ResponseEntity<String> responseEntity) {
        String body = responseEntity.getBody();
        return JsonPath.read(body, "$.authorize_code");
    }

    /**
     * get refresh token response
     *
     * @param refreshToken refresh token
     * @param clientId     client id
     * @param clientSecret client secret
     * @return oauth2 token
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getRefreshTokenResponse(
        String refreshToken,
        String clientId,
        String clientSecret) {
        return getRefreshTokenResponse(refreshToken, clientId, clientSecret, "not_used");
    }

    /**
     * get refresh token response
     *
     * @param refreshToken refresh token
     * @param client client
     * @param tenantContext tenant context
     * @return oauth2 token
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getTenantRefreshToken(
        String refreshToken,
        Client client,
        TenantContext tenantContext
    ) {
        return getRefreshTokenResponse(refreshToken, client.getId(), client.getClientSecret(), tenantContext.getProject().getId());
    }

    /**
     * get refresh token response
     *
     * @param refreshToken refresh token
     * @param clientId     client id
     * @param clientSecret client secret
     * @param projectId    project id
     * @return oauth2 token
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getRefreshTokenResponse(
        String refreshToken,
        String clientId,
        String clientSecret,
        String projectId
    ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);
        params.add("scope", projectId);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return TestContext.getRestTemplate()
            .exchange(AppConstant.PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }
}
