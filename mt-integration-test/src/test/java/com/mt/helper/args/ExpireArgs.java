package com.mt.helper.args;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ExpireArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        return Stream.of(
            Arguments.of(null, HttpStatus.OK),
            Arguments.of((long) Integer.MAX_VALUE, HttpStatus.BAD_REQUEST),
            Arguments.of(0L, HttpStatus.BAD_REQUEST)
        );
    }
}
