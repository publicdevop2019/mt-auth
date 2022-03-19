package com.mt.access.application.endpoint.representation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class EndpointSharedCardRepresentation {
    private String id;
    private String name;
    private String description;
    private String path;
    private String method;
    private Integer version;
    private boolean websocket;
    private String projectId;
    private String projectName;
    private transient ProjectId originalProjectId;
    private transient ClientId clientId;

    public EndpointSharedCardRepresentation(Endpoint endpoint) {
        this.clientId = endpoint.getClientId();
        this.projectId = endpoint.getProjectId().getDomainId();
        this.originalProjectId = endpoint.getProjectId();
        this.id = endpoint.getEndpointId().getDomainId();
        this.description = endpoint.getDescription();
        this.name = endpoint.getName();
        this.websocket = endpoint.isWebsocket();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.version = endpoint.getVersion();
    }

    public static void updateDetail(List<EndpointSharedCardRepresentation> original) {
        if(!original.isEmpty()){
            Set<ClientId> collect = original.stream().map(e -> e.clientId).collect(Collectors.toSet());
            Set<ProjectId> collect2 = original.stream().map(e -> e.originalProjectId).collect(Collectors.toSet());
            Set<Client> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getClientRepository().clientsOfQuery((ClientQuery) e), new ClientQuery(collect));
            Set<Project> allByQuery2 = QueryUtility.getAllByQuery(e -> DomainRegistry.getProjectRepository().getByQuery((ProjectQuery) e), new ProjectQuery(collect2));
            original.forEach(e -> {
                Optional<Client> first = allByQuery.stream().filter(ee -> ee.getClientId().equals(e.getClientId())).findFirst();
                first.ifPresent(ee->{
                    String path = ee.getPath();
                    e.path = "/" + path + "/" + e.path;
                });
                Optional<Project> first2 = allByQuery2.stream().filter(ee -> ee.getProjectId().equals(e.getOriginalProjectId())).findFirst();
                first2.ifPresent(ee->{
                    e.projectName = ee.getName();
                });
            });
        }
    }
}
