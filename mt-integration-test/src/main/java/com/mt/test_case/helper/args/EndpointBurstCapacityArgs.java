package com.mt.test_case.helper.args;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class EndpointBurstCapacityArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        return Stream.of(
            Arguments.of(10, null, HttpStatus.BAD_REQUEST),
            Arguments.of(10, 0, HttpStatus.BAD_REQUEST),
            Arguments.of(10, Integer.MAX_VALUE, HttpStatus.BAD_REQUEST)
        );
    }
}
