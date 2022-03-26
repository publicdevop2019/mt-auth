package com.mt.access.application.cors_profile;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.cors_profile.command.CORSProfileCreateCommand;
import com.mt.access.application.cors_profile.command.CORSProfilePatchCommand;
import com.mt.access.application.cors_profile.command.CORSProfileUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cors_profile.CORSProfile;
import com.mt.access.domain.model.cors_profile.CORSProfileId;
import com.mt.access.domain.model.cors_profile.CORSProfileQuery;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CORSProfileApplicationService {

    public static final String CORS_PROFILE = "CORS_PROFILE";

    @Transactional
    public String create(CORSProfileCreateCommand command, String changeId) {
        CORSProfileId corsProfileId = new CORSProfileId();
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            CORSProfile corsProfile = new CORSProfile(
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

    public SumPagedRep<CORSProfile> corsProfile(String queryParam, String pageParam, String config) {
        return DomainRegistry.getCorsProfileRepository().corsProfileOfQuery(new CORSProfileQuery(queryParam, pageParam, config));
    }

    @Transactional
    public void update(String id, CORSProfileUpdateCommand command, String changeId) {
        CORSProfileId corsProfileId = new CORSProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<CORSProfile> corsProfile = DomainRegistry.getCorsProfileRepository().corsProfileOfId(corsProfileId);
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
        CORSProfileId corsProfileId = new CORSProfileId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<CORSProfile> corsProfile = DomainRegistry.getCorsProfileRepository().corsProfileOfId(corsProfileId);
            corsProfile.ifPresent(e -> {
                e.removeAllReference();
                DomainRegistry.getCorsProfileRepository().remove(e);
            });
            return null;
        }, CORS_PROFILE);
    }

    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        CORSProfileId corsProfileId = new CORSProfileId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (ignored) -> {
            Optional<CORSProfile> corsProfile = DomainRegistry.getCorsProfileRepository().corsProfileOfId(corsProfileId);
            if (corsProfile.isPresent()) {
                CORSProfile corsProfile1 = corsProfile.get();
                CORSProfilePatchCommand beforePatch = new CORSProfilePatchCommand(corsProfile1);
                CORSProfilePatchCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, CORSProfilePatchCommand.class);
                corsProfile1.update(
                        afterPatch.getName(),
                        afterPatch.getDescription(),
                        afterPatch.getAllowedHeaders(),
                        afterPatch.isAllowCredentials(),
                        afterPatch.getAllowOrigin().stream().map(Origin::new).collect(Collectors.toSet()),
                        afterPatch.getExposedHeaders(),
                        afterPatch.getMaxAge()
                );
            }
            return null;
        }, CORS_PROFILE);
    }
}
