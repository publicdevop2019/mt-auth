package com.mt.access.application.user.command;

import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AssignRoleCommand {
    private LinkedHashSet<String> roleIds;
}
