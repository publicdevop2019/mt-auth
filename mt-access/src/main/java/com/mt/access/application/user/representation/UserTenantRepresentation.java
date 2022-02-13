package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.common.domain.model.domainId.DomainId;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;
@Data
public class UserTenantRepresentation {
    private final String id;
    private final String email;
    private Set<String> roles;

    public UserTenantRepresentation(UserRelation userRelation, User user) {
        this.id = user.getUserId().getDomainId();
        this.email = user.getEmail().getEmail();
        if (userRelation.getStandaloneRoles() != null && userRelation.getStandaloneRoles().size() > 0)
            this.roles = userRelation.getStandaloneRoles().stream().map(DomainId::getDomainId).collect(Collectors.toSet());

    }
}
