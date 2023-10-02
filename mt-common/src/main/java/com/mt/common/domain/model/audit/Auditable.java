package com.mt.common.domain.model.audit;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public abstract class Auditable implements Serializable {
    public static final String DB_ID = "id";
    public static final String DB_CREATED_BY = "created_by";
    public static final String DB_CREATED_AT = "created_at";
    public static final String DB_MODIFIED_BY = "modified_by";
    public static final String DB_MODIFIED_AT = "modified_at";
    public static final String DB_VERSION = "version";
    @Setter
    @Getter
    protected Long id;
    @Setter
    @Getter
    private String createdBy;
    @Getter
    @Setter
    private Long createdAt;
    @Getter
    @Setter
    private String modifiedBy;
    @Getter
    @Setter
    private Long modifiedAt;
    @Setter
    @Getter
    private Integer version;

    public void checkVersion(Integer version) {
        if (!getVersion().equals(version)) {
            throw new DefinedRuntimeException("aggregate outdated", "0009",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public void validate(ValidationNotificationHandler handler) {
        Validator.notNull(handler);
    }

}
