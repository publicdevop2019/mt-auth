package com.mt.helper.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectTenantView {
    private String id;
    private String createdBy;
    private Long createdAt;
    private String creatorName;
    private String name;
    private Long totalClient;
    private Long totalEndpoint;
    private Long totalUserOwned;
    private Long totalPermissionCreated;
    private Long totalRoleCreated;

}
