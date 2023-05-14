package com.mt.access.domain.model.client;

import com.mt.access.port.adapter.persistence.client.RedirectUrlConverter;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Embeddable
@NoArgsConstructor
public class RedirectDetail implements Serializable {

    @Getter
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "client_redirect_url_map", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "redirect_url")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "clientRedirectUrlRegion")
    @Convert(converter = RedirectUrlConverter.class)
    private final Set<RedirectUrl> redirectUrls = new HashSet<>();

    @Setter(value = AccessLevel.PRIVATE)
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
        if (!this.redirectUrls.equals(redirectUrls)) {
            this.redirectUrls.clear();
            this.redirectUrls.addAll(redirectUrls);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RedirectDetail that = (RedirectDetail) o;
        return autoApprove == that.autoApprove &&
            Objects.equals(redirectUrls, that.redirectUrls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(redirectUrls, autoApprove);
    }
}
