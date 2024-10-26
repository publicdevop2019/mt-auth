package com.mt.access.application.registry;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import java.util.List;
import lombok.Data;

@Data
public class RegistryCardRepresentation {
    private final Integer count;
    private String name;
    private String id;

    public RegistryCardRepresentation(Application application) {
        this.name = application.getName();
        this.id = application.getName();
        List<InstanceInfo> instances = application.getInstances();
        count = instances.size();
    }
}
