package com.mt.access.domain.model.cors_profile;

import com.google.common.base.Objects;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.UrlValidator;

import javax.persistence.AttributeConverter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class Origin implements Serializable {
    private static final long serialVersionUID = 1;
    private static final UrlValidator defaultValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String value;

    public Origin(String url) {
        if (defaultValidator.isValid(url)) {
            value = url;
        } else {
            throw new InvalidOriginValueException();
        }
    }

    public static class InvalidOriginValueException extends RuntimeException {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Origin)) return false;
        Origin that = (Origin) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public static class OriginConverter implements AttributeConverter<Set<Origin>, byte[]> {
        @Override
        public byte[] convertToDatabaseColumn(Set<Origin> redirectURLS) {
            if (redirectURLS == null || redirectURLS.isEmpty())
                return null;
            return CommonDomainRegistry.getCustomObjectSerializer().serializeCollection(redirectURLS).getBytes();
        }

        @Override
        public Set<Origin> convertToEntityAttribute(byte[] bytes) {
            if (bytes == null || bytes.length == 0)
                return Collections.emptySet();
            Collection<Origin> redirectURLS = CommonDomainRegistry.getCustomObjectSerializer().deserializeCollection(new String(bytes, StandardCharsets.UTF_8), Origin.class);
            return new HashSet<>(redirectURLS);
        }
    }
}
