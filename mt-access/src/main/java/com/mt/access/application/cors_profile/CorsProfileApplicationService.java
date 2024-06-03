package com.mt.access.application.cors_profile;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_CORS_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_CORS_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.UPDATE_TENANT_CORS_PROFILE;
import static com.mt.access.domain.model.permission.Permission.API_MGMT;

import com.mt.access.application.cors_profile.command.CorsProfileCreateCommand;
import com.mt.access.application.cors_profile.command.CorsProfileUpdateCommand;
import com.mt.access.application.cors_profile.representation.CorsProfileRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.CorsProfileQuery;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.infrastructure.CommonUtility;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CorsProfileApplicationService {

    private static final String CORS_PROFILE = "CORS_PROFILE";

    public SumPagedRep<CorsProfileRepresentation> tenantQuery(String projectId1, String queryParam,
                                                              String pageParam,
                                                              String config) {
        ProjectId projectId = new ProjectId(projectId1);
        DomainRegistry.getPermissionCheckService().canAccess(projectId, API_MGMT);
        SumPagedRep<CorsProfile> query = DomainRegistry.getCorsProfileRepository()
            .query(CorsProfileQuery.tenantQuery(queryParam, pageParam, config));
        return new SumPagedRep<>(query, CorsProfileRepresentation::new);
    }

    @AuditLog(actionName = CREATE_TENANT_CORS_PROFILE)
    public String tenantCreate(String projectId, CorsProfileCreateCommand command,
                               String changeId) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId1, API_MGMT);
        CorsProfileId corsProfileId = new CorsProfileId();
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (context) -> {
            CorsProfile corsProfile = new CorsProfile(
                command.getName(),
                command.getDescription(),
                command.getAllowedHeaders(),
                command.getAllowCredentials(),
                CommonUtility.map(command.getAllowOrigin(), Origin::new),
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
        DomainRegistry.getPermissionCheckService().canAccess(projectId, API_MGMT);
        CorsProfileId corsProfileId = new CorsProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (context) -> {
            CorsProfileQuery corsProfileQuery =
                CorsProfileQuery.tenantQuery(projectId, corsProfileId);
            Optional<CorsProfile> corsProfile =
                DomainRegistry.getCorsProfileRepository().query(corsProfileQuery).findFirst();
            corsProfile.ifPresent(e -> {
                CorsProfile update = e.update(
                    command.getName(),
                    command.getDescription(),
                    command.getAllowedHeaders(),
                    command.getAllowCredentials(),
                    CommonUtility.map(command.getAllowOrigin(), Origin::new),
                    command.getExposedHeaders(),
                    command.getMaxAge(),
                    context
                );
                DomainRegistry.getCorsProfileRepository().update(e, update);
            });
            return null;
        }, CORS_PROFILE);
    }

    @AuditLog(actionName = DELETE_TENANT_CORS_PROFILE)
    public void tenantRemove(String projectId, String id, String changeId) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId1, API_MGMT);
        CorsProfileId corsProfileId = new CorsProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (context) -> {
            CorsProfileQuery corsProfileQuery =
                CorsProfileQuery.tenantQuery(projectId1, corsProfileId);
            Optional<CorsProfile> corsProfile =
                DomainRegistry.getCorsProfileRepository().query(corsProfileQuery)
                    .findFirst();
            corsProfile.ifPresent(e -> {
                e.removeAllReference(context);
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

}
