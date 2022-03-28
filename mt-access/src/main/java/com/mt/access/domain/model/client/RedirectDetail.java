package com.mt.access.domain.model.client;

import com.mt.common.domain.CommonDomainRegistry;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
public class RedirectDetail implements Serializable {

    @Lob
    @Getter
    @Convert(converter = RedirectUrlConverter.class)
    private final Set<RedirectUrl> redirectUrls = new HashSet<>();
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private boolean autoApprove = false;

    public RedirectDetail(Set<String> redirectUrls, boolean autoApprove) {
        if (redirectUrls != null) {
            setRedirectUrls(
                redirectUrls.stream().map(RedirectUrl::new).collect(Collectors.toSet()));
        }
        setAutoApprove(autoApprove);
    }

    private void setRedirectUrls(Set<RedirectUrl> redirectUrls) {
        this.redirectUrls.clear();
        this.redirectUrls.addAll(redirectUrls);
    }

    private static class RedirectUrlConverter
        implements AttributeConverter<Set<RedirectUrl>, byte[]> {
        @Override
        public byte[] convertToDatabaseColumn(Set<RedirectUrl> redirectUrls) {
            if (redirectUrls == null || redirectUrls.isEmpty()) {
                return null;
            }
            return CommonDomainRegistry.getCustomObjectSerializer()
                .serializeCollection(redirectUrls).getBytes();
        }

        @Override
        public Set<RedirectUrl> convertToEntityAttribute(byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
                return Collections.emptySet();
            }
            Collection<RedirectUrl> redirectUrls = CommonDomainRegistry.getCustomObjectSerializer()
                .deserializeCollection(new String(bytes, StandardCharsets.UTF_8),
                    RedirectUrl.class);
            return new HashSet<>(redirectUrls);
        }
    }
}
