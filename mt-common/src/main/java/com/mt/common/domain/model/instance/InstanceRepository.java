package com.mt.common.domain.model.instance;

import java.util.List;

public interface InstanceRepository {
    void addInstance(Instance instance);

    void removeInstance(Instance instance);

    void renewInstance(Instance instance);

    List<Instance> getAllInstances();
}
