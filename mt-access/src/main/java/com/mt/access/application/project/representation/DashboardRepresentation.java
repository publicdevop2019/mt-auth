package com.mt.access.application.project.representation;

import lombok.Data;

@Data
public class DashboardRepresentation {
    private Long totalProjects;
    private Long totalClients;
    private Long totalEndpoint;
    private Long totalSharedEndpoint;
    private Long totalPublicEndpoint;
    private Long totalUser;

    public DashboardRepresentation(long projectCount, long clientCount, long epCount,
                                   long epSharedCount, long epPublicCount, long userCount) {
        this.totalProjects = projectCount;
        this.totalClients = clientCount;
        this.totalEndpoint = epCount;
        this.totalSharedEndpoint = epSharedCount;
        this.totalPublicEndpoint = epPublicCount;
        this.totalUser = userCount;
    }
}
