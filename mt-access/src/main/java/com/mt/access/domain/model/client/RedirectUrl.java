package com.mt.access.domain.model.client;

import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.UrlValidator;

@NoArgsConstructor
@MappedSuperclass
@EqualsAndHashCode
public class RedirectUrl implements Serializable {
    private static final long serialVersionUID = 1;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String value;

    public RedirectUrl(String url) {
        Validator.notNull(url);
        Validator.isHttpUrl(url);
        value = url;
    }
}
