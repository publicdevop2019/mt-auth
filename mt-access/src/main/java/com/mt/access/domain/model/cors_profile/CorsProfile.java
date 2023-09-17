package com.mt.access.domain.model.cors_profile;

import com.mt.access.domain.model.cors_profile.event.CorsProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CorsProfileUpdated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "cors_profile")
@Getter
@EqualsAndHashCode(callSuper = true)
public class CorsProfile extends Auditable {
    private static final Pattern HEADER_NAME_REGEX = Pattern.compile("^[a-zA-Z-]+$");
    private String name;
    private String description;
    @Embedded
    private CorsProfileId corsId;
    private Boolean allowCredentials;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "allowed_header_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "allowed_header")
    private Set<String> allowedHeaders = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "cors_origin_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "allowed_origin")
    @Convert(converter = Origin.OriginConverter.class)
    private Set<Origin> allowOrigin = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "exposed_header_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "exposed_header")
    private Set<String> exposedHeaders = new LinkedHashSet<>();
    private Long maxAge;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId",
            column = @Column(name = "projectId", updatable = false, nullable = false))
    })
    private ProjectId projectId;

    private CorsProfile() {
    }

    public CorsProfile(
        String name,
        String description,
        Set<String> allowedHeaders,
        Boolean allowCredentials,
        Set<Origin> allowOrigin,
        Set<String> exposedHeaders,
        Long maxAge,
        CorsProfileId corsId,
        ProjectId projectId
    ) {
        super();
        setName(name);
        setDescription(description);
        setAllowedHeaders(allowedHeaders);
        setAllowCredentials(allowCredentials);
        setAllowOrigin(allowOrigin);
        setExposedHeaders(exposedHeaders);
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
        Set<String> allowedHeaders,
        Boolean allowCredentials,
        Set<Origin> allowOrigin,
        Set<String> exposedHeaders,
        Long maxAge,
        TransactionContext context
    ) {
        CorsProfile updated =
            CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, CorsProfile.class);
        updated.setName(name);
        updated.setDescription(description);
        updated.setAllowedHeaders(allowedHeaders);
        updated.setAllowCredentials(allowCredentials);
        updated.setAllowOrigin(allowOrigin);
        updated.setExposedHeaders(exposedHeaders);
        updated.setMaxAge(maxAge);
        if (this.keyFieldChanged(updated)) {
            context.append(new CorsProfileUpdated(this));
        }
        return updated;
    }

    private boolean keyFieldChanged(CorsProfile o) {
        return
            !Objects.equals(allowCredentials, o.allowCredentials) ||
                !Objects.equals(exposedHeaders, o.exposedHeaders) ||
                !Objects.equals(allowedHeaders, o.allowedHeaders) ||
                !Objects.equals(maxAge, o.maxAge) ||
                !Objects.equals(allowOrigin, o.allowOrigin);
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
        if (Checker.notNull(description)) {
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
        if (Checker.notNull(maxAge)) {
            Validator.lessThanOrEqualTo(maxAge, 60 * 60 * 2);
            Validator.greaterThanOrEqualTo(maxAge, 5);
        }
        this.maxAge = maxAge;
    }

    private void setAllowCredentials(Boolean allowCredentials) {
        Validator.notNull(allowCredentials);
        this.allowCredentials = allowCredentials;
    }

    private void setAllowedHeaders(Set<String> allowedHeaders) {
        Validator.validOptionalCollection(10, allowedHeaders);
        if (Checker.notNull(allowedHeaders)) {
            validateHeaderName(allowedHeaders);
        }
        CommonUtility.updateCollection(this.allowedHeaders, allowedHeaders,
            () -> this.allowedHeaders = allowedHeaders);
    }

    private static void validateHeaderName(Set<String> headerNames) {
        headerNames.forEach(header -> {
            boolean pass = false;
            Matcher matcher = HEADER_NAME_REGEX.matcher(header);
            if (matcher.find()) {
                if (!header.startsWith("-") && !header.endsWith("-") &&
                    !header.equalsIgnoreCase("-")) {
                    pass = true;
                }
            }
            if (!pass) {
                throw new DefinedRuntimeException("invalid header format", "1085",
                    HttpResponseCode.BAD_REQUEST);
            }
        });
    }

    private void setAllowOrigin(Set<Origin> origins) {
        Validator.validRequiredCollection(1, 5, origins);
        CommonUtility.updateCollection(this.allowOrigin, origins,
            () -> this.allowOrigin = origins);
    }

    private void setExposedHeaders(Set<String> headers) {
        Validator.validOptionalCollection(10, headers);
        if (Checker.notNull(headers)) {
            validateHeaderName(headers);
        }
        CommonUtility.updateCollection(this.exposedHeaders, headers,
            () -> this.exposedHeaders = headers);
    }

    public void removeAllReference(TransactionContext context) {
        context.append(new CorsProfileRemoved(this));
    }
}
