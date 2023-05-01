package com.hw.helper;

import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserMgmt {
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


    @Data
    private static class UserLoginHistory {
        private long loginAt;
        private String ipAddress;
        private String agent;

    }
}
