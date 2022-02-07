package com.mt.access.application.user_relation;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
@Getter
@NoArgsConstructor
public class UpdateUserRelationCommand {
    private Set<String> roles;
}
