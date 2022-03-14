package com.mt.access.application.permission.command;

import lombok.Data;

import java.util.List;

@Data
public class PermissionUpdateCommand {
    private String name;
    private String projectId;
    private List<String> linkedApiIds;
}
