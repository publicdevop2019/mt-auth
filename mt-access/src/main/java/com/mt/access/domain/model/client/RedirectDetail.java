package com.mt.access.domain.model.client;

import com.mt.common.domain.CommonDomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor
public class RedirectDetail implements Serializable {

    @Lob
    @Getter
    @Convert(converter = RedirectURLConverter.class)
    private final Set<RedirectURL> redirectUrls = new HashSet<>();
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private boolean autoApprove = false;

    public RedirectDetail(Set<String> redirectUrls, boolean autoApprove) {
        if (redirectUrls != null) {
            setRedirectUrls(redirectUrls.stream().map(RedirectURL::new).collect(Collectors.toSet()));
        }
        setAutoApprove(autoApprove);
    }

    private void setRedirectUrls(Set<RedirectURL> redirectUrls) {
        this.redirectUrls.clear();
        this.redirectUrls.addAll(redirectUrls);
    }

    private static class RedirectURLConverter implements AttributeConverter<Set<RedirectURL>, byte[]> {
        @Override
        public byte[] convertToDatabaseColumn(Set<RedirectURL> redirectURLS) {
            if (redirectURLS == null || redirectURLS.isEmpty())
                return null;
            return CommonDomainRegistry.getCustomObjectSerializer().serializeCollection(redirectURLS).getBytes();
        }

        @Override
        public Set<RedirectURL> convertToEntityAttribute(byte[] bytes) {
            if (bytes == null || bytes.length == 0)
                return Collections.emptySet();
            Collection<RedirectURL> redirectURLS = CommonDomainRegistry.getCustomObjectSerializer().deserializeCollection(new String(bytes, StandardCharsets.UTF_8), RedirectURL.class);
            return new HashSet<>(redirectURLS);
        }
    }
}
