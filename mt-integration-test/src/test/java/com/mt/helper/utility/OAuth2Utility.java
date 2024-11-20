package com.mt.helper.utility;

import com.jayway.jsonpath.JsonPath;
import com.mt.helper.AppConstant;
import com.mt.helper.TenantContext;
import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.User;
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

    /**
     * get oauth2 password response.
     *
     * @param clientId     client id
     * @param clientSecret client secret
     * @param email        login email
     * @param userPwd      login password
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getPasswordFlowEmailPwdToken(
        String clientId,
        String clientSecret,
        String email,
        String userPwd
    ) {
        return getTokenWithUserEmailPwd(AppConstant.GRANT_TYPE_PASSWORD, clientId, clientSecret,
            email,
            userPwd);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getPasswordFlowEmailCodeToken(
        String clientId,
        String clientSecret,
        String email,
        String code
    ) {
        return getTokenWithUserEmailCode(AppConstant.GRANT_TYPE_PASSWORD, clientId, clientSecret,
            email,
            code);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getPasswordFlowMobileCodeToken(
        String clientId,
        String clientSecret,
        String countryCode,
        String mobileNumber,
        String code
    ) {
        return getTokenWithUserMobileCode(AppConstant.GRANT_TYPE_PASSWORD, clientId, clientSecret,
            countryCode, mobileNumber,
            code);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getPasswordFlowUsernamePwdToken(
        String clientId,
        String clientSecret,
        String username,
        String pwd
    ) {
        return getTokenWithUserUsernamePwd(AppConstant.GRANT_TYPE_PASSWORD, clientId, clientSecret,
            username,
            pwd);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getPasswordFlowMobilePwdToken(
        String clientId,
        String clientSecret,
        String countryCode,
        String mobileNumber,
        String pwd
    ) {
        return getTokenWithUserMobilePwd(AppConstant.GRANT_TYPE_PASSWORD, clientId, clientSecret,
            countryCode, mobileNumber,
            pwd);
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
        return getPasswordToken(AppConstant.GRANT_TYPE_PASSWORD, client.getId(),
            client.getClientSecret(),
            user.getEmail(), null, null, null, user.getPassword(), null,
            context.getProject().getId(), AppConstant.LOGIN_TYPE_EMAIL_PWD);
    }

    /**
     * get oauth2 client credential response.
     *
     * @param clientId     client id
     * @param clientSecret client secret
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getClientCredentialToken(
        String clientId, String clientSecret
    ) {
        return getToken(AppConstant.GRANT_TYPE_CLIENT_CREDENTIALS, clientId,
            clientSecret);
    }

    /**
     * get code value for authorization flow.
     *
     * @param clientId    client id
     * @param bearerToken user jwt bearer token
     * @param redirectUri redirect uri
     * @return code raw response
     */
    public static ResponseEntity<String> getAuthorizationCode(
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
    public static ResponseEntity<DefaultOAuth2AccessToken> getAuthorizationToken(
        String code,
        String redirectUri,
        String clientId,
        String clientSecret
    ) {
        return getCode(AppConstant.GRANT_TYPE_AUTHORIZATION_CODE, code, redirectUri,
            clientId,
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
    public static ResponseEntity<DefaultOAuth2AccessToken> getCode(
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
    public static ResponseEntity<DefaultOAuth2AccessToken> getToken(
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
            .exchange(AppConstant.PROXY_URL_TOKEN, HttpMethod.POST, request,
                DefaultOAuth2AccessToken.class);
    }

    /**
     * get oauth2 password response, also taken care of mfa if required.
     *
     * @param grantType    grant type
     * @param clientId     client id
     * @param clientSecret client secret
     * @param email        login username
     * @param pwd          login password
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getTokenWithUserEmailPwd(
        String grantType,
        String clientId,
        String clientSecret,
        String email,
        String pwd
    ) {
        return getPasswordToken(grantType, clientId, clientSecret, email, null, null, null,
            pwd, null, "not_used", AppConstant.LOGIN_TYPE_EMAIL_PWD);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getTokenWithUserEmailCode(
        String grantType,
        String clientId,
        String clientSecret,
        String email,
        String code
    ) {
        return getPasswordToken(grantType, clientId, clientSecret, email, null, null, null,
            null, code, "not_used", AppConstant.LOGIN_TYPE_EMAIL_CODE);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getTokenWithUserMobileCode(
        String grantType,
        String clientId,
        String clientSecret,
        String countryCode,
        String mobileNumber,
        String code
    ) {
        return getPasswordToken(grantType, clientId, clientSecret, null, countryCode, mobileNumber,
            null,
            null, code, "not_used", AppConstant.LOGIN_TYPE_MOBILE_CODE);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getTokenWithUserUsernamePwd(
        String grantType,
        String clientId,
        String clientSecret,
        String username,
        String pwd
    ) {
        return getPasswordToken(grantType, clientId, clientSecret, null, null, null, username,
            pwd, null, "not_used", AppConstant.LOGIN_TYPE_USERNAME_PWD);
    }

    public static ResponseEntity<DefaultOAuth2AccessToken> getTokenWithUserMobilePwd(
        String grantType,
        String clientId,
        String clientSecret,
        String countryCode,
        String mobileNumber,
        String pwd
    ) {
        return getPasswordToken(grantType, clientId, clientSecret, null, countryCode, mobileNumber,
            null,
            pwd, null, "not_used", AppConstant.LOGIN_TYPE_MOBILE_PWD);
    }

    /**
     * get oauth2 password response, also taken care of mfa if required.
     *
     * @param grantType    grant type
     * @param clientId     client id
     * @param clientSecret client secret
     * @param email        login email
     * @param countryCode  login country code
     * @param mobileNumber login mobile number
     * @param username     login username
     * @param pwd          login password
     * @param code         login code
     * @param tenantId     tenant project id
     * @param type         mobile_w_pwd, email_w_pwd, username_w_pwd, email_w_code, mobile_w_code
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getPasswordToken(
        String grantType,
        String clientId,
        String clientSecret,
        String email,
        String countryCode,
        String mobileNumber,
        String username,
        String pwd,
        String code,
        String tenantId,
        String type
    ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("email", email);
        params.add("mobile_number", mobileNumber);
        params.add("country_code", countryCode);
        params.add("password", pwd);
        params.add("code", code);
        params.add("type", type);
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
            Boolean deliveryMethod =
                (Boolean) exchange.getBody().getAdditionalInformation().get("deliveryMethod");
            if (deliveryMethod != null && deliveryMethod) {
                ResponseEntity<DefaultOAuth2AccessToken> exchange2 =
                    getTokenWithUserMfa(grantType, clientId, clientSecret, email, countryCode,
                        mobileNumber, username, pwd,
                        null, "email", type);
            }
            return getTokenWithUserMfa(grantType, clientId, clientSecret, email, countryCode,
                mobileNumber, username, pwd,
                AppConstant.MFA_CODE, null, type);
        }
    }

    /**
     * get oauth2 password response with mfa info.
     *
     * @return oauth2 token raw response
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getTokenWithUserMfa(
        String grantType,
        String clientId,
        String clientSecret,
        String email,
        String countryCode,
        String mobileNumber,
        String username,
        String pwd,
        String mfaCode,
        String mfaMethod,
        String type
    ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("username", username);
        params.add("email", email);
        params.add("country_code", countryCode);
        params.add("mobile_number", mobileNumber);
        params.add("password", pwd);
        params.add("scope", "not_used");
        params.add("mfa_code", mfaCode);
        params.add("mfa_method", mfaMethod);
        params.add("type", type);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return TestContext.getRestTemplate()
            .exchange(AppConstant.PROXY_URL_TOKEN, HttpMethod.POST, request,
                DefaultOAuth2AccessToken.class);
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
     * @param refreshToken  refresh token
     * @param client        client
     * @param tenantContext tenant context
     * @return oauth2 token
     */
    public static ResponseEntity<DefaultOAuth2AccessToken> getTenantRefreshToken(
        String refreshToken,
        Client client,
        TenantContext tenantContext
    ) {
        return getRefreshTokenResponse(refreshToken, client.getId(), client.getClientSecret(),
            tenantContext.getProject().getId());
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
            .exchange(AppConstant.PROXY_URL_TOKEN, HttpMethod.POST, request,
                DefaultOAuth2AccessToken.class);
    }
}
