package com.mt.access.application.user.representation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;

@Data
public class UserTenantRepresentation {
    private final String id;
    private final String email;
    private Set<String> roles;
    private Set<RoleDetail> roleDetails;

    public UserTenantRepresentation(UserRelation userRelation, User user) {
        this.id = user.getUserId().getDomainId();
        this.email = user.getEmail().getEmail();
        if (userRelation.getStandaloneRoles() != null
            && userRelation.getStandaloneRoles().size() > 0) {
            this.roles = userRelation.getStandaloneRoles().stream().map(DomainId::getDomainId)
                .collect(Collectors.toSet());
            Set<RoleId> standaloneRoles = userRelation.getStandaloneRoles();
            Set<Role> allByQuery = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getRoleRepository().getByQuery(e),
                    new RoleQuery(standaloneRoles));
            roleDetails =
                allByQuery.stream().map(RoleDetail::new).collect(Collectors.toSet());
        }


    }

    @Getter
    private static class RoleDetail {
        private final String id;
        private final String name;

        public RoleDetail(Role e) {
            id = e.getRoleId().getDomainId();
            name = e.getName();
        }
    }
}
