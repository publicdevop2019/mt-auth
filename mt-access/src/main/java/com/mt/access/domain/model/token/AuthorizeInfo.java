package com.mt.access.domain.model.token;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;

@Data
public class AuthorizeInfo implements Serializable {
    private static final long serialVersionUID = 1;

    private String redirectUri;
    private ClientId clientId;
    private Set<String> scope;
    private ProjectId projectId;
    private Set<PermissionId> permissionIds;
    private UserId userId;
}
