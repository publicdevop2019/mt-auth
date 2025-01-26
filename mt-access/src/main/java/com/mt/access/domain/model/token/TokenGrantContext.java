package com.mt.access.domain.model.token;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.access.domain.model.user.LoginUser;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserMobile;
import com.mt.access.domain.model.user.event.MfaDeliverMethod;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.validate.Utility;
import lombok.Data;

@Data
public class TokenGrantContext {
    private Boolean newUserRequired;
    private Boolean recordLoginRequired;
    private MfaDeliverMethod deliveryMethod;
    private User mfaUser;
    private Boolean triggerMfaRequired;
    private Boolean triggerDefaultMfaRequired;
    private LoginResult loginResult;
    private LoginUser loginUser;
    private TokenGrantClient client;
    private JwtToken jwtToken;
    private ClientId clientId;
    private UserId userId;
    private String clientSecret;
    private String agentInfo;
    private String ipAddress;
    private String changeId;
    private TokenGrantType grantType;
    private ProjectId scope;
    private String refreshToken;
    private ProjectId viewTenantId;
    private LoginType type;
    private String code;//verification code or authorization code
    private UserEmail email;
    private String username;
    private String password;
    private String mfaMethod;
    private String mfaCode;
    private String redirectUri;
    private UserMobile userMobile;


    public TokenGrantContext(ClientId clientId, String clientSecret, String agentInfo,
                             String ipAddress, String changeId, TokenGrantType grantType,
                             String scope,
                             String refreshToken,
                             String viewTenantId, String type, String mobileNumber,
                             String countryCode, String code, String email, String username,
                             String password, String mfaMethod, String mfaCode,
                             String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.agentInfo = agentInfo;
        this.ipAddress = ipAddress;
        this.changeId = changeId;
        this.scope = Utility.isNull(scope) ? null : new ProjectId(scope);
        this.refreshToken = refreshToken;
        this.type = Utility.isNull(type) ? null : LoginType.parse(type);
        this.code = code;
        this.email = Utility.isNull(email) ? null : new UserEmail(email);
        this.username = username;
        this.password = password;
        this.mfaMethod = mfaMethod;
        this.mfaCode = mfaCode;
        this.redirectUri = redirectUri;
        this.grantType = grantType;
        this.viewTenantId =
            Utility.isBlank(viewTenantId) ? null : new ProjectId(viewTenantId);
        if (Utility.notNull(countryCode) && Utility.notNull(mobileNumber)) {
            this.userMobile =
                new UserMobile(countryCode, mobileNumber);
        }

    }

    public ProjectId getParsedScope() {
        return Utility.notNull(scope) ? scope :
            new ProjectId(AppConstant.MAIN_PROJECT_ID);
    }


}
