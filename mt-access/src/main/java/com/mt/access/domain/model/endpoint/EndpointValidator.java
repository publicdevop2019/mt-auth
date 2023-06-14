package com.mt.access.domain.model.endpoint;


import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class EndpointValidator {
    private final Endpoint endpoint;
    private final ValidationNotificationHandler handler;

    public EndpointValidator(Endpoint client, ValidationNotificationHandler handler) {
        this.endpoint = client;
        this.handler = handler;
    }

    protected void validate() {
        websocketAndHttpMethod();
        websocketAndCsrf();
        ifSecureThenPermissionIdMustExist();
        onlyGetCanHaveCacheConfig();
        replenishRateAndBurstCapacity();
    }

    private void replenishRateAndBurstCapacity() {
        if (Checker.isTrue(endpoint.getWebsocket()) &&
            (Checker.notNull(endpoint.getBurstCapacity()) ||
                Checker.notNull(endpoint.getReplenishRate()))) {
            handler.handleError("websocket endpoints can not have rate limit config");
        }
        if (Checker.isFalse(endpoint.getWebsocket()) &&
            (Checker.isNull(endpoint.getReplenishRate()) ||
                Checker.isNull(endpoint.getBurstCapacity()))) {
            handler.handleError("none-websocket endpoints must have rate limit config");
        }
        if (Checker.isFalse(endpoint.getWebsocket())) {
            if (endpoint.getBurstCapacity() < endpoint.getReplenishRate()) {
                handler.handleError("replenish rate must less than or equal to burst capacity");
            }
        }

    }

    private void onlyGetCanHaveCacheConfig() {
        if (endpoint.getCacheProfileId() != null) {
            if (endpoint.getWebsocket() || !"get".equalsIgnoreCase(endpoint.getMethod())) {
                handler.handleError("cache can only configured for http get calls");
            }
        }
    }

    private void websocketAndCsrf() {
        if (Checker.isTrue(endpoint.getWebsocket()) && Checker.notNull(endpoint.getCsrfEnabled())) {
            handler.handleError("websocket endpoints can not have csrf config");
        }
        if (Checker.isFalse(endpoint.getWebsocket()) && Checker.isNull(endpoint.getCsrfEnabled())) {
            handler.handleError("none-websocket endpoints must have csrf config");
        }
    }

    private void ifSecureThenPermissionIdMustExist() {
        if (endpoint.getSecured() && endpoint.getPermissionId() == null) {
            handler.handleError("secured endpoint must have role group id");
        }
    }

    private void websocketAndHttpMethod() {
        if (Boolean.FALSE.equals(endpoint.getWebsocket())) {
            if (endpoint.getMethod() == null || endpoint.getMethod().isBlank()) {
                handler.handleError("non websocket endpoints must have method");
            }
        }
    }


}
