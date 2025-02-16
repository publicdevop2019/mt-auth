package com.mt.access.domain.model.permission;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import java.util.Set;

public class LinkedApiPermissionId {
    private static void validate(Set<PermissionId> linked) {
        Validator.validOptionalCollection(20, linked);
    }

    public static void add(Permission permission, Set<PermissionId> linkedPermId) {
        validate(linkedPermId);
        DomainRegistry.getLinkedApiPermissionIdRepository().add(permission, linkedPermId);
    }
}
