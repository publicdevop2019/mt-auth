package com.mt.access.resource;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;
import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.token.representation.JwtTokenRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.token.TokenGrantContext;
import com.mt.access.domain.model.token.TokenGrantType;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.access.infrastructure.HttpUtility;
import com.mt.common.domain.model.validate.Utility;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TokenResource {
    private static final String MFA_REQUIRED = "mfa required";

    /**
     * token endpoint to track last login.
     *
     * @param parameters     oauth2 params
     * @param servletRequest http request
     * @param agentInfo      User-Agent header
     * @return oauth2 token
     */
    @PostMapping(path = "/oauth/token")
    public ResponseEntity<?> getToken(
        @RequestParam Map<String, String> parameters,
        HttpServletRequest servletRequest,
        @RequestHeader(name = "Authorization") String authorization,
        @RequestHeader(name = "User-Agent") String agentInfo,
        @RequestHeader(name = HTTP_HEADER_CHANGE_ID, required = false) String changeId
    ) {
        String basic = authorization.replace("Basic ", "");
        String decoded = new String(Base64.getDecoder().decode(basic));
        String[] split = decoded.split(":");
        String clientId;
        String clientSecret;
        if (split.length == 0) {
            clientId = "";
            clientSecret = "";
        } else if (split.length == 1) {
            clientId = split[0];
            clientSecret = "";
        } else {
            clientId = split[0];
            clientSecret = decoded.split(":")[1];
        }
        String ipAddress = HttpUtility.getClientIpAddress(servletRequest);
        log.info("token acquire with ip {}", ipAddress);
        String grantType = parameters.get("grant_type");
        String scope =
            "not_used".equalsIgnoreCase(parameters.get("scope")) ? null : parameters.get("scope");
        String refreshToken = parameters.get("refresh_token");
        String viewTenantId = parameters.get("view_tenant_id");
        String type = parameters.get("type");
        String mobileNumber = parameters.get("mobile_number");
        String countryCode = parameters.get("country_code");
        String code = parameters.get("code");
        String email = parameters.get("email");
        String username = parameters.get("username");
        String password = parameters.get("password");
        String mfaMethod = parameters.get("mfa_method");
        String mfaCode = parameters.get("mfa_code");
        String redirectUri = parameters.get("redirect_uri");
        TokenGrantContext context =
            ApplicationServiceRegistry.getTokenApplicationService()
                .grantToken(
                    clientId,
                    clientSecret,
                    agentInfo,
                    ipAddress,
                    changeId,
                    grantType,
                    scope,
                    refreshToken,
                    viewTenantId,
                    type,
                    mobileNumber,
                    countryCode,
                    code,
                    email,
                    username,
                    password,
                    mfaMethod,
                    mfaCode,
                    redirectUri
                );
        LoginResult result = context.getLoginResult();
        if (Utility.notNull(result) && Utility.isFalse(result.getAllowed())) {
            if (Utility.isTrue(result.getInvalidMfa())) {
                return ResponseEntity.badRequest().build();
            } else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("message", MFA_REQUIRED);
                map.put("partialMobile", result.getPartialMobile());
                map.put("partialEmail", result.getPartialEmail());
                if (result.isSelectRequired()) {
                    map.put("deliveryMethod", true);
                }
                return ResponseEntity.ok().body(map);
            }
        } else {
            if (context.getGrantType().equals(TokenGrantType.PASSWORD)) {
                return ResponseEntity.ok()
                    .header("Location", context.getLoginUser().getUserId().getDomainId())
                    .body(new JwtTokenRepresentation(context.getJwtToken()));
            } else {
                return ResponseEntity.ok(new JwtTokenRepresentation(context.getJwtToken()));
            }
        }
    }

    /**
     * entry point for authorization flow.
     *
     * @param parameters request params
     * @param jwt        jwt generated
     * @return authorization flow response
     */
    @PostMapping("/authorize")
    public Map<String, String> authorize(
        @RequestParam Map<String, String> parameters,
        @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt
    ) {
        DomainRegistry.getCurrentUserService().setUserJwt(jwt);
        HashMap<String, String> response = new HashMap<>();
        String clientId = parameters.get("client_id");
        String responseType = parameters.get("response_type");
        String redirectUri = parameters.get("redirect_uri");
        String projectId = parameters.get("project_id");
        String code = ApplicationServiceRegistry.getTokenApplicationService()
            .authorize(projectId, clientId, responseType, redirectUri);
        response.put("authorize_code", code);
        return response;
    }
}
