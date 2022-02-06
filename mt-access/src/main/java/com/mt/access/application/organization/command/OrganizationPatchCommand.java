package com.mt.access.application.organization.command;

import com.mt.access.domain.model.organization.Organization;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OrganizationPatchCommand {
    private String name;

    public OrganizationPatchCommand(Organization project) {
        this.name = project.getName();
    }
}
