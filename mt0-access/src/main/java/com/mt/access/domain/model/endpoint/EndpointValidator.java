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
        if(endpoint.getCacheProfileId()!=null){
            if (endpoint.isWebsocket()||!"get".equalsIgnoreCase(endpoint.getMethod())) {
                handler.handleError("cache can only configured for http get calls");
            }
        }
    }

    private void csrf() {
        if (endpoint.isWebsocket()&&endpoint.isCsrfEnabled()) {
            handler.handleError("websocket endpoints can not have csrf enabled");
        }
    }
    private void ifSecureThenRoleGroupIdMustExist() {
        if (endpoint.isSecured()&&endpoint.getSystemRoleId()==null) {
            handler.handleError("secured endpoint must have role group id");
        }
    }

    private void httpMethod() {
        if (!endpoint.isWebsocket()) {
            if (endpoint.getMethod() == null || endpoint.getMethod().isBlank())
                handler.handleError("non websocket endpoints must have method");
        }
    }



}
