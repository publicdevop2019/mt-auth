package com.mt.access.domain.model.instance;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.application.instance.InstanceCreateCommand;
import com.mt.common.application.instance.InstanceRemoveCommand;
import com.mt.common.application.instance.InstanceRenewCommand;
import com.mt.common.application.instance.InstanceRepresentation;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.infrastructure.SnowflakeUniqueIdService;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@DependsOn({"commonDomainRegistry", "commonApplicationServiceRegistry"})
public class InstanceService implements DisposableBean {
    private boolean isReady = false;
    @Getter
    private Integer instanceId;

    @PostConstruct
    public void initInstanceId() {
        log.info("init instance id");
        InstanceCreateCommand command = new InstanceCreateCommand();
        command.setName("mt-access");
        InstanceRepresentation rep =
            CommonApplicationServiceRegistry.getInstanceApplicationService().create(command);
        SnowflakeUniqueIdService svc =
            (SnowflakeUniqueIdService) CommonDomainRegistry.getUniqueIdGeneratorService();
        //if instance id assign failed, ready status should be false
        svc.assignInstanceId(rep.getId());
        log.info("assigned instance id {}", rep.getId());
        instanceId = rep.getId();
        isReady = true;
    }

    public boolean checkReady() {
        return isReady;
    }

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 60 * 1000)
    public void scheduledRenew() {
        if (instanceId != null) {
            log.info("renewing instance id");
            InstanceRenewCommand command = new InstanceRenewCommand();
            command.setId(instanceId);
            CommonApplicationServiceRegistry.getInstanceApplicationService().renew(command);
            log.info("instance renewed");
        } else {
            log.debug("skipped instance id renew due to instance id not ready");
        }
    }

    @Override
    public void destroy() {
        log.info("removing instance id");
        Integer instanceId = DomainRegistry.getInstanceService().getInstanceId();
        InstanceRemoveCommand command = new InstanceRemoveCommand();
        command.setId(instanceId);
        CommonApplicationServiceRegistry.getInstanceApplicationService().remove(command);
    }
}
