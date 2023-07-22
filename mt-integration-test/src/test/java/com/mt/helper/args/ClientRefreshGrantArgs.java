package com.mt.helper.args;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientRefreshGrantArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        return Stream.of(
            Arguments.of(0, HttpStatus.BAD_REQUEST),
            Arguments.of(null, HttpStatus.BAD_REQUEST)
        );
    }
}
