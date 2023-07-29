package com.mt.common.domain.model.audit;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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

    @Id
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @CreatedBy
    @Setter(AccessLevel.PROTECTED)
    @Getter
    private String createdBy;
    @CreatedDate
    @Getter
    private Long createdAt;
    @LastModifiedBy
    @Getter
    private String modifiedBy;
    @LastModifiedDate
    @Getter
    private Long modifiedAt;
    @Version
    @Setter(AccessLevel.PRIVATE)
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
