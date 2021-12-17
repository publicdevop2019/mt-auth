package com.mt.access.application.system_role;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.client.command.ClientPatchCommand;
import com.mt.access.application.system_role.command.CreateSystemRoleCommand;
import com.mt.access.application.system_role.command.PatchSystemRoleCommand;
import com.mt.access.application.system_role.command.ReplaceSystemRoleCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.TokenDetail;
import com.mt.access.domain.model.system_role.SystemRole;
import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.access.domain.model.system_role.SystemRoleQuery;
import com.mt.access.domain.model.system_role.event.SystemRoleDeleted;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SystemRoleApplicationService {

    public static final String SYSTEM_ROLE = "SystemRole";

    @Transactional
    @SubscribeForEvent
    public String create(CreateSystemRoleCommand command, String changeId) {
        SystemRoleId systemRoleId = new SystemRoleId();
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            DomainRegistry.getSystemRoleRepository().add(SystemRole.create(systemRoleId, command));
            return systemRoleId.getDomainId();
        }, SYSTEM_ROLE);
    }

    public SumPagedRep<SystemRole> systemRoles(String queryParam, String pageParam, String config) {
        return DomainRegistry.getSystemRoleRepository().systemRoleOfQuery(new SystemRoleQuery(queryParam, pageParam, config));
    }

    @Transactional
    @SubscribeForEvent
    public void replace(String id, ReplaceSystemRoleCommand command, String changeId) {
        SystemRoleId systemRoleId = new SystemRoleId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Optional<SystemRole> first = DomainRegistry.getSystemRoleRepository().systemRoleOfQuery(new SystemRoleQuery(systemRoleId)).findFirst();
            first.ifPresent(e -> {
                e.replace(command);
                DomainRegistry.getSystemRoleRepository().add(e);
            });
            return null;
        }, SYSTEM_ROLE);
    }

    @Transactional
    @SubscribeForEvent
    public void remove(String id, String changeId) {
        SystemRoleId systemRoleId = new SystemRoleId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Optional<SystemRole> first = DomainRegistry.getSystemRoleRepository().systemRoleOfQuery(new SystemRoleQuery(systemRoleId)).findFirst();
            first.ifPresent(SystemRole::remove);
            return null;
        }, SYSTEM_ROLE);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        SystemRoleId roleId = new SystemRoleId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent( changeId, (ignored) -> {
            Optional<SystemRole> role = DomainRegistry.getSystemRoleRepository().systemRoleOfQuery(new SystemRoleQuery(roleId)).findFirst();
            if (role.isPresent()) {
                SystemRole original = role.get();
                PatchSystemRoleCommand beforePatch = new PatchSystemRoleCommand(original);
                PatchSystemRoleCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PatchSystemRoleCommand.class);
                original.replace(
                        afterPatch.getName(),
                        afterPatch.getDescription()
                );
            }
            return null;
        }, SYSTEM_ROLE);
    }
}
