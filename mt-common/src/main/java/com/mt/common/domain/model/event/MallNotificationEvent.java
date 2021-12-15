package com.mt.common.domain.model.event;

import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
@NoArgsConstructor
public class MallNotificationEvent extends DomainEvent {
    @Getter
    private final Map<String,String> details=new HashMap<>();
    @Setter
    @Getter
    private String changeId;
    @Setter
    private String message;
    @Setter
    @Getter
    private String name;

    private MallNotificationEvent(String name) {
        setInternal(false);
        setTopic(AppInfo.EventName.MT3_MALL_NOTIFICATION);
        setName(name);
    }
    public void addDetail(String key,String value){
        this.details.put(key,value);
    }
    public void setOrderId(String orderId){
        setDomainId(new DomainId(orderId));
    }
    public static MallNotificationEvent create(String name){
        return new MallNotificationEvent(name);
    }
}
