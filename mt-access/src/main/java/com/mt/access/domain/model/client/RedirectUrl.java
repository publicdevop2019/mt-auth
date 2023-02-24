package com.mt.access.domain.model.client;

import com.google.common.base.Objects;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.UrlValidator;

@NoArgsConstructor
public class RedirectUrl implements Serializable {
    private static final long serialVersionUID = 1;
    private static final UrlValidator defaultValidator =
        new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String value;

    public RedirectUrl(String url) {
        if (defaultValidator.isValid(url)) {
            value = url;
        } else {
            throw new DefinedRuntimeException("invalid redirect url", "0000",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RedirectUrl)) {
            return false;
        }
        RedirectUrl that = (RedirectUrl) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
