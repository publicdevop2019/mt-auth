package com.mt.helper.args;

import com.mt.helper.pojo.ClientType;
import com.mt.helper.utility.RandomUtility;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientTypeArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        HashSet<String> strings = new HashSet<>();
        strings.add(ClientType.BACKEND_APP.name());
        strings.add(ClientType.FRONTEND_APP.name());
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of(Collections.emptySet(), HttpStatus.BAD_REQUEST),
            Arguments.of(strings, HttpStatus.BAD_REQUEST),
            Arguments.of(Collections.singleton(RandomUtility.randomStringNoNum()), HttpStatus.BAD_REQUEST)
        );
    }
}
