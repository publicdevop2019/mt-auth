package com.mt.test_case.helper.args;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientNameArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of("", HttpStatus.BAD_REQUEST),
            Arguments.of("  ", HttpStatus.BAD_REQUEST),
            Arguments.of("<", HttpStatus.BAD_REQUEST),
            Arguments.of("1", HttpStatus.BAD_REQUEST),
            Arguments.of("012345678901234567890123456789012345678901234567890123456789"
                , HttpStatus.BAD_REQUEST)
        );
    }
}
