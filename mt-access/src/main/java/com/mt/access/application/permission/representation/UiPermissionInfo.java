package com.mt.access.application.permission.representation;

import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.project.ProjectId;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class UiPermissionInfo {
    List<ProjectPermission> projectPermissionInfo;

    public UiPermissionInfo(Set<Permission> ui) {
        Set<ProjectId> collect =
            ui.stream().map(Permission::getTenantId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        projectPermissionInfo = collect.stream().map(e -> {
            Set<Permission> collect1 =
                ui.stream().filter(ee -> ee.getTenantId().equals(e)).collect(Collectors.toSet());
            List<PermissionInfo> collect2 =
                collect1.stream().map(PermissionInfo::new).collect(Collectors.toList());
            return new ProjectPermission(e, collect2);
        }).collect(Collectors.toList());
    }

    @Data
    private static class ProjectPermission {
        private final String projectId;
        private final List<PermissionInfo> permissionInfo;

        public ProjectPermission(ProjectId e, List<PermissionInfo> collect2) {
            projectId = e.getDomainId();
            permissionInfo = collect2;
        }
    }

    @Data
    private static class PermissionInfo {
        private final String name;
        private final String id;

        public PermissionInfo(Permission ee) {
            name = ee.getName();
            id = ee.getPermissionId().getDomainId();
        }
    }
}
