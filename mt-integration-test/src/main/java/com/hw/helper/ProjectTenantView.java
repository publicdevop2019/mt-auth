package com.hw.helper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectTenantView {
    private  String id;
    private  String createdBy;
    private  Long createdAt;
    private String creatorName;
    private String name;
    private long totalClient;
    private long totalEndpoint;
    private long totalUserOwned;
    private long totalPermissionCreated;
    private long totalRoleCreated;

}
