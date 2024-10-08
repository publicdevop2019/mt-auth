package com.mt.helper.pojo;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Permission {
    private String id;
    private String name;
    private String description;
    private String projectId;
    private Integer version;
    private List<String> linkedApiIds;
    private List<String> linkedApiNames;
    private List<String> linkedApiPermissionIds;
}
