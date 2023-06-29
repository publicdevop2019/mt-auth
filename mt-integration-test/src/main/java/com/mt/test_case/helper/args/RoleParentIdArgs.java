package com.mt.test_case.helper.args;

import com.mt.test_case.helper.AppConstant;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class RoleParentIdArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
            Arguments.of(null, HttpStatus.OK),
            Arguments.of("", HttpStatus.BAD_REQUEST),
            Arguments.of("  ", HttpStatus.BAD_REQUEST),
            Arguments.of("123", HttpStatus.BAD_REQUEST),
            Arguments.of(AppConstant.MT_ACCESS_ROLE_ID, HttpStatus.BAD_REQUEST),
            Arguments.of("0R99999999"
                , HttpStatus.BAD_REQUEST)
        );
    }
}
