package com.mt.test_case.helper.args;

import com.mt.test_case.helper.AppConstant;
import com.mt.test_case.helper.utility.RandomUtility;
import java.util.HashSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class ClientResourceIdsArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
        throws Exception {
        //too many elements
        HashSet<String> strings = new HashSet<>();
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        strings.add(RandomUtility.randomStringNoNum());
        HashSet<String> strings2 = new HashSet<>();
        strings2.add(RandomUtility.randomStringNoNum());
        HashSet<String> strings3 = new HashSet<>();
        strings3.add(AppConstant.CLIENT_ID_TEST_ID);
        return Stream.of(
            Arguments.of(strings, HttpStatus.BAD_REQUEST),//too many element
            Arguments.of(strings2, HttpStatus.BAD_REQUEST),//invalid format
            Arguments.of(strings3, HttpStatus.BAD_REQUEST)//other tenant's id
        );
    }
}
