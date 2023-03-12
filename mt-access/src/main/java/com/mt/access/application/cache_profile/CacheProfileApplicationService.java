package com.mt.access.application.cache_profile;

import static com.mt.access.domain.model.audit.AuditActionName.DELETE_CACHE_PROFILE;

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
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheProfileApplicationService {

    private static final String CACHE_PROFILE = "CACHE_PROFILE";

    public SumPagedRep<CacheProfile> query(String queryParam, String pageParam,
                                           String config) {
        return DomainRegistry.getCacheProfileRepository()
            .query(new CacheProfileQuery(queryParam, pageParam, config));
    }

    public String create(CreateCacheProfileCommand command, String changeId) {
        CacheProfileId cacheProfileId = new CacheProfileId();
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            CacheProfile cacheProfile = new CacheProfile(
                command.getName(),
                command.getDescription(),
                cacheProfileId,
                command.getCacheControl().stream().map(CacheControlValue::valueOfLabel)
                    .collect(Collectors.toSet()),
                command.getExpires(),
                command.getMaxAge(),
                command.getSmaxAge(),
                command.getVary(),
                command.isAllowCache(),
                command.isEtag(),
                command.isWeakValidation()
            );
            DomainRegistry.getCacheProfileRepository().add(cacheProfile);
            return null;
        }, CACHE_PROFILE);
        return cacheProfileId.getDomainId();
    }


    public void update(String id, ReplaceCacheProfileCommand command, String changeId) {
        CacheProfileId cacheProfileId = new CacheProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<CacheProfile> cacheProfile1 =
                DomainRegistry.getCacheProfileRepository().id(cacheProfileId);
            cacheProfile1.ifPresent(e -> e.update(
                command.getName(),
                command.getDescription(),
                command.getCacheControl().stream().map(CacheControlValue::valueOfLabel)
                    .collect(Collectors.toSet()),
                command.getExpires(),
                command.getMaxAge(),
                command.getSmaxAge(),
                command.getVary(),
                command.isAllowCache(),
                command.isEtag(),
                command.isWeakValidation()
            ));
            return null;
        }, CACHE_PROFILE);
    }

    public void patch(String id, JsonPatch command, String changeId) {
        CacheProfileId cacheProfileId = new CacheProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                Optional<CacheProfile> cacheProfile =
                    DomainRegistry.getCacheProfileRepository().id(cacheProfileId);
                if (cacheProfile.isPresent()) {
                    CacheProfile profile = cacheProfile.get();
                    PatchCacheProfileCommand beforePatch = new PatchCacheProfileCommand(profile);
                    PatchCacheProfileCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, PatchCacheProfileCommand.class);
                    profile.update(
                        afterPatch.getName(),
                        afterPatch.getDescription(),
                        afterPatch.getCacheControl().stream().map(CacheControlValue::valueOfLabel)
                            .collect(Collectors.toSet()),
                        afterPatch.getExpires(),
                        afterPatch.getMaxAge(),
                        afterPatch.getSmaxAge(),
                        afterPatch.getVary(),
                        profile.isAllowCache(),
                        afterPatch.isEtag(),
                        afterPatch.isWeakValidation()
                    );
                }
                return null;
            }, CACHE_PROFILE);
    }

    @AuditLog(actionName = DELETE_CACHE_PROFILE)
    public void remove(String id, String changeId) {
        CacheProfileId cacheProfileId = new CacheProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<CacheProfile> cacheProfile =
                DomainRegistry.getCacheProfileRepository().id(cacheProfileId);
            cacheProfile.ifPresent(e -> {
                e.removeAllReference();
                DomainRegistry.getCacheProfileRepository().remove(e);
                DomainRegistry.getAuditService()
                    .storeAuditAction(DELETE_CACHE_PROFILE,
                        e);
                DomainRegistry.getAuditService()
                    .logUserAction(log, DELETE_CACHE_PROFILE,
                        e);
            });
            return null;
        }, CACHE_PROFILE);
    }
}
