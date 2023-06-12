package com.mt.access.domain.model.endpoint;


import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class EndpointValidator {
    private final Endpoint endpoint;
    private final ValidationNotificationHandler handler;

    public EndpointValidator(Endpoint client, ValidationNotificationHandler handler) {
        this.endpoint = client;
        this.handler = handler;
    }

    protected void validate() {
        httpMethod();
        csrf();
        ifSecureThenRoleGroupIdMustExist();
        onlyGetCanHaveCacheConfig();
    }

    private void onlyGetCanHaveCacheConfig() {
        if (endpoint.getCacheProfileId() != null) {
            if (endpoint.getWebsocket() || !"get".equalsIgnoreCase(endpoint.getMethod())) {
                handler.handleError("cache can only configured for http get calls");
            }
        }
    }

    private void csrf() {
        if (endpoint.getWebsocket() && endpoint.getCsrfEnabled() != null) {
            handler.handleError("websocket endpoints can not have csrf enabled");
        }
    }

    private void ifSecureThenRoleGroupIdMustExist() {
        if (endpoint.getSecured() && endpoint.getPermissionId() == null) {
            handler.handleError("secured endpoint must have role group id");
        }
    }

    private void httpMethod() {
        if (Boolean.FALSE.equals(endpoint.getWebsocket())) {
            if (endpoint.getMethod() == null || endpoint.getMethod().isBlank()) {
                handler.handleError("non websocket endpoints must have method");
            }
        }
    }


}
