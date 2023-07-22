package com.mt.helper.args;

import static com.mt.test_case.helper.AppConstant.MT_ACCESS_PERMISSION_ID;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class RolePermissionIdsArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        HashSet<String> strings = new HashSet<>();
        strings.add("0Y0000000000");
        strings.add("0Y0000000001");
        strings.add("0Y0000000002");
        strings.add("0Y0000000003");
        strings.add("0Y0000000004");
        strings.add("0Y0000000005");
        strings.add("0Y0000000006");
        strings.add("0Y0000000007");
        strings.add("0Y0000000008");
        strings.add("0Y0000000009");
        strings.add("0Y0000000010");
        return Stream.of(
            Arguments.of(null, HttpStatus.OK),
            Arguments.of(Collections.emptySet(), HttpStatus.OK),
            Arguments.of(Collections.singleton(" "), HttpStatus.BAD_REQUEST),
            Arguments.of(Collections.singleton(""), HttpStatus.BAD_REQUEST),
            Arguments.of(strings, HttpStatus.BAD_REQUEST),
            Arguments.of(Collections.singleton("123"), HttpStatus.BAD_REQUEST),
            Arguments.of(Collections.singleton(MT_ACCESS_PERMISSION_ID)
                , HttpStatus.BAD_REQUEST)
        );
    }
}
