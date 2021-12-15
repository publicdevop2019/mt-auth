package com.mt.messenger.port.adapter.http;

import com.mt.messenger.domain.model.email_delivery.CoolDownException;
import com.mt.messenger.port.adapter.email.GmailDeliveryException;
import com.mt.messenger.application.email_delivery.NoAdminFoundException;
import com.mt.messenger.application.email_delivery.UnknownBizTypeException;
import com.mt.common.domain.model.logging.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            CoolDownException.class,
            UnknownBizTypeException.class,
            NoAdminFoundException.class,
            SQLIntegrityConstraintViolationException.class
    })
    protected ResponseEntity<?> handle400Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Error-Id", errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {
            GmailDeliveryException.class,
    })
    protected ResponseEntity<?> handle500Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Error-Id", errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
