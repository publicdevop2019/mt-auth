package com.mt.test_case.helper.args;

import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientRedirectUriArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of(Collections.emptySet(), HttpStatus.BAD_REQUEST)
        );
    }
}
