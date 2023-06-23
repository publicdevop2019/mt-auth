package com.mt.test_case.helper.args;

import com.mt.test_case.helper.pojo.Client;
import com.mt.test_case.helper.pojo.GrantType;
import com.mt.test_case.helper.utility.ClientUtility;
import com.mt.test_case.helper.utility.RandomUtility;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientRedirectUriExtArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        HashSet<String> urls = new HashSet<>();
        urls.add(RandomUtility.randomLocalHostUrl());
        urls.add(RandomUtility.randomLocalHostUrl());
        urls.add(RandomUtility.randomLocalHostUrl());
        urls.add(RandomUtility.randomLocalHostUrl());
        urls.add(RandomUtility.randomLocalHostUrl());
        urls.add(RandomUtility.randomLocalHostUrl());

        Client client = ClientUtility.createAuthorizationClientObj();
        Set<String> grantTypeEnums = client.getGrantTypeEnums();
        Set<String> registeredRedirectUri = client.getRegisteredRedirectUri();
        return Stream.of(
            Arguments.of(Collections.singleton(GrantType.CLIENT_CREDENTIALS.name()),
                registeredRedirectUri, HttpStatus.BAD_REQUEST),
            Arguments.of(grantTypeEnums,
                Collections.singleton(RandomUtility.randomStringNoNum()),
                HttpStatus.BAD_REQUEST),
            Arguments.of(grantTypeEnums, urls, HttpStatus.BAD_REQUEST)
        );
    }
}
