package com.mt.access.application.permission.command;

import java.util.List;
import lombok.Data;

@Data
public class PermissionCreateCommand {
    private String name;
    private String projectId;
    private List<String> linkedApiIds;
}
