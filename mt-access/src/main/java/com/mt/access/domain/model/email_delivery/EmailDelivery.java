package com.mt.access.domain.model.email_delivery;

import com.mt.common.domain.model.audit.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"deliverTo", "bizType"}))
@NoArgsConstructor
public class EmailDelivery extends Auditable {
    @Id
    private Long id;

    @Column
    private String deliverTo;

    @Column
    private BizType bizType;

    @Column
    private Boolean lastTimeResult;

    @Column
    private Date lastSuccessTime;

    public static EmailDelivery create(Long id, String deliverTo, BizType bizType) {
        return new EmailDelivery(id, deliverTo, bizType);
    }

    public EmailDelivery(Long id, String deliverTo, BizType bizType) {
        this.id = id;
        this.deliverTo = deliverTo;
        this.bizType = bizType;
        this.lastTimeResult = false;
        this.lastSuccessTime = null;
    }

    public Boolean hasCoolDown() {
        if (lastSuccessTime == null)
            return true;
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
