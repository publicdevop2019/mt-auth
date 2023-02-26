package com.mt.access.domain.model.cors_profile;

import com.google.common.base.Objects;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.UrlValidator;

@NoArgsConstructor
public class Origin implements Serializable {
    private static final long serialVersionUID = 1;
    private static final UrlValidator defaultValidator =
        new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String value;

    public Origin(String url) {
        if (defaultValidator.isValid(url)) {
            value = url;
        } else {
            throw new DefinedRuntimeException("invalid origin value", "0039",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Origin)) {
            return false;
        }
        Origin that = (Origin) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public static class OriginConverter implements AttributeConverter<Set<Origin>, byte[]> {
        @Override
        public byte[] convertToDatabaseColumn(Set<Origin> redirectUrls) {
            if (redirectUrls == null || redirectUrls.isEmpty()) {
                return null;
            }
            return CommonDomainRegistry.getCustomObjectSerializer()
                .serializeCollection(redirectUrls).getBytes();
        }

        @Override
        public Set<Origin> convertToEntityAttribute(byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
                return Collections.emptySet();
            }
            Collection<Origin> redirectUrls = CommonDomainRegistry.getCustomObjectSerializer()
                .deserializeCollection(new String(bytes, StandardCharsets.UTF_8), Origin.class);
            return new HashSet<>(redirectUrls);
        }
    }
}
