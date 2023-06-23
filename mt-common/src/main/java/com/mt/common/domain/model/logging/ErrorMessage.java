package com.mt.common.domain.model.logging;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;

@Slf4j
@Data
@AllArgsConstructor
public class ErrorMessage {
    private List<String> errors;
    private String errorId;

    public ErrorMessage(RuntimeException ex) {
        errorId = UUID.randomUUID().toString();
        List<String> strings;
        if (ex instanceof DefinedRuntimeException) {
            strings = List.of(
                NestedExceptionUtils.getMostSpecificCause(ex).getMessage().replace("\t", "")
                    .split("\n"));
            log.warn("defined exception UUID - {} - {} - exception: {} - details: ", errorId,
                ex.getClass(), strings.get(0), ex);
        } else if (NestedExceptionUtils.getMostSpecificCause(ex).getMessage() != null) {
            strings = List.of(
                NestedExceptionUtils.getMostSpecificCause(ex).getMessage().replace("\t", "")
                    .split("\n"));
            log.error("known cause exception UUID - {} - {} - exception: ", errorId,
                ex.getClass(), ex);
        } else {
            strings = List.of("Unable to get most specific cause, see log");
            log.error("unknown cause exception UUID - {} - {} - exception: ", errorId,
                ex.getClass(), ex);
        }
        errors = strings;
    }
}
