package com.mt.access.application.cache_profile;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_CACHE_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_CACHE_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.PATCH_TENANT_CACHE_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.UPDATE_TENANT_CACHE_PROFILE;
import static com.mt.access.domain.model.permission.Permission.CREATE_CACHE;
import static com.mt.access.domain.model.permission.Permission.EDIT_CACHE;
import static com.mt.access.domain.model.permission.Permission.EDIT_CORS;
import static com.mt.access.domain.model.permission.Permission.VIEW_CACHE;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.cache_profile.command.CreateCacheProfileCommand;
import com.mt.access.application.cache_profile.command.PatchCacheProfileCommand;
import com.mt.access.application.cache_profile.command.ReplaceCacheProfileCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.cache_profile.CacheControlValue;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.CacheProfileQuery;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.infrastructure.CommonUtility;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheProfileApplicationService {

    private static final String CACHE_PROFILE = "CACHE_PROFILE";

    public SumPagedRep<CacheProfile> tenantQuery(String projectId, String queryParam,
                                                 String pageParam,
                                                 String config) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId1, VIEW_CACHE);
        return DomainRegistry.getCacheProfileRepository()
            .query(new CacheProfileQuery(queryParam, pageParam, config));
    }

    @AuditLog(actionName = CREATE_TENANT_CACHE_PROFILE)
    public String tenantCreate(String rawProjectId, CreateCacheProfileCommand command,
                               String changeId) {
        ProjectId projectId = new ProjectId(rawProjectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId, CREATE_CACHE);
        CacheProfileId cacheProfileId = new CacheProfileId();
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (context) -> {
            CacheProfile cacheProfile = new CacheProfile(
                command.getName(),
                command.getDescription(),
                cacheProfileId,
                command.getCacheControl(),
                command.getExpires(),
                command.getMaxAge(),
                command.getSmaxAge(),
                command.getVary(),
                command.getAllowCache(),
                command.getEtag(),
                command.getWeakValidation(),
                projectId
            );
            DomainRegistry.getCacheProfileRepository().add(cacheProfile);
            return null;
        }, CACHE_PROFILE);
        return cacheProfileId.getDomainId();
    }

    @AuditLog(actionName = UPDATE_TENANT_CACHE_PROFILE)
    public void tenantUpdate(String id, ReplaceCacheProfileCommand command, String changeId) {
        CacheProfileId cacheProfileId = new CacheProfileId(id);
        ProjectId projectId = new ProjectId(command.getProjectId());
        DomainRegistry.getPermissionCheckService().canAccess(projectId, EDIT_CACHE);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (context) -> {
            CacheProfileQuery cacheProfileQuery = new CacheProfileQuery(projectId, cacheProfileId);
            Optional<CacheProfile> cacheProfile1 =
                DomainRegistry.getCacheProfileRepository().query(cacheProfileQuery).findFirst();
            cacheProfile1.ifPresent(e -> e.update(
                command.getName(),
                command.getDescription(),
                CommonUtility.map(command.getCacheControl(),CacheControlValue::valueOfLabel),
                command.getExpires(),
                command.getMaxAge(),
                command.getSmaxAge(),
                command.getVary(),
                command.getAllowCache(),
                command.getEtag(),
                command.getWeakValidation(),context
            ));
            return null;
        }, CACHE_PROFILE);
    }

    @AuditLog(actionName = PATCH_TENANT_CACHE_PROFILE)
    public void tenantPatch(String projectId, String id, JsonPatch command, String changeId) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId1, EDIT_CORS);
        CacheProfileId cacheProfileId = new CacheProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                CacheProfileQuery cacheProfileQuery = new CacheProfileQuery(projectId1, cacheProfileId);
                Optional<CacheProfile> cacheProfile =
                    DomainRegistry.getCacheProfileRepository().query(cacheProfileQuery).findFirst();
                if (cacheProfile.isPresent()) {
                    CacheProfile original = cacheProfile.get();
                    PatchCacheProfileCommand beforePatch = new PatchCacheProfileCommand(original);
                    PatchCacheProfileCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, PatchCacheProfileCommand.class);
                    original.updateNameAndDescription(
                        afterPatch.getName(),
                        afterPatch.getDescription()
                    );
                }
                return null;
            }, CACHE_PROFILE);
    }

    @AuditLog(actionName = DELETE_TENANT_CACHE_PROFILE)
    public void tenantRemove(String projectId, String id, String changeId) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId1, EDIT_CACHE);
        CacheProfileId cacheProfileId = new CacheProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (context) -> {
            CacheProfileQuery cacheProfileQuery = new CacheProfileQuery(projectId1, cacheProfileId);
            Optional<CacheProfile> cacheProfile =
                DomainRegistry.getCacheProfileRepository().query(cacheProfileQuery).findFirst();
            cacheProfile.ifPresent(e -> {
                e.removeAllReference(context);
                DomainRegistry.getCacheProfileRepository().remove(e);
                DomainRegistry.getAuditService()
                    .storeAuditAction(DELETE_TENANT_CACHE_PROFILE,
                        e);
                DomainRegistry.getAuditService()
                    .logUserAction(log, DELETE_TENANT_CACHE_PROFILE,
                        e);
            });
            return null;
        }, CACHE_PROFILE);
    }
}
