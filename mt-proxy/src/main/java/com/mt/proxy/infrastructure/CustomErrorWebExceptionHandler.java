package com.mt.proxy.infrastructure;

import java.util.Map;
import java.util.Optional;
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
import org.springframework.web.server.ResponseStatusException;
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
        Optional<Object> first = request.exchange().getAttributes().values().stream()
            .filter(e -> e instanceof ResponseStatusException).findFirst();
        if (first.isPresent()) {
            LogService.reactiveLog(request, () -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("response status exception");
                }
            });
            ResponseStatusException ex = (ResponseStatusException) first.get();
            return ServerResponse.status(ex.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
        }
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
        if (HttpStatus.resolve(response.rawStatusCode()) != null) {
            LogService.reactiveLog(request, () -> {
                logger.error(LogMessage.of(() -> {
                    return String.format("%s Server Error for %s", response.statusCode().value(),
                        this.formatRequest(request));
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
