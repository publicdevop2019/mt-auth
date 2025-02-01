package com.mt.access.domain.model.role;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.role.event.ExternalPermissionUpdated;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.Validator;
import java.util.Set;

public class ExternalPermissionId {
    public static void add(Role role, Set<PermissionId> externalPermissionIds,
                           TransactionContext context) {
        if (Utility.notNullOrEmpty(externalPermissionIds)) {
            Validator.validOptionalCollection(10, externalPermissionIds);
            DomainRegistry.getExternalPermissionIdRepository().add(role, externalPermissionIds);
            context.append(new ExternalPermissionUpdated(role.getProjectId()));
        }
    }

    public static void update(Role role, Set<PermissionId> old, Set<PermissionId> next,
                              TransactionContext context) {
        if (!Utility.sameAs(old, next)) {
            Utility.updateSet(old, next,
                (added) -> {
                    Validator.noNullMember(added);
                    Validator.lessThanOrEqualTo(next, 10);
                    DomainRegistry.getExternalPermissionIdRepository().add(role, added);
                },
                (removed) -> DomainRegistry.getExternalPermissionIdRepository().remove(role, removed));
            context.append(new ExternalPermissionUpdated(role.getProjectId()));
        }
    }
}
