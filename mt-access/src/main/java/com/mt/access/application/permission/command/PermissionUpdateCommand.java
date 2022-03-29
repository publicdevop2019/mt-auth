package com.mt.access.application.permission.command;

import java.util.List;
import lombok.Data;

@Data
public class PermissionUpdateCommand {
    private String name;
    private String projectId;
    private List<String> linkedApiIds;
}
