package com.mt.access.port.adapter.http;

import com.mt.access.application.client.ClientApplicationService;
import com.mt.access.application.endpoint.EndpointApplicationService;
import com.mt.access.application.user.UserApplicationService;
import com.mt.access.domain.model.AccessDeniedException;
import com.mt.access.domain.model.client.RedirectUrl;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.access.domain.model.operation_cool_down.OperationNotCoolDownException;
import com.mt.access.domain.model.notification.EmailNotificationException;
import com.mt.access.domain.model.image.FileUploadException;
import com.mt.common.domain.model.logging.ErrorMessage;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
        RedirectMismatchException.class,
        org.springframework.security.access.AccessDeniedException.class,
        ClientApplicationService.RootClientDeleteException.class,
        UserApplicationService.DefaultUserDeleteException.class,
        IllegalArgumentException.class,
        OAuth2Exception.class,
        EndpointApplicationService.InvalidClientIdException.class,
        RedirectUrl.InvalidRedirectUrlException.class,
        Origin.InvalidOriginValueException.class,
        DataIntegrityViolationException.class,
        OperationNotCoolDownException.class,
        IllegalStateException.class,
        FileUploadException.class,
    })
    protected ResponseEntity<Object> handle400Exception(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMessage(ex), new HttpHeaders(),
            HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {
        AccessDeniedException.class,
    })
    protected ResponseEntity<Object> handle403Exception(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMessage(ex), new HttpHeaders(),
            HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = {
        InvalidTokenException.class,
        InvalidClientException.class,
        InternalAuthenticationServiceException.class,
    })
    protected ResponseEntity<Object> handle401Exception(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMessage(ex), new HttpHeaders(),
            HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {
        EmailNotificationException.class,
    })
    protected ResponseEntity<Object> handle500Exception(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMessage(ex), new HttpHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * override springboot default error msg when params like changeId missing
     */
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
        ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMessage(new RuntimeException("missing param")), headers, status, request);
    }
}
