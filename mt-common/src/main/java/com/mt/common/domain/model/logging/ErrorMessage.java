package com.mt.common.domain.model.logging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Data
@AllArgsConstructor
public class ErrorMessage {
    private List<String> errors;
    private String errorId;

    public ErrorMessage(RuntimeException ex) {
        errorId = UUID.randomUUID().toString();
        List<String> strings;
        if (NestedExceptionUtils.getMostSpecificCause(ex).getMessage() != null) {
            strings = List.of(NestedExceptionUtils.getMostSpecificCause(ex).getMessage().replace("\t", "").split("\n"));
            log.error("Handled exception UUID - {} - class - [{}] - Exception :", errorId, ex.getClass(), ex);
        } else {
            strings = List.of("Unable to get most specific cause, see log");
            log.error("Unhandled exception UUID - {} - class - [{}] - Exception :", errorId, ex.getClass(), ex);
        }
        errors = strings;
    }
}
