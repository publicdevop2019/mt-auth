package com.mt.access.application.cors_profile;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_CORS_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_CORS_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.PATCH_TENANT_CORS_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.UPDATE_TENANT_CORS_PROFILE;
import static com.mt.access.domain.model.permission.Permission.CREATE_CORS;
import static com.mt.access.domain.model.permission.Permission.EDIT_CORS;
import static com.mt.access.domain.model.permission.Permission.VIEW_CORS;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.cors_profile.command.CorsProfileCreateCommand;
import com.mt.access.application.cors_profile.command.CorsProfilePatchCommand;
import com.mt.access.application.cors_profile.command.CorsProfileUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.CorsProfileQuery;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CorsProfileApplicationService {

    private static final String CORS_PROFILE = "CORS_PROFILE";

    public SumPagedRep<CorsProfile> tenantQuery(String projectId1, String queryParam,
                                                String pageParam,
                                                String config) {
        ProjectId projectId = new ProjectId(projectId1);
        DomainRegistry.getPermissionCheckService().canAccess(projectId, VIEW_CORS);
        return DomainRegistry.getCorsProfileRepository()
            .query(new CorsProfileQuery(queryParam, pageParam, config));
    }

    @AuditLog(actionName = CREATE_TENANT_CORS_PROFILE)
    public String tenantCreate(String projectId, CorsProfileCreateCommand command,
                               String changeId) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId1, CREATE_CORS);
        CorsProfileId corsProfileId = new CorsProfileId();
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            CorsProfile corsProfile = new CorsProfile(
                command.getName(),
                command.getDescription(),
                command.getAllowedHeaders(),
                command.isAllowCredentials(),
                command.getAllowOrigin().stream().map(Origin::new).collect(Collectors.toSet()),
                command.getExposedHeaders(),
                command.getMaxAge(),
                corsProfileId,
                projectId1
            );
            DomainRegistry.getCorsProfileRepository().add(corsProfile);
            return null;
        }, CORS_PROFILE);
        return corsProfileId.getDomainId();
    }

    @AuditLog(actionName = UPDATE_TENANT_CORS_PROFILE)
    public void tenantUpdate(String id, CorsProfileUpdateCommand command, String changeId) {
        ProjectId projectId = new ProjectId(command.getProjectId());
        DomainRegistry.getPermissionCheckService().canAccess(projectId, EDIT_CORS);
        CorsProfileId corsProfileId = new CorsProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            CorsProfileQuery corsProfileQuery =
                new CorsProfileQuery(projectId, corsProfileId);
            Optional<CorsProfile> corsProfile =
                DomainRegistry.getCorsProfileRepository().query(corsProfileQuery).findFirst();
            corsProfile.ifPresent(e -> e.update(
                command.getName(),
                command.getDescription(),
                command.getAllowedHeaders(),
                command.getAllowCredentials(),
                command.getAllowOrigin().stream().map(Origin::new).collect(Collectors.toSet()),
                command.getExposedHeaders(),
                command.getMaxAge()
            ));
            return null;
        }, CORS_PROFILE);
    }

    @AuditLog(actionName = DELETE_TENANT_CORS_PROFILE)
    public void tenantRemove(String projectId, String id, String changeId) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId1, EDIT_CORS);
        CorsProfileId corsProfileId = new CorsProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            CorsProfileQuery corsProfileQuery =
                new CorsProfileQuery(projectId1, corsProfileId);
            Optional<CorsProfile> corsProfile =
                DomainRegistry.getCorsProfileRepository().query(corsProfileQuery)
                    .findFirst();
            corsProfile.ifPresent(e -> {
                e.removeAllReference();
                DomainRegistry.getCorsProfileRepository().remove(e);
                DomainRegistry.getAuditService()
                    .storeAuditAction(DELETE_TENANT_CORS_PROFILE,
                        e);
                DomainRegistry.getAuditService()
                    .logUserAction(log, DELETE_TENANT_CORS_PROFILE,
                        e);
            });
            return null;
        }, CORS_PROFILE);
    }

    @AuditLog(actionName = PATCH_TENANT_CORS_PROFILE)
    public void tenantPatch(String projectId, String id, JsonPatch command, String changeId) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId1, EDIT_CORS);
        CorsProfileId corsProfileId = new CorsProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                CorsProfileQuery corsProfileQuery = new CorsProfileQuery(projectId1, corsProfileId);
                Optional<CorsProfile> corsProfile =
                    DomainRegistry.getCorsProfileRepository().query(corsProfileQuery)
                        .findFirst();
                if (corsProfile.isPresent()) {
                    CorsProfile corsProfile1 = corsProfile.get();
                    CorsProfilePatchCommand beforePatch = new CorsProfilePatchCommand(corsProfile1);
                    CorsProfilePatchCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, CorsProfilePatchCommand.class);
                    corsProfile1.update(
                        afterPatch.getName(),
                        afterPatch.getDescription(),
                        afterPatch.getAllowedHeaders(),
                        afterPatch.isAllowCredentials(),
                        afterPatch.getAllowOrigin().stream().map(Origin::new)
                            .collect(Collectors.toSet()),
                        afterPatch.getExposedHeaders(),
                        afterPatch.getMaxAge()
                    );
                }
                return null;
            }, CORS_PROFILE);
    }
}
