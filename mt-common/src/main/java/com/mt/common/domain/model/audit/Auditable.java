package com.mt.common.domain.model.audit;

import com.mt.common.domain.model.restful.exception.AggregateOutdatedException;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

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
            throw new AggregateOutdatedException();
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
