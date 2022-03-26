package com.mt.access.domain.model.cors_profile;

import com.mt.access.domain.model.cors_profile.event.CORSProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CORSProfileUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;

import com.mt.common.domain.model.sql.converter.StringSetConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cors_profile")
@Slf4j
@NoArgsConstructor
@Getter
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "corsProfileRegion")
@Setter(AccessLevel.PRIVATE)
public class CORSProfile extends Auditable {
    private String name;
    private String description;
    @Embedded
    private CORSProfileId corsId;
    private boolean allowCredentials;
    @Convert(converter = StringSetConverter.class)
    private Set<String> allowedHeaders;

    @Lob
    @Convert(converter = Origin.OriginConverter.class)
    private Set<Origin> allowOrigin;

    @Convert(converter = StringSetConverter.class)
    private Set<String> exposedHeaders;
    private Long maxAge;

    public CORSProfile(
            String name,
            String description,
            Set<String> allowedHeaders,
                       boolean allowCredentials,
                       Set<Origin> allowOrigin,
                       Set<String> exposedHeaders,
                       Long maxAge,
                       CORSProfileId corsId) {
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
            Set<String> allowedHeaders, Boolean allowCredentials, Set<Origin> allowOrigin, Set<String> exposedHeaders, Long maxAge) {
        CORSProfile copy = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        setName(name);
        setDescription(description);
        setAllowedHeaders(allowedHeaders);
        setAllowCredentials(allowCredentials);
        setAllowOrigin(allowOrigin);
        setExposedHeaders(exposedHeaders);
        setMaxAge(maxAge);
        CORSProfile copy2 = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        copy.setName(null);
        copy2.setName(null);
        copy.setDescription(null);
        copy2.setDescription(null);
        if(!copy.equals(copy2)){
            CommonDomainRegistry.getDomainEventRepository().append(new CORSProfileUpdated(this));
        }
    }

    public void removeAllReference() {
        CommonDomainRegistry.getDomainEventRepository().append(new CORSProfileRemoved(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CORSProfile that = (CORSProfile) o;
        return Objects.equals(corsId, that.corsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), corsId);
    }
}
