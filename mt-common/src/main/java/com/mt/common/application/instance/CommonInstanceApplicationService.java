package com.mt.common.application.instance;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.instance.Instance;
import org.springframework.stereotype.Service;

@Service
public class CommonInstanceApplicationService {
    public InstanceRepresentation create(InstanceCreateCommand command) {
        Instance instance = Instance.create(command.getName(), command.getUrl());
        return new InstanceRepresentation(instance);
    }

    public void remove(InstanceRemoveCommand command) {
        CommonDomainRegistry.getInstanceRepository().removeInstance(Instance.of(command.getId()));
    }

    public void renew(InstanceRenewCommand command) {
        Instance instance = Instance.renew(command.getId());
        CommonDomainRegistry.getInstanceRepository().renewInstance(instance);
    }
}
