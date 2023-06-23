package com.mt.test_case.helper.args;

import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientPathArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        String longPath = RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum() +
            RandomUtility.randomStringNoNum() + RandomUtility.randomStringNoNum();
        return Stream.of(
            Arguments.of(ClientUtility.createValidBackendClient(), null, HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(), "", HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(), longPath, HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(),
                RandomUtility.randomStringNoNum() + "-/-test", HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(), "  ", HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(), "<", HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(),
                RandomUtility.randomStringNoNum().substring(0, 4), HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(), "/test/",
                HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(),
                RandomUtility.randomStringNoNum() + "//test", HttpStatus.BAD_REQUEST),
            //type_is_front_but_path_present
            Arguments.of(ClientUtility.createValidFrontendClient(),
                RandomUtility.randomStringNoNum(), HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(),
                "012345678901234567890123456789012345678901234567890123456789" +
                    "012345678901234567890123456789012345678901234567890123456789"
                , HttpStatus.BAD_REQUEST)
        );
    }
}
