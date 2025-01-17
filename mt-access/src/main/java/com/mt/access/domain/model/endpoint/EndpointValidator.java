package com.mt.access.domain.model.endpoint;


import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;

public class EndpointValidator {
    private final Endpoint endpoint;
    private final ValidationNotificationHandler handler;

    public EndpointValidator(Endpoint client, ValidationNotificationHandler handler) {
        this.endpoint = client;
        this.handler = handler;
    }

    protected void validate() {
        checkNotNullValue();
        websocketAndHttpMethod();
        websocketAndCsrf();
        ifSecureThenPermissionIdMustExist();
        onlyGetCanHaveCacheConfig();
        replenishRateAndBurstCapacity();
        publicEndpointCannotHaveCsrf();
        checkEndpointConfig();
    }

    private void publicEndpointCannotHaveCsrf() {
        if (Checker.isFalse(this.endpoint.getSecured()) &&
            Checker.isTrue(this.endpoint.getExternal()) &&
            Checker.isTrue(this.endpoint.getCsrfEnabled())
        ) {
            handler.handleError("public endpoint can not have csrf enabled");
        }
    }

    private void checkEndpointConfig() {
        if (Checker.isTrue(this.endpoint.getShared())
        ) {
            //shared endpoint
            if (Checker.isTrue(this.endpoint.getExternal()) &&
                Checker.isTrue(this.endpoint.getSecured())) {
                return;
            }
        } else {
            if (Checker.isTrue(this.endpoint.getExternal())) {
                if (Checker.isTrue(this.endpoint.getSecured())) {
                    //protected endpoint
                    return;
                } else {
                    //public endpoint
                    return;
                }
            } else {
                if (Checker.isFalse(this.endpoint.getSecured())) {
                    //private endpoint
                    return;
                }
            }
        }
        handler.handleError("invalid endpoint config");
    }

    private void checkNotNullValue() {
        Validator.notNull(endpoint.getEndpointId());
        Validator.notNull(endpoint.getProjectId());
        Validator.notNull(endpoint.getName());
        Validator.notNull(endpoint.getClientId());
        Validator.notNull(endpoint.getShared());
        Validator.notNull(endpoint.getExternal());
        Validator.notNull(endpoint.getSecured());
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
        if (Checker.isTrue(endpoint.getWebsocket())) {
            if (endpoint.getMethod() == null || !endpoint.getMethod().equals("GET")) {
                handler.handleError("websocket endpoints must have GET method");
            }
        }
    }


}
