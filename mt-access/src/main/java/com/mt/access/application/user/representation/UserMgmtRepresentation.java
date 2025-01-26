package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.LoginHistory;
import com.mt.access.domain.model.user.User;
import com.mt.common.domain.model.validate.Utility;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class UserMgmtRepresentation {
    private String id;

    private String email;
    private Boolean locked;
    private Set<String> grantedAuthorities;
    private String createdBy;

    private Long createdAt;

    private String modifiedBy;

    private Long modifiedAt;
    private Boolean subscription;

    private List<UserLoginHistory> loginHistory;

    public UserMgmtRepresentation(User user, Set<LoginHistory> loginInfoList) {
        this.id = user.getUserId().getDomainId();
        this.email = Utility.notNull(user.getEmail()) ? user.getEmail().getEmail() : null;
        this.locked = user.getLocked();
        this.createdBy = user.getCreatedBy();
        this.createdAt = user.getCreatedAt();
        this.modifiedBy = user.getModifiedBy();
        this.modifiedAt = user.getModifiedAt();
        this.loginHistory =
            loginInfoList.stream().map(UserLoginHistory::new)
                .sorted((a, b) -> (int) (a.loginAt - b.loginAt)).collect(
                    Collectors.toList());
    }

    @Data
    private static class UserLoginHistory {
        private Long loginAt;
        private String ipAddress;
        private String agent;

        public UserLoginHistory(LoginHistory info) {
            this.loginAt = info.getLoginAt();
            this.ipAddress = info.getIpAddress();
            this.agent = info.getAgent();
        }
    }
}
