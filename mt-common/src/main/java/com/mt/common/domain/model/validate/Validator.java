package com.mt.common.domain.model.validate;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.util.StringUtils;

public class Validator {
    private static final String NOT_NULL_MSG = "condition not match notNull";
    private static final String IS_NULL_MSG = "condition not match isNull";
    private static final String IS_EMPTY_MSG = "condition not match isEmpty";
    private static final String VALUE_MISMATCH_MSG = "value not match";
    private static final String NOT_EMPTY_MSG = "condition not match notEmpty";
    private static final String NOT_MEMBER = "condition not match member of";
    private static final String NO_NULL_MEMBER_MSG = "condition not match noNullMember";
    private static final String DEC_GREATER_OR_EQUAL_TO_MSG =
        "condition not match decimal greaterThanOrEqualTo";
    private static final String DEC_GREATER_TO_MSG = "condition not match decimal greaterThan";
    private static final String EMAIL_MSG = "condition not match isValidEmail";
    private static final String NUM_EQUAL_TO_MSG = "condition not match equals";
    private static final String URL_MSG = "condition not match isHttpUrl";
    private static final String HAS_TEXT_MSG = "condition not match hasText";
    private static final String GREATER_OR_EQUAL_TO_MSG =
        "condition not match lengthGreaterThanOrEqualTo";
    private static final String LESS_OR_EQUAL_TO_MSG =
        "condition not match lengthLessThanOrEqualTo";
    private static final String TEXT_WHITE_LIST_MSG = "condition not match whitelistOnly";
    private static final Pattern TEXT_WHITE_LIST =
        Pattern.compile("^[a-zA-Z0-9_ +\\-x/:(),.!\\u4E00-\\u9FFF\\uff0c\\u3002]+$");
    private static final UrlValidator httpValidator =
        new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);


    public static void notNull(Object obj) {
        if (Utility.isNull(obj)) {
            throw new DefinedRuntimeException(NOT_NULL_MSG, "0037",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void isNull(Object obj) {
        if (!Utility.isNull(obj)) {
            throw new DefinedRuntimeException(IS_NULL_MSG, "0059",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void isEmpty(Collection<?> obj) {
        if (!Utility.isEmpty(obj)) {
            throw new DefinedRuntimeException(IS_EMPTY_MSG, "0062",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void isTrue(Boolean obj) {
        if (!Utility.isTrue(obj)) {
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
        if (Utility.isEmpty(objects)) {
            throw new DefinedRuntimeException(NOT_EMPTY_MSG, "0041",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void memberOf(String value, Set<String> assertions) {
        if (!assertions.contains(value)) {
            throw new DefinedRuntimeException(NOT_MEMBER, "0058", HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void memberOf(Collection<String> value,
                                Set<String> assertions) {
        value.forEach(e -> memberOf(e, assertions));
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

    public static void equals(@Nullable Object value, @Nullable Object target) {
        if (!Objects.equals(value, target)) {
            throw new DefinedRuntimeException(NUM_EQUAL_TO_MSG, "0045",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static void sizeEquals(Collection<?> value, Collection<?> target) {
        equals(value.size(), target.size());
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
        if (!httpValidator.isValid(url)) {
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
        Validator.noNullMember(collection);
        Validator.greaterThanOrEqualTo(collection, minCount);
        Validator.lessThanOrEqualTo(collection, maxCount);

    }

    public static void validOptionalCollection(Integer maxCount,
                                               Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return;
        }
        Validator.noNullMember(collection);
        Validator.lessThanOrEqualTo(collection, maxCount);
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
