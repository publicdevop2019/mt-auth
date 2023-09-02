package com.mt.access.domain.model.token;

import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import java.util.Set;
import lombok.Data;

@Data
public class JwtToken {
    private String id;
    private String signedAccessToken;
    private Long accessTokenValidityInSecond;
    private Long issueAtMilli;
    private Set<PermissionId> permissionIds;
    private ProjectId projectId;
    private String signedRefreshToken;
    private Set<ProjectId> tenantIds;
    private ProjectId viewTenantId;
    private UserId userId;
}
