package com.mt.test_case.helper.args;

import com.mt.test_case.helper.pojo.GrantType;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientRefreshTokenSecondArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        Set<String> grantTypes = new HashSet<>();
        grantTypes.add(GrantType.REFRESH_TOKEN.name());
        grantTypes.add(GrantType.PASSWORD.name());
        Set<String> grantTypes2 = new HashSet<>();
        grantTypes2.add(GrantType.REFRESH_TOKEN.name());

        return Stream.of(
            Arguments.of(grantTypes, 1, HttpStatus.BAD_REQUEST),
            Arguments.of(grantTypes, Integer.MAX_VALUE, HttpStatus.BAD_REQUEST),
            Arguments.of(grantTypes2, 120, HttpStatus.BAD_REQUEST)//has value but not password grant
        );
    }
}
