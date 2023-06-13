package com.mt.access.domain.model.cors_profile;

import com.mt.access.domain.model.cors_profile.event.CorsProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CorsProfileUpdated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "cors_profile")
@Slf4j
@NoArgsConstructor
@Getter
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "corsProfileRegion")
@Setter(AccessLevel.PRIVATE)
public class CorsProfile extends Auditable {
    private static Pattern HEADER_NAME_REGEX = Pattern.compile("^[a-z-]+$");
    private String name;
    private String description;
    @Embedded
    private CorsProfileId corsId;
    private Boolean allowCredentials;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "allowed_header_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "allowed_header")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "allowedHeadersRegion")
    private Set<String> allowedHeaders;

    @Getter
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "cors_origin_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "allowed_origin")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "corsOriginRegion")
    @Convert(converter = Origin.OriginConverter.class)
    private Set<Origin> allowOrigin;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "exposed_header_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "exposed_header")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "exposedHeadersRegion")
    private Set<String> exposedHeaders;
    private Long maxAge;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId",
            column = @Column(name = "projectId", updatable = false, nullable = false))
    })
    private ProjectId projectId;

    public CorsProfile(
        String name,
        String description,
        Set<String> allowedHeaders,
        Boolean allowCredentials,
        Set<String> allowOrigin,
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

    public void update(
        String name,
        String description,
        Set<String> allowedHeaders, Boolean allowCredentials, Set<String> allowOrigin,
        Set<String> exposedHeaders, Long maxAge) {
        CorsProfile original =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        //exclude name and description from comparison and bypass setter check
        original.name = null;
        original.description = null;
        setName(name);
        setDescription(description);
        setAllowedHeaders(allowedHeaders);
        setAllowCredentials(allowCredentials);
        setAllowOrigin(allowOrigin);
        setExposedHeaders(exposedHeaders);
        setMaxAge(maxAge);
        CorsProfile afterUpdate =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        afterUpdate.name = null;
        afterUpdate.description = null;
        if (!original.equals(afterUpdate)) {
            CommonDomainRegistry.getDomainEventRepository().append(new CorsProfileUpdated(this));
        }
    }

    public void update(
        String name,
        String description
    ) {
        setName(name);
        setDescription(description);
    }

    private void setName(String name) {
        Validator.validRequiredString(1, 50, name);
        this.name = name.trim();
    }

    public void setDescription(String description) {
        Validator.validOptionalString(100, description);
        if (Checker.notNull(description)) {
            description = description.trim();
        }
        this.description = description;
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
        if (!Objects.equals(allowedHeaders, this.allowedHeaders)) {
            if (this.allowedHeaders != null) {
                this.allowedHeaders.clear();
            }
            this.allowedHeaders = allowedHeaders;
        }
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

    private void setAllowOrigin(Set<String> origins) {
        Validator.validRequiredCollection(1, 5, origins);
        Set<Origin> allowOrigin = origins.stream().map(Origin::new).collect(Collectors.toSet());
        if (!Objects.equals(allowOrigin, this.allowOrigin)) {
            if (this.allowOrigin != null) {
                this.allowOrigin.clear();
            }
            this.allowOrigin = allowOrigin;
        }
    }

    private void setExposedHeaders(Set<String> exposedHeaders) {
        Validator.validOptionalCollection(10, exposedHeaders);
        if (exposedHeaders != null) {
            validateHeaderName(exposedHeaders);
        }
        if (!Objects.equals(exposedHeaders, this.exposedHeaders)) {
            if (this.exposedHeaders != null) {
                this.exposedHeaders.clear();
            }
            this.exposedHeaders = exposedHeaders;
        }
    }

    public void removeAllReference() {
        CommonDomainRegistry.getDomainEventRepository().append(new CorsProfileRemoved(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CorsProfile that = (CorsProfile) o;
        return Objects.equals(corsId, that.corsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), corsId);
    }
}
