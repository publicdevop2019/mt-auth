package com.hw.helper.utility;

import static com.hw.helper.AppConstant.AUTHORIZE_RESPONSE_TYPE;
import static com.hw.helper.AppConstant.AUTHORIZE_STATE;
import static com.hw.helper.AppConstant.GRANT_TYPE_AUTHORIZATION_CODE;
import static com.hw.helper.AppConstant.GRANT_TYPE_CLIENT_CREDENTIALS;
import static com.hw.helper.AppConstant.GRANT_TYPE_PASSWORD;
import static com.hw.helper.AppConstant.PROJECT_ID;
import static com.hw.helper.AppConstant.PROXY_URL_TOKEN;
import static com.hw.helper.AppConstant.proxyUrl;

import com.hw.helper.AppConstant;
import com.jayway.jsonpath.JsonPath;
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
        return getOAuth2WithUser(GRANT_TYPE_PASSWORD, clientId, clientSecret, username, userPwd);
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
        return getOAuth2WithClient(GRANT_TYPE_CLIENT_CREDENTIALS, clientId, clientSecret);
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
        params.add("response_type", AUTHORIZE_RESPONSE_TYPE);
        params.add("client_id", clientId);
        params.add("state", AUTHORIZE_STATE);
        params.add("redirect_uri", redirectUri);
        params.add("project_id", PROJECT_ID);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        String url = proxyUrl + AppConstant.SVC_NAME_AUTH + "/authorize";
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
        return getOAuth2WithCode(GRANT_TYPE_AUTHORIZATION_CODE, code, redirectUri, clientId,
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
            .exchange(PROXY_URL_TOKEN, HttpMethod.POST, request,
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
            .exchange(PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("password", userPwd);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<DefaultOAuth2AccessToken> exchange = TestContext.getRestTemplate()
            .exchange(PROXY_URL_TOKEN, HttpMethod.POST, request,
                DefaultOAuth2AccessToken.class);
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
            .exchange(PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
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
}
