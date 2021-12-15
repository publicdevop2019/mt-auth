package com.mt.access.port.adapter.persistence.system_role;

import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.access.domain.model.system_role.event.SystemRoleDeleted;
import com.mt.common.domain.model.domainId.DomainId;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SystemRoleIdConverter implements AttributeConverter<Set<SystemRoleId>, String> {
    @Override
    public String convertToDatabaseColumn(Set<SystemRoleId> systemRoleIds) {
        return systemRoleIds.stream().map(DomainId::getDomainId).collect(Collectors.joining(","));
    }

    @Override
    public Set<SystemRoleId> convertToEntityAttribute(String s) {
        return Arrays.stream(s.split(",")).filter(e->!e.isBlank()).map(SystemRoleId::new).collect(Collectors.toSet());
    }
}
