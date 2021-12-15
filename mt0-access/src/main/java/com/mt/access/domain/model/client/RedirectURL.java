package com.mt.access.domain.model.client;

import com.google.common.base.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.Serializable;
@NoArgsConstructor
public class RedirectURL implements Serializable {
    private static final long serialVersionUID = 1;
    private static final UrlValidator defaultValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String value;

    public RedirectURL(String url) {
        if (defaultValidator.isValid(url)) {
            value = url;
        } else {
            throw new InvalidRedirectURLException();
        }
    }

    public static class InvalidRedirectURLException extends RuntimeException {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RedirectURL)) return false;
        RedirectURL that = (RedirectURL) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
