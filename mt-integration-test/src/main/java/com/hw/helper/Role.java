package com.hw.helper;

import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Role {
    private String type;
    private String id;
    private String name;
    private String parentId;
    private String projectId;
    private String description;
    private Set<String> apiPermissionIds;
    private Set<String> commonPermissionIds;
    private Set<String> externalPermissionIds;
}
