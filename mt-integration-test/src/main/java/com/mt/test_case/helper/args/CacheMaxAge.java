package com.mt.test_case.helper.args;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class CacheMaxAge implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        return Stream.of(
            //null
            Arguments.of(null, HttpStatus.OK),
            Arguments.of((long) Integer.MAX_VALUE, HttpStatus.BAD_REQUEST),
            Arguments.of(0L, HttpStatus.BAD_REQUEST)
        );
    }
}
