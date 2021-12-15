package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.event.*;
import com.mt.access.domain.model.cors_profile.event.CORSProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CORSProfileUpdated;
import com.mt.access.domain.model.system_role.event.SystemRoleDeleted;
import com.mt.access.domain.model.user.event.UserAuthorityChanged;
import com.mt.access.domain.model.user.event.UserDeleted;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MQHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.access.domain.model.client.event.ClientAccessibilityRemoved.CLIENT_ACCESSIBILITY_REMOVED;
import static com.mt.access.domain.model.client.event.ClientAsResourceDeleted.CLIENT_AS_RESOURCE_DELETED;
import static com.mt.access.domain.model.client.event.ClientAuthoritiesChanged.CLIENT_AUTHORITIES_CHANGED;
import static com.mt.access.domain.model.client.event.ClientDeleted.CLIENT_DELETED;
import static com.mt.access.domain.model.client.event.ClientGrantTypeChanged.CLIENT_GRANT_TYPE_CHANGED;
import static com.mt.access.domain.model.client.event.ClientResourceCleanUpCompleted.CLIENT_RESOURCE_CLEAN_UP_COMPLETED;
import static com.mt.access.domain.model.client.event.ClientResourcesChanged.CLIENT_RESOURCES_CHANGED;
import static com.mt.access.domain.model.client.event.ClientScopesChanged.CLIENT_SCOPES_CHANGED;
import static com.mt.access.domain.model.client.event.ClientSecretChanged.CLIENT_SECRET_CHANGED;
import static com.mt.access.domain.model.client.event.ClientTokenDetailChanged.CLIENT_TOKEN_DETAIL_CHANGED;
import static com.mt.access.domain.model.cors_profile.event.CORSProfileRemoved.CORS_PROFILE_REMOVED;
import static com.mt.access.domain.model.cors_profile.event.CORSProfileUpdated.CORS_PROFILE_UPDATED;
import static com.mt.access.domain.model.system_role.event.SystemRoleDeleted.SYSTEM_ROLE_DELETED;
import static com.mt.access.domain.model.user.event.UserAuthorityChanged.USER_AUTHORITY_CHANGED;
import static com.mt.access.domain.model.user.event.UserDeleted.USER_DELETED;
import static com.mt.access.domain.model.user.event.UserGetLocked.USER_GET_LOCKED;

@Slf4j
@Component
public class DomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener16() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, MQHelper.handlerOf(appName+"_user", SYSTEM_ROLE_DELETED), (event) -> {
            SystemRoleDeleted deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), SystemRoleDeleted.class);
            ApplicationServiceRegistry.getUserApplicationService().handleChange(deserialize);
        },SYSTEM_ROLE_DELETED);
    }

}
