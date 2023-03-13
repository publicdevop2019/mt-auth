package com.mt.access.domain.model.cors_profile;

import com.mt.access.domain.model.cors_profile.event.CorsProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CorsProfileUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.sql.converter.StringSetConverter;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
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
    private String name;
    private String description;
    @Embedded
    private CorsProfileId corsId;
    private boolean allowCredentials;
    @Convert(converter = StringSetConverter.class)
    private Set<String> allowedHeaders;

    @Lob
    @Convert(converter = Origin.OriginConverter.class)
    private Set<Origin> allowOrigin;

    @Convert(converter = StringSetConverter.class)
    private Set<String> exposedHeaders;
    private Long maxAge;

    public CorsProfile(
        String name,
        String description,
        Set<String> allowedHeaders,
        boolean allowCredentials,
        Set<Origin> allowOrigin,
        Set<String> exposedHeaders,
        Long maxAge,
        CorsProfileId corsId) {
        super();
        setName(name);
        setDescription(description);
        setAllowedHeaders(allowedHeaders);
        setAllowCredentials(allowCredentials);
        setAllowOrigin(allowOrigin);
        setExposedHeaders(exposedHeaders);
        setMaxAge(maxAge);
        setCorsId(corsId);
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
    }

    public void update(
        String name,
        String description,
        Set<String> allowedHeaders, Boolean allowCredentials, Set<Origin> allowOrigin,
        Set<String> exposedHeaders, Long maxAge) {
        setName(name);
        setDescription(description);
        setAllowedHeaders(allowedHeaders);
        setAllowCredentials(allowCredentials);
        setAllowOrigin(allowOrigin);
        setExposedHeaders(exposedHeaders);
        setMaxAge(maxAge);
        CorsProfile copy = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        CorsProfile copy2 = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        copy.setName(null);
        copy2.setName(null);
        copy.setDescription(null);
        copy2.setDescription(null);
        if (!copy.equals(copy2)) {
            CommonDomainRegistry.getDomainEventRepository().append(new CorsProfileUpdated(this));
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
