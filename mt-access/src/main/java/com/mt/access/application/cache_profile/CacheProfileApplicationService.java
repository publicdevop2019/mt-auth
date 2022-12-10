package com.mt.access.application.cache_profile;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.cache_profile.command.CreateCacheProfileCommand;
import com.mt.access.application.cache_profile.command.PatchCacheProfileCommand;
import com.mt.access.application.cache_profile.command.ReplaceCacheProfileCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheControlValue;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.CacheProfileQuery;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CacheProfileApplicationService {

    public static final String CACHE_PROFILE = "CACHE_PROFILE";

    @Transactional
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

    public SumPagedRep<CacheProfile> cacheProfiles(String queryParam, String pageParam,
                                                   String config) {
        return DomainRegistry.getCacheProfileRepository()
            .cacheProfileOfQuery(new CacheProfileQuery(queryParam, pageParam, config));
    }

    @Transactional
    public void update(String id, ReplaceCacheProfileCommand command, String changeId) {
        CacheProfileId cacheProfileId = new CacheProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<CacheProfile> cacheProfile1 =
                DomainRegistry.getCacheProfileRepository().cacheProfileOfId(cacheProfileId);
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

    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        CacheProfileId cacheProfileId = new CacheProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                Optional<CacheProfile> cacheProfile =
                    DomainRegistry.getCacheProfileRepository().cacheProfileOfId(cacheProfileId);
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

    @Transactional
    public void remove(String id, String changeId) {
        CacheProfileId cacheProfileId = new CacheProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<CacheProfile> cacheProfile =
                DomainRegistry.getCacheProfileRepository().cacheProfileOfId(cacheProfileId);
            cacheProfile.ifPresent(e -> {
                e.removeAllReference();
                DomainRegistry.getCacheProfileRepository().remove(e);
            });
            return null;
        }, CACHE_PROFILE);
    }
}
