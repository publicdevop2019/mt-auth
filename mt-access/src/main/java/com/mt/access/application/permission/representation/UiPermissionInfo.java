package com.mt.access.application.permission.representation;

import com.mt.access.domain.model.permission.Permission;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class UiPermissionInfo {
    private String projectId;
    private List<PermissionInfo> permissionInfo;

    public UiPermissionInfo(List<Permission> ui) {
        projectId = ui.stream().map(Permission::getTenantId).filter(Objects::nonNull)
            .findFirst().get().getDomainId();
        permissionInfo = ui.stream().map(PermissionInfo::new).collect(Collectors.toList());
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
