package com.mt.common.domain.model.audit;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
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
public abstract class Auditable implements Serializable {

    @Id
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    protected Long id;
    @CreatedBy
    @Setter(AccessLevel.PROTECTED)
    @Getter
    private String createdBy;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    private Date createdAt;
    @LastModifiedBy
    @Getter
    private String modifiedBy;
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    private Date modifiedAt;
    private long deleted = 0;
    @Version
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Integer version;

    public void checkVersion(Integer version) {
        if (!getVersion().equals(version)) {
            throw new DefinedRuntimeException("aggregate outdated", "0009",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_STATE);
        }
    }

    public void validate(@NotNull ValidationNotificationHandler handler) {
    }

    public void softDelete() {
        this.deleted = getId();
    }

    public void restore() {
        this.deleted = 0L;
    }
}
