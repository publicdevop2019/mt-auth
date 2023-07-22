package com.mt.helper.args;

import com.mt.helper.pojo.Client;
import com.mt.helper.pojo.GrantType;
import com.mt.helper.utility.ClientUtility;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientAutoApproveArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        Client client = ClientUtility.createAuthorizationClientObj();
        Set<String> registeredRedirectUri = client.getRegisteredRedirectUri();
        return Stream.of(
            //null
            Arguments.of(null, registeredRedirectUri, client.getGrantTypeEnums(),
                HttpStatus.BAD_REQUEST),
            //present when not authorization grant
            Arguments.of(true, registeredRedirectUri,
                Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()),
                HttpStatus.BAD_REQUEST
            ),
            //present when not authorization grant and redirect url missing
            Arguments.of(true, Collections.emptySet(),
                Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()),
                HttpStatus.BAD_REQUEST)
        );
    }
}
