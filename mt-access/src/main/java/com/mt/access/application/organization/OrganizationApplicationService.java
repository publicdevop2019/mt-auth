package com.mt.access.application.organization;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.organization.command.OrganizationCreateCommand;
import com.mt.access.application.organization.command.OrganizationPatchCommand;
import com.mt.access.application.organization.command.OrganizationUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.organization.Organization;
import com.mt.access.domain.model.organization.OrganizationId;
import com.mt.access.domain.model.organization.OrganizationQuery;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrganizationApplicationService {

    private static final String ORGANIZATION = "Organization";

    public SumPagedRep<Organization> tenantQuery(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getOrganizationRepository()
            .query(new OrganizationQuery(queryParam, pageParam, skipCount));
    }

    public Organization tenantQuery(String id) {
        return DomainRegistry.getOrganizationRepository().get(new OrganizationId(id));
    }


    public void tenantUpdate(String id, OrganizationUpdateCommand command, String changeId) {
        OrganizationId organizationId = new OrganizationId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Optional<Organization> first = DomainRegistry.getOrganizationRepository()
                    .query(new OrganizationQuery(organizationId)).findFirst();
                first.ifPresent(e -> {
                    e.replace(command.getName());
                    DomainRegistry.getOrganizationRepository().add(e);
                });
                return null;
            }, ORGANIZATION);
    }


    public void tenantRemove(String id, String changeId) {
        OrganizationId organizationId = new OrganizationId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Organization corsProfile =
                DomainRegistry.getOrganizationRepository().get(organizationId);
                DomainRegistry.getOrganizationRepository().remove(corsProfile);
            return null;
        }, ORGANIZATION);
    }


    public void tenantPatch(String id, JsonPatch command, String changeId) {
        OrganizationId organizationId = new OrganizationId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                Organization organization =
                    DomainRegistry.getOrganizationRepository().get(organizationId);
                    OrganizationPatchCommand beforePatch =
                        new OrganizationPatchCommand(organization);
                    OrganizationPatchCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, OrganizationPatchCommand.class);
                    organization.replace(
                        afterPatch.getName()
                    );
                return null;
            }, ORGANIZATION);
    }


    public String tenantCreate(OrganizationCreateCommand command, String changeId) {
        OrganizationId organizationId = new OrganizationId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Organization organization = new Organization(organizationId, command.getName());
                DomainRegistry.getOrganizationRepository().add(organization);
                return organizationId.getDomainId();
            }, ORGANIZATION);
    }
}
