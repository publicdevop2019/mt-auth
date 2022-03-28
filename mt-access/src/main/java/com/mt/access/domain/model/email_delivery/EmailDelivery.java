package com.mt.access.domain.model.email_delivery;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"deliverTo", "bizType", "deleted"}))
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class EmailDelivery extends Auditable {
    @Column
    private String deliverTo;

    @Column
    private BizType bizType;

    @Column
    private Boolean lastTimeResult;

    @Column
    private Date lastSuccessTime;

    public EmailDelivery(String deliverTo, BizType bizType) {
        super();
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setDeliverTo(deliverTo);
        setBizType(bizType);
        setLastSuccessTime(null);
        setLastTimeResult(false);
    }

    public static EmailDelivery create(String deliverTo, BizType bizType) {
        return new EmailDelivery(deliverTo, bizType);
    }

    public Boolean hasCoolDown() {
        if (lastSuccessTime == null) {
            return true;
        }
        if (bizType.equals(BizType.PWD_RESET)) {
            return System.currentTimeMillis() > lastSuccessTime.getTime() + 60 * 1000;
        } else if (bizType.equals(BizType.NEW_USER_CODE)) {
            return System.currentTimeMillis() > lastSuccessTime.getTime() + 60 * 1000;
        } else {
            throw new UnknownBizTypeException();
        }
    }

    public void onMsgSendSuccess() {
        lastTimeResult = Boolean.TRUE;
        lastSuccessTime = new Date(System.currentTimeMillis());
    }
}
