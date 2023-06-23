package com.mt.test_case.helper.args;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class EndpointReplenishRateArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        return Stream.of(
            Arguments.of(null, 10, HttpStatus.BAD_REQUEST),
            Arguments.of(0, 10, HttpStatus.BAD_REQUEST),
            Arguments.of(1001, 1500, HttpStatus.BAD_REQUEST),
            Arguments.of(100, 50, HttpStatus.BAD_REQUEST)
        );
    }
}
