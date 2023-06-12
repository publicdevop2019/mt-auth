package com.mt.common.domain.model.validate;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.StringUtils;

public class Validator {
    private static final String NOT_NULL_MSG = "condition not match notNull";
    private static final String IS_NULL_MSG = "condition not match isNull";
    private static final String VALUE_MISMATCH_MSG = "value not match";
    private static final String NOT_EMPTY_MSG = "condition not match notEmpty";
    private static final String NOT_MEMBER = "condition not match member of";
    private static final String NO_NULL_MEMBER_MSG = "condition not match noNullMember";
    private static final String DEC_GREATER_OR_EQUAL_TO_MSG =
        "condition not match decimal greaterThanOrEqualTo";
    private static final String DEC_GREATER_TO_MSG = "condition not match decimal greaterThan";
    private static final String EMAIL_MSG = "condition not match isValidEmail";
    private static final String NUM_EQUAL_TO_MSG = "condition not match int equals";
    private static final String URL_MSG = "condition not match isHttpUrl";
    private static final String HAS_TEXT_MSG = "condition not match hasText";
    private static final String GREATER_OR_EQUAL_TO_MSG =
        "condition not match lengthGreaterThanOrEqualTo";
    private static final String LESS_OR_EQUAL_TO_MSG =
        "condition not match lengthLessThanOrEqualTo";
    private static final String TEXT_WHITE_LIST_MSG = "condition not match whitelistOnly";
    private static final Pattern TEXT_WHITE_LIST =
        Pattern.compile("^[a-zA-Z0-9 +\\-x/:(),.!\\u4E00-\\u9FFF\\uff0c\\u3002]+$");
    private static final Pattern HTTP_URL = Pattern.compile(
        "^https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}"
            +
            "\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)");
    private static final Pattern HTTP_URL_LOCAL =
        Pattern.compile("^https?://localhost:"
            +
            "[0-9]{1,5}/([-a-zA-Z0-9()@:%_+.~#?&/=]*)");


    public static void notNull(Object obj) {
        if (Checker.isNull(obj)) {
            throw new DefinedRuntimeException(NOT_NULL_MSG, "0037",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void isNull(Object obj) {
        if (!Checker.isNull(obj)) {
            throw new DefinedRuntimeException(IS_NULL_MSG, "0059",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void isTrue(Boolean obj) {
        if (!Checker.isTrue(obj)) {
            throw new DefinedRuntimeException(VALUE_MISMATCH_MSG, "0061",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void notBlank(String text) {
        if (!StringUtils.hasText(text)) {
            throw new DefinedRuntimeException(HAS_TEXT_MSG, "0036",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void notEmpty(Collection<?> objects) {
        if (Checker.isEmpty(objects)) {
            throw new DefinedRuntimeException(NOT_EMPTY_MSG, "0041",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static <T extends Enum<T>> void memberOf(Collection<String> rawString,
                                                    Set<String> targetStrings) {
        rawString.forEach(e -> {
            if (!targetStrings.contains(e)) {
                throw new DefinedRuntimeException(NOT_MEMBER, "0058", HttpResponseCode.BAD_REQUEST);
            }
        });
    }

    public static void noNullMember(Collection<?> objects) {
        if (objects.contains(null)) {
            throw new DefinedRuntimeException(NO_NULL_MEMBER_MSG,
                "0042",
                HttpResponseCode.BAD_REQUEST);
        }
    }


    public static void greaterThan(BigDecimal value, BigDecimal min) {
        if (value.compareTo(min) <= 0) {
            throw new DefinedRuntimeException(DEC_GREATER_TO_MSG,
                "0046",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void greaterThanOrEqualTo(String text, Integer min) {
        int length = text.length();
        checkMinLength(min > length);
    }

    public static void greaterThanOrEqualTo(Collection<?> collection,
                                            Integer minCount) {
        int length = collection.size();
        checkMinLength(minCount > length);
    }

    public static void greaterThanOrEqualTo(Long along,
                                            Integer minCount) {
        checkMinLength(minCount > along);
    }

    public static void greaterThanOrEqualTo(Integer aInt,
                                            Integer minCount) {
        checkMinLength(minCount > aInt);
    }

    public static void greaterThanOrEqualTo(BigDecimal value, BigDecimal min) {
        if (value.compareTo(min) < 0) {
            throw new DefinedRuntimeException(
                DEC_GREATER_OR_EQUAL_TO_MSG, "0044",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void lessThanOrEqualTo(Collection<?> collection,
                                         Integer maxCount) {
        int length = collection.size();
        checkMaxLength(maxCount < length);
    }

    public static void lessThanOrEqualTo(String text, Integer max) {
        int length = text.length();
        checkMaxLength(max < length);
    }

    public static void lessThanOrEqualTo(Long aLong, Integer max) {
        checkMaxLength(max < aLong);
    }

    public static void lessThanOrEqualTo(Integer aInt, Integer max) {
        checkMaxLength(max < aInt);
    }

    public static void whitelistOnly(String text) {
        Matcher matcher = TEXT_WHITE_LIST.matcher(text);
        if (!matcher.find()) {
            throw new DefinedRuntimeException(TEXT_WHITE_LIST_MSG,
                "0040",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void equalTo(int value, int target) {
        if (value != target) {
            throw new DefinedRuntimeException(NUM_EQUAL_TO_MSG, "0045",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void sizeEqualTo(Collection<?> value, Collection<?> target) {
        equalTo(value.size(), target.size());
    }

    public static void isEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new DefinedRuntimeException(EMAIL_MSG, "0047",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static void isHttpUrl(String url) {
        Matcher matcher = HTTP_URL.matcher(url);
        Matcher localMatcher = HTTP_URL_LOCAL.matcher(url);
        if (!matcher.find() && !localMatcher.find()) {
            throw new DefinedRuntimeException(URL_MSG, "0048",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void validRequiredString(Integer minLength, Integer maxLength, String value) {
        Validator.notNull(value);
        Validator.notBlank(value);
        Validator.greaterThanOrEqualTo(value, minLength);
        Validator.lessThanOrEqualTo(value, maxLength);
        Validator.whitelistOnly(value);
    }

    public static void validOptionalString(Integer maxLength,
                                           String value) {
        if (value == null) {
            return;
        }
        validRequiredString(1, maxLength, value);
    }

    public static void validRequiredCollection(Integer minCount, Integer maxCount,
                                               Collection<?> collection) {
        Validator.notNull(collection);
        Validator.notEmpty(collection);
        Validator.greaterThanOrEqualTo(collection, minCount);
        Validator.lessThanOrEqualTo(collection, maxCount);

    }

    public static void validOptionalCollection(Integer minCount, Integer maxCount,
                                               Collection<?> collection) {
        if (collection == null) {
            return;
        }
        validRequiredCollection(minCount, maxCount, collection);
    }

    private static void checkMinLength(boolean failed) {
        if (failed) {
            throw new DefinedRuntimeException(
                GREATER_OR_EQUAL_TO_MSG,
                "0038",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    private static void checkMaxLength(boolean failed) {
        if (failed) {
            throw new DefinedRuntimeException(LESS_OR_EQUAL_TO_MSG,
                "0039",
                HttpResponseCode.BAD_REQUEST);
        }
    }
}
