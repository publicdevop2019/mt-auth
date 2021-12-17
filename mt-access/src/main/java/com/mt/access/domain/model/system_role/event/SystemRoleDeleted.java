package com.mt.access.domain.model.system_role.event;

import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SystemRoleDeleted extends DomainEvent {

    public static final String SYSTEM_ROLE_DELETED = "system_role_deleted";
    public static final String name = "SYSTEM_ROLE_DELETED";
    public SystemRoleDeleted(SystemRoleId userId) {
        super(userId);
        setTopic(SYSTEM_ROLE_DELETED);
        setName(name);
    }
}
