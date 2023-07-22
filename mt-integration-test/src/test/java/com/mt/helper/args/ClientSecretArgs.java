package com.mt.helper.args;

import com.mt.helper.pojo.Client;
import com.mt.helper.utility.ClientUtility;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientSecretArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        Client beClient = ClientUtility.createValidBackendClient();
        Client feClient = ClientUtility.createValidFrontendClient();
        return Stream.of(
            //type is backend and secret is missing
            Arguments.of(beClient, null, HttpStatus.BAD_REQUEST),
            //type is frontend but secret is present
            Arguments.of(feClient, "test", HttpStatus.OK),
            //secret format
            Arguments.of(beClient, "0123456789012345678901234567890123456789", HttpStatus.OK)
        );
    }
}
