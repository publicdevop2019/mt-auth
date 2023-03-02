package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.LoginHistory;
import com.mt.access.domain.model.user.LoginInfo;
import com.mt.access.domain.model.user.User;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class UserMngmntRepresentation {
    private String id;

    private String email;
    private boolean locked;
    private Set<String> grantedAuthorities;
    private String createdBy;

    private Long createdAt;

    private String modifiedBy;

    private Long modifiedAt;
    private boolean subscription;

    private List<UserLoginHistory> loginHistory;

    public UserMngmntRepresentation(User user, Set<LoginHistory> loginInfoList) {
        this.id = user.getUserId().getDomainId();
        this.email = user.getEmail().getEmail();
        this.locked = user.isLocked();
        this.createdBy = user.getCreatedBy();
        this.createdAt = user.getCreatedAt().getTime();
        this.modifiedBy = user.getModifiedBy();
        this.modifiedAt = user.getModifiedAt().getTime();
        this.loginHistory =
            loginInfoList.stream().map(UserLoginHistory::new)
                .sorted((a, b) -> (int) (a.loginAt - b.loginAt)).collect(
                    Collectors.toList());
    }

    @Data
    private static class UserLoginHistory {
        private long loginAt;
        private String ipAddress;
        private String agent;

        public UserLoginHistory(LoginHistory info) {
            this.loginAt = info.getLoginAt().getTime();
            this.ipAddress = info.getIpAddress();
            this.agent = info.getAgent();
        }
    }
}
