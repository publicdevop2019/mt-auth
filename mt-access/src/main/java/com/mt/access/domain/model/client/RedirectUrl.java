package com.mt.access.domain.model.client;

import com.google.common.base.Objects;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.UrlValidator;

@NoArgsConstructor
@MappedSuperclass
public class RedirectUrl implements Serializable {
    private static final long serialVersionUID = 1;
    private static final UrlValidator defaultValidator =
        new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String value;

    public RedirectUrl(String url) {
        Validator.notNull(url);
        if (defaultValidator.isValid(url)) {
            value = url;
        } else {
            throw new DefinedRuntimeException("invalid redirect url", "1038",
                HttpResponseCode.BAD_REQUEST);
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
