package com.mt.access.application;

import com.mt.access.domain.DomainRegistry;
import org.springframework.stereotype.Service;

@Service
public class InstanceApplicationService {
    public boolean checkReady() {
        return DomainRegistry.getInstanceService().checkReady();
    }
}
