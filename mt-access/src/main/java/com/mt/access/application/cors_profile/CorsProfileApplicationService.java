package com.mt.access.application.cors_profile;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.cors_profile.command.CorsProfileCreateCommand;
import com.mt.access.application.cors_profile.command.CorsProfilePatchCommand;
import com.mt.access.application.cors_profile.command.CorsProfileUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.CorsProfileQuery;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CorsProfileApplicationService {

    public static final String CORS_PROFILE = "CORS_PROFILE";

    @Transactional
    public String create(CorsProfileCreateCommand command, String changeId) {
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
                corsProfileId);
            DomainRegistry.getCorsProfileRepository().add(corsProfile);
            return null;
        }, CORS_PROFILE);
        return corsProfileId.getDomainId();
    }

    public SumPagedRep<CorsProfile> corsProfile(String queryParam, String pageParam,
                                                String config) {
        return DomainRegistry.getCorsProfileRepository()
            .corsProfileOfQuery(new CorsProfileQuery(queryParam, pageParam, config));
    }

    @Transactional
    public void update(String id, CorsProfileUpdateCommand command, String changeId) {
        CorsProfileId corsProfileId = new CorsProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<CorsProfile> corsProfile =
                DomainRegistry.getCorsProfileRepository().corsProfileOfId(corsProfileId);
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

    @Transactional
    public void remove(String id, String changeId) {
        CorsProfileId corsProfileId = new CorsProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<CorsProfile> corsProfile =
                DomainRegistry.getCorsProfileRepository().corsProfileOfId(corsProfileId);
            corsProfile.ifPresent(e -> {
                e.removeAllReference();
                DomainRegistry.getCorsProfileRepository().remove(e);
            });
            return null;
        }, CORS_PROFILE);
    }

    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        CorsProfileId corsProfileId = new CorsProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                Optional<CorsProfile> corsProfile =
                    DomainRegistry.getCorsProfileRepository().corsProfileOfId(corsProfileId);
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
