package com.mt.access.application.endpoint.representation;

import com.mt.access.domain.model.endpoint.Router;
import lombok.Data;

@Data
public class RouterRepresentation {
    private String id;
    private String name;
    private String description;
    private String path;
    private String externalUrl;
    private String projectId;

    public RouterRepresentation(Router router) {
        this.id = router.getRouterId().getDomainId();
        this.name = router.getName();
        this.description = router.getDescription();
        this.path = router.getPath();
        this.externalUrl = router.getExternalUrl().getValue();
        this.projectId = router.getProjectId().getDomainId();
    }
}
