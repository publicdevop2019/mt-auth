package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.system_role.RoleType;
import com.mt.access.domain.model.system_role.SystemRole;
import com.mt.access.domain.model.system_role.SystemRoleQuery;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class UserValidator{
    private final User user;
    private final ValidationNotificationHandler handler;
    public UserValidator(User user, @NotNull ValidationNotificationHandler handler) {
        this.user=user;
        this.handler=handler;
    }

    public void validate() {
        onlyRoleWithUserType();
    }

    private void onlyRoleWithUserType() {
        Set<SystemRole> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getSystemRoleRepository().systemRoleOfQuery((SystemRoleQuery) query), new SystemRoleQuery(user.getRoles()));
        if(allByQuery.stream().anyMatch(e->e.getRoleType().equals(RoleType.CLIENT))){
            handler.handleError("client can only has client roles");
        }
    }
}
