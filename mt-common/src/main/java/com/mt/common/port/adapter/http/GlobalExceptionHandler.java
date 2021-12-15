package com.mt.common.port.adapter.http;

import com.mt.common.CommonConstant;
import com.mt.common.application.idempotent.exception.ChangeNotFoundException;
import com.mt.common.application.idempotent.exception.RollbackNotSupportedException;
import com.mt.common.domain.model.jwt.IllegalJwtException;
import com.mt.common.domain.model.jwt.JwtTokenExtractException;
import com.mt.common.domain.model.jwt.JwtTokenRetrievalException;
import com.mt.common.domain.model.logging.ErrorMessage;
import com.mt.common.domain.model.restful.exception.*;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.builder.UpdateQueryBuilder;
import com.mt.common.domain.model.sql.exception.UnsupportedQueryException;
import com.mt.common.infrastructure.JacksonObjectSerializer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            TransactionSystemException.class,
            IllegalArgumentException.class,
            ObjectOptimisticLockingFailureException.class,
            JwtTokenExtractException.class,
            UnsupportedQueryException.class,
            UnsupportedPatchOperationException.class,
            UpdateFiledValueException.class,
            RollbackNotSupportedException.class,
            UpdateQueryBuilder.PatchCommandExpectNotMatchException.class,
            AggregateNotExistException.class,
            JacksonObjectSerializer.UnableToJsonPatchException.class,
            ChangeNotFoundException.class,
            AggregateOutdatedException.class,
            IllegalJwtException.class,
            QueryUtility.QueryParseException.class,
            PageConfig.PagingParseException.class,
            QueryUtility.UnknownQueryValueException.class,
            NoUpdatableFieldException.class
    })
    protected ResponseEntity<Object> handle400Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CommonConstant.HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {
            RuntimeException.class,
            JwtTokenRetrievalException.class,
            JacksonObjectSerializer.UnableToDeepCopyCollectionException.class,
            JacksonObjectSerializer.UnableToDeserializeCollectionException.class,
            JacksonObjectSerializer.UnableToSerializeCollectionException.class,
            JacksonObjectSerializer.UnableToDeSerializeException.class,
            JacksonObjectSerializer.UnableToSerializeException.class,
    })
    protected ResponseEntity<Object> handle500Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CommonConstant.HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    //@note duplicate key exception should result in 400
    @ExceptionHandler(value = {
            DataIntegrityViolationException.class,
    })
    protected ResponseEntity<Object> handle200Exception(RuntimeException ex, WebRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CommonConstant.HTTP_HEADER_SUPPRESS, CommonConstant.HTTP_HEADER_SUPPRESS_REASON_INTEGRITY_VIOLATION);
        return handleExceptionInternal(ex, null, httpHeaders, HttpStatus.OK, request);
    }
}
