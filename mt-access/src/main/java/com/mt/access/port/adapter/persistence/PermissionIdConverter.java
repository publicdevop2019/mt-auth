package com.mt.access.port.adapter.persistence;

import com.mt.access.domain.model.permission.PermissionId;
import javax.persistence.AttributeConverter;

public class PermissionIdConverter implements AttributeConverter<PermissionId, String> {
    @Override
    public String convertToDatabaseColumn(PermissionId permissionId) {
        if (permissionId == null) {
            return null;
        }
        return permissionId.getDomainId();
    }

    @Override
    public PermissionId convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData.isBlank() || dbData.isEmpty()) {
            return null;
        }
        return new PermissionId(dbData);
    }
}
