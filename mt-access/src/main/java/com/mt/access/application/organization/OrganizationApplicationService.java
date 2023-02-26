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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrganizationApplicationService {

    private static final String ORGANIZATION = "Organization";

    public SumPagedRep<Organization> query(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getOrganizationRepository()
            .getByQuery(new OrganizationQuery(queryParam, pageParam, skipCount));
    }

    public Optional<Organization> getById(String id) {
        return DomainRegistry.getOrganizationRepository().getById(new OrganizationId(id));
    }


    @Transactional
    public void replace(String id, OrganizationUpdateCommand command, String changeId) {
        OrganizationId organizationId = new OrganizationId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Optional<Organization> first = DomainRegistry.getOrganizationRepository()
                    .getByQuery(new OrganizationQuery(organizationId)).findFirst();
                first.ifPresent(e -> {
                    e.replace(command.getName());
                    DomainRegistry.getOrganizationRepository().add(e);
                });
                return null;
            }, ORGANIZATION);
    }


    @Transactional
    public void remove(String id, String changeId) {
        OrganizationId organizationId = new OrganizationId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<Organization> corsProfile =
                DomainRegistry.getOrganizationRepository().getById(organizationId);
            corsProfile.ifPresent(e -> {
                DomainRegistry.getOrganizationRepository().remove(e);
            });
            return null;
        }, ORGANIZATION);
    }


    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        OrganizationId organizationId = new OrganizationId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                Optional<Organization> corsProfile =
                    DomainRegistry.getOrganizationRepository().getById(organizationId);
                if (corsProfile.isPresent()) {
                    Organization corsProfile1 = corsProfile.get();
                    OrganizationPatchCommand beforePatch =
                        new OrganizationPatchCommand(corsProfile1);
                    OrganizationPatchCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, OrganizationPatchCommand.class);
                    corsProfile1.replace(
                        afterPatch.getName()
                    );
                }
                return null;
            }, ORGANIZATION);
    }


    @Transactional
    public String create(OrganizationCreateCommand command, String changeId) {
        OrganizationId organizationId = new OrganizationId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Organization organization = new Organization(organizationId, command.getName());
                DomainRegistry.getOrganizationRepository().add(organization);
                return organizationId.getDomainId();
            }, ORGANIZATION);
    }
}
