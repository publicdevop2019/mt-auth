package com.mt.test_case.helper.args;

import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.utility.ClientUtility;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientResourceIndicatorArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        Client beClient = ClientUtility.createValidBackendClient();
        Client feClient = ClientUtility.createValidFrontendClient();
        return Stream.of(
            Arguments.of(beClient, null, HttpStatus.BAD_REQUEST),
            Arguments.of(feClient, true, HttpStatus.BAD_REQUEST)
        );
    }
}
