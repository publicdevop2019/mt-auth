package com.mt.helper.args;


import static com.mt.helper.AppConstant.MT_ACCESS_ENDPOINT_ID;

import com.mt.helper.AppConstant;
import com.mt.helper.utility.RandomUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpStatus;

public class LinkedApiIdArgs implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        List<String> strings = new ArrayList<>();
        strings.add("0E8AZTODP401");
        strings.add("0E8AZTODP402");
        strings.add("0E8AZTODP403");
        strings.add("0E8AZTODP404");
        strings.add("0E8AZTODP405");
        List<String> strings2 = new ArrayList<>();
        strings2.add("0E8AZTODP400");
        strings2.add("0E8AZTODP401");
        strings2.add("0E8AZTODP402");
        strings2.add("0E8AZTODP403");
        strings2.add("0E8AZTODP404");
        strings2.add("0E8AZTODP405");
        strings2.add("0E8AZTODP406");
        strings2.add("0E8AZTODP407");
        strings2.add("0E8AZTODP408");
        strings2.add("0E8AZTODP409");
        strings2.add("0E8AZTODP410");
        strings2.add("0E8AZTODP411");
        strings2.add("0E8AZTODP412");
        strings2.add("0E8AZTODP413");
        strings2.add("0E8AZTODP414");
        strings2.add("0E8AZTODP415");
        strings2.add("0E8AZTODP416");
        strings2.add("0E8AZTODP417");
        strings2.add("0E8AZTODP418");
        strings2.add("0E8AZTODP419");
        strings2.add("0E8AZTODP420");
        return Stream.of(
            Arguments.of(null, HttpStatus.OK),
            Arguments.of(List.of(" "), HttpStatus.BAD_REQUEST),
            Arguments.of(List.of(""), HttpStatus.BAD_REQUEST),
            Arguments.of(Collections.emptyList(), HttpStatus.OK),
            Arguments.of(strings, HttpStatus.BAD_REQUEST),
            Arguments.of(List.of(RandomUtility.randomStringNoNum()), HttpStatus.BAD_REQUEST),
            Arguments.of(List.of(MT_ACCESS_ENDPOINT_ID), HttpStatus.BAD_REQUEST),
            Arguments.of(List.of("0E99999999"), HttpStatus.BAD_REQUEST),
            Arguments.of(strings2, HttpStatus.BAD_REQUEST),
            Arguments.of(List.of(AppConstant.MT_ACCESS_ROLE_ID)
                , HttpStatus.BAD_REQUEST)
        );
    }
}
