package com.mt.helper.args;

import com.mt.helper.pojo.CacheControlValue;
import com.mt.helper.utility.RandomUtility;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class CacheControlArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        Set<String> collect =
            Arrays.stream(CacheControlValue.values()).map(e -> e.label).collect(Collectors.toSet());
        collect.add(CacheControlValue.NO_CACHE.label);
        return Stream.of(
            Arguments.of(true, null, HttpStatus.OK),
            Arguments.of(true, Collections.emptySet(), HttpStatus.OK),
            Arguments.of(true, Collections.singleton(RandomUtility.randomStringNoNum()),
                HttpStatus.BAD_REQUEST),
            Arguments.of(true, collect, HttpStatus.OK),
            Arguments.of(false, collect, HttpStatus.BAD_REQUEST)//no cache
        );
    }
}
