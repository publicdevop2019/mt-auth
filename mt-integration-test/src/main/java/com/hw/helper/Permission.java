package com.hw.helper;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Permission {
    private String id;
    private String name;
    private String parentId;
    private String projectId;
    private Integer version;
    private List<String> linkedApiIds;
    private List<String> linkedApiPermissionIds;
}
