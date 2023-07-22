package com.mt.helper.args;

import com.mt.helper.AppConstant;
import com.mt.helper.utility.RandomUtility;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class UserRoleArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        List<String> mtAccessRoleId =
            List.of(AppConstant.MT_ACCESS_ROLE_ID, "0Z8HHJ489S00", "0Z8HHJ489S01",
                "0Z8HHJ489S02",
                "0Z8HHJ489S03", "0Z8HHJ489S04", "0Z8HHJ489S05", "0Z8HHJ489S06", "0Z8HHJ489S07",
                "0Z8HHJ489S08", "0Z8HHJ489S09", "0Z8HHJ489S10", "0Z8HHJ489S11");
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of(List.of(" "), HttpStatus.BAD_REQUEST),
            Arguments.of(List.of(""), HttpStatus.BAD_REQUEST),
            Arguments.of(Collections.emptyList(), HttpStatus.BAD_REQUEST),
            Arguments.of(mtAccessRoleId, HttpStatus.BAD_REQUEST),
            Arguments.of(List.of(RandomUtility.randomStringNoNum()), HttpStatus.BAD_REQUEST),
            Arguments.of(List.of(AppConstant.MT_ACCESS_ROLE_ID)
                , HttpStatus.BAD_REQUEST)
        );
    }
}
