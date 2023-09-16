package com.mt.common.domain.model.audit;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public abstract class Auditable implements Serializable {
    public static final String DB_ID = "id";
    public static final String DB_CREATED_BY = "created_by";
    public static final String DB_CREATED_AT = "created_at";
    public static final String DB_MODIFIED_BY = "modified_by";
    public static final String DB_MODIFIED_AT = "modified_at";
    public static final String DB_VERSION = "version";
    @Id
    @Setter
    @Getter
    protected Long id;
    @CreatedBy
    @Setter
    @Getter
    private String createdBy;
    @CreatedDate
    @Getter
    @Setter
    private Long createdAt;
    @LastModifiedBy
    @Getter
    @Setter
    private String modifiedBy;
    @LastModifiedDate
    @Getter
    @Setter
    private Long modifiedAt;
    @Version
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
