package com.mt.test_case.helper.args;

import com.mt.test_case.helper.utility.RandomUtility;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class EndpointPathArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        String s = RandomUtility.randomHttpPath() +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij" +
            "abcdefghij";
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of("", HttpStatus.BAD_REQUEST),
            Arguments.of(s, HttpStatus.BAD_REQUEST),
            Arguments.of(RandomUtility.randomStringNoNum() + "-/-test", HttpStatus.BAD_REQUEST),
            Arguments.of("  ", HttpStatus.BAD_REQUEST),
            Arguments.of("<", HttpStatus.BAD_REQUEST),
            Arguments.of(RandomUtility.randomStringNoNum() + "//test", HttpStatus.BAD_REQUEST),
            Arguments.of("012345678901234567890123456789012345678901234567890123456789" +
                    "012345678901234567890123456789012345678901234567890123456789"
                , HttpStatus.BAD_REQUEST)
        );
    }
}
