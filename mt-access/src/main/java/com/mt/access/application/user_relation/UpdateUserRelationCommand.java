package com.mt.access.application.user_relation;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserRelationCommand {
    private Set<String> roles;
}
