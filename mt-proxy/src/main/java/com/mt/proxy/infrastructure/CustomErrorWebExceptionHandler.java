package com.mt.proxy.infrastructure;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class CustomErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    private static final Log logger;

    static {
        logger = HttpLogging.forLogName(CustomErrorWebExceptionHandler.class);
    }

    public CustomErrorWebExceptionHandler(GlobalErrorAttributes gea,
                                          ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(gea, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {
        final Map<String, Object>
            errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(errorPropertiesMap));
    }
    //modify based on abstract
    @Override
    protected void logError(ServerRequest request, ServerResponse response, Throwable throwable) {
        LogService.reactiveLog(request, () -> {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    request.exchange().getLogPrefix() + this.formatError(throwable, request));
            }
        });
        if (HttpStatus.resolve(response.rawStatusCode()) != null &&
            response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            LogService.reactiveLog(request, () -> {
                logger.error(LogMessage.of(() -> {
                    return String.format("%s 500 Server Error for %s",
                        request.exchange().getLogPrefix(), this.formatRequest(request));
                }), throwable);
            });
        }
    }
    //copy from abstract
    private String formatError(Throwable ex, ServerRequest request) {
        String reason = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        return "Resolved [" + reason + "] for HTTP " + request.methodName() + " " + request.path();
    }
    //copy from abstract
    private String formatRequest(ServerRequest request) {
        String rawQuery = request.uri().getRawQuery();
        String query = StringUtils.hasText(rawQuery) ? "?" + rawQuery : "";
        return "HTTP " + request.methodName() + " \"" + request.path() + query + "\"";
    }
}
