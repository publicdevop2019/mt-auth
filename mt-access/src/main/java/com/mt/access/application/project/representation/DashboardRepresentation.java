package com.mt.access.application.project.representation;

import lombok.Data;

@Data
public class DashboardRepresentation {
    private long totalProjects;
    private long totalClients;
    private long totalEndpoint;
    private long totalSharedEndpoint;
    private long totalPublicEndpoint;
    private long totalUser;

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
