package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeaderNameValidator {
    private static final Pattern HEADER_NAME_REGEX = Pattern.compile("^[a-zA-Z-]+$");

    public static void validateHeaderName(Set<String> headerNames) {
        headerNames.forEach(header -> {
            boolean pass = false;
            Matcher matcher = HEADER_NAME_REGEX.matcher(header);
            if (matcher.find()) {
                if (!header.startsWith("-") && !header.endsWith("-") &&
                    !header.equalsIgnoreCase("-")) {
                    pass = true;
                }
            }
            if (!pass) {
                throw new DefinedRuntimeException("invalid header format", "1085",
                    HttpResponseCode.BAD_REQUEST);
            }
        });
    }
}
