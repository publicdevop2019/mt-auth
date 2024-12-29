package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.project.Project;
import lombok.Data;

@Data
public class ClientAutoApproveRepresentation {
    private String clientName;
    private String projectName;

    public ClientAutoApproveRepresentation(Project project, Client client) {
        clientName = client.getName();
        projectName = project.getName();
    }
}
