package com.mt.access.domain.model.client;

import com.mt.access.port.adapter.persistence.client.RedirectUrlConverter;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.CommonUtility;
import java.io.Serializable;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private Set<RedirectUrl> redirectUrls;

    @Getter
    private Boolean autoApprove;

    public RedirectDetail(Set<String> redirectUrls, Boolean autoApprove) {
        setRedirectUrls(redirectUrls);
        setAutoApprove(autoApprove);
    }

    private void setAutoApprove(Boolean autoApprove) {
        Validator.notNull(autoApprove);
        this.autoApprove = autoApprove;
    }

    private void setRedirectUrls(Set<String> redirectUrls) {
        Validator.notNull(redirectUrls);
        Validator.notEmpty(redirectUrls);
        Validator.lessThanOrEqualTo(redirectUrls, 5);
        Set<RedirectUrl> collect =
            redirectUrls.stream().map(RedirectUrl::new).collect(Collectors.toSet());
        CommonUtility.updateCollection(this.redirectUrls, collect,
            () -> this.redirectUrls = collect);
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
