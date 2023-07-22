package com.mt.helper.args;

import com.mt.helper.AppConstant;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class CacheIdArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
            Arguments.of(null, HttpStatus.OK),
            Arguments.of("", HttpStatus.BAD_REQUEST),
            Arguments.of("  ", HttpStatus.BAD_REQUEST),
            Arguments.of("123", HttpStatus.BAD_REQUEST),
            Arguments.of(AppConstant.MT_ACCESS_CACHE_ID, HttpStatus.BAD_REQUEST),
            Arguments.of("0X8999999999"
                , HttpStatus.BAD_REQUEST)
        );
    }
}
