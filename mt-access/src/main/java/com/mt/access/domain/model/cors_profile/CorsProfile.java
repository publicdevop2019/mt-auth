package com.mt.access.domain.model.cors_profile;

import com.mt.access.domain.model.cors_profile.event.CorsProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CorsProfileUpdated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@EqualsAndHashCode(callSuper = true)
public class CorsProfile extends Auditable {

    private String name;
    private String description;
    private CorsProfileId corsId;
    private Boolean allowCredentials;
    private Long maxAge;

    private ProjectId projectId;

    private CorsProfile() {
    }

    public CorsProfile(
        String name,
        String description,
        Boolean allowCredentials,
        Long maxAge,
        CorsProfileId corsId,
        ProjectId projectId
    ) {
        super();
        setName(name);
        setDescription(description);
        setAllowCredentials(allowCredentials);
        setMaxAge(maxAge);
        setCorsId(corsId);
        setProjectId(projectId);
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
    }

    public static CorsProfile fromDatabaseRow(Long id, Long createdAt, String createdBy,
                                              Long modifiedAt, String modifiedBy, Integer version,
                                              Boolean allowCredentials, CorsProfileId domainId,
                                              String description, Long maxAge, String name,
                                              ProjectId projectId) {
        CorsProfile corsProfile = new CorsProfile();
        corsProfile.setId(id);
        corsProfile.setCreatedAt(createdAt);
        corsProfile.setCreatedBy(createdBy);
        corsProfile.setModifiedAt(modifiedAt);
        corsProfile.setModifiedBy(modifiedBy);
        corsProfile.setVersion(version);
        corsProfile.setAllowCredentials(allowCredentials);
        corsProfile.setCorsId(domainId);
        corsProfile.setDescription(description);
        corsProfile.setMaxAge(maxAge);
        corsProfile.setName(name);
        corsProfile.setProjectId(projectId);
        return corsProfile;
    }



    public CorsProfile update(
        String name,
        String description,
        Boolean allowCredentials,
        Long maxAge,
        TransactionContext context
    ) {
        CorsProfile updated =
            CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, CorsProfile.class);
        updated.setName(name);
        updated.setDescription(description);
        updated.setAllowCredentials(allowCredentials);
        updated.setMaxAge(maxAge);
        if (this.keyFieldChanged(updated)) {
            context.append(new CorsProfileUpdated(this));
        }
        return updated;
    }

    private boolean keyFieldChanged(CorsProfile o) {
        return
            !Objects.equals(allowCredentials, o.allowCredentials) ||
                !Objects.equals(maxAge, o.maxAge);
    }

    public void update(
        String name,
        String description
    ) {
        setName(name);
        setDescription(description);
    }

    private void setCorsId(CorsProfileId corsId) {
        this.corsId = corsId;
    }

    private void setName(String name) {
        Validator.validRequiredString(1, 50, name);
        this.name = name.trim();
    }

    private void setDescription(String description) {
        Validator.validOptionalString(100, description);
        if (Utility.notNull(description)) {
            description = description.trim();
        }
        this.description = description;
    }

    private void setProjectId(ProjectId projectId) {
        this.projectId = projectId;
    }

    /**
     * set Access-Control-Max-Age for preflight request
     * note: Firefox caps this at 24 hours (86400 seconds)
     * Chromium (prior to v76) caps at 10 minutes (600 seconds)
     * Chromium (starting in v76) caps at 2 hours (7200 seconds)
     * The default value is 5 seconds
     *
     * @param maxAge max age
     */
    private void setMaxAge(Long maxAge) {
        if (Utility.notNull(maxAge)) {
            Validator.lessThanOrEqualTo(maxAge, 60 * 60 * 2);
            Validator.greaterThanOrEqualTo(maxAge, 5);
        }
        this.maxAge = maxAge;
    }

    private void setAllowCredentials(Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public void removeAllReference(TransactionContext context) {
        context.append(new CorsProfileRemoved(this));
    }
}
