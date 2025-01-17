package com.mt.helper.args;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class RevokeTokenArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
            Arguments.of(""),
            Arguments.of("  "),
            Arguments.of("0Uabc"),
            Arguments.of("0Cdef"),
            Arguments.of("xxx"),
            Arguments.of("012345678901234567890123456789012345678901234567890123456789")
        );
    }
}
