package com.mt.access.application.organization.representation;

import com.mt.access.domain.model.organization.Organization;
import lombok.Data;

@Data
public class OrganizationRepresentation {
    private String name;
    public OrganizationRepresentation(Organization role) {
        this.name= role.getName();
    }
}
