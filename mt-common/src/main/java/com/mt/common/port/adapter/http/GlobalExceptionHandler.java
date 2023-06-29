package com.mt.common.port.adapter.http;

import com.mt.common.CommonConstant;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.logging.ErrorMessage;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
        DefinedRuntimeException.class,
    })
    protected ResponseEntity<Object> handleDefinedException(DefinedRuntimeException ex,
                                                            WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CommonConstant.HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        httpHeaders.set(CommonConstant.HTTP_HEADER_ERROR_CODE, ex.getErrorCode());
        return handleExceptionInternal(ex, errorMessage, httpHeaders,
            ex.getResponseType().getHttpCode(),
            request);
    }

    @ExceptionHandler(value = {
        TransactionSystemException.class,
        IllegalArgumentException.class,
        HttpMessageConversionException.class,
        ObjectOptimisticLockingFailureException.class,
    })
    protected ResponseEntity<Object> handle400Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CommonConstant.HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.BAD_REQUEST,
            request);
    }

    @ExceptionHandler(value = {
        RuntimeException.class,
    })
    protected ResponseEntity<Object> handle500Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CommonConstant.HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders,
            HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    //@note duplicate key exception should result in 400
    @ExceptionHandler(value = {
        DataIntegrityViolationException.class,
    })
    protected ResponseEntity<Object> handle200Exception(RuntimeException ex, WebRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CommonConstant.HTTP_HEADER_SUPPRESS,
            CommonConstant.HTTP_HEADER_SUPPRESS_REASON_INTEGRITY_VIOLATION);
        return handleExceptionInternal(ex, null, httpHeaders, HttpStatus.OK, request);
    }
}
