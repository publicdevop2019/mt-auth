package com.mt.helper.args;

import com.mt.helper.utility.ClientUtility;
import com.mt.helper.utility.RandomUtility;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientExternalUrlArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        return Stream.of(
            Arguments.of(ClientUtility.createValidBackendClient(),
                RandomUtility.randomStringNoNum(), HttpStatus.BAD_REQUEST),
            //type is frontend but externalUrl is present
            Arguments.of(ClientUtility.createValidFrontendClient(),
                RandomUtility.randomLocalHostUrl(), HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(),
                RandomUtility.randomStringNoNum() +
                    "/abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij",
                HttpStatus.BAD_REQUEST),
            Arguments.of(ClientUtility.createValidBackendClient(), null
                , HttpStatus.BAD_REQUEST)
        );
    }
}
