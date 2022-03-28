package com.mt.common.domain.model.validate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class Validator {
    private static final String NOT_NULL_MSG = "condition not match notNull";
    private static final String NOT_EMPTY_MSG = "condition not match notEmpty";
    private static final String NO_NULL_MEMBER_MSG = "condition not match noNullMember";
    private static final String NUM_GREATER_OR_EQUAL_TO_MSG =
        "condition not match greaterThanOrEqualTo";
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
        Pattern.compile("[a-zA-Z0-9 +\\-x/:()\\u4E00-\\u9FFF]*");
    private static final Pattern HTTP_URL = Pattern.compile(
        "^https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}"
            +
            "\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)");
    private static final Pattern HTTP_URL_LOCAL =
        Pattern.compile("^https?://localhost:"
            +
            "[0-9]{1,5}/([-a-zA-Z0-9()@:%_+.~#?&/=]*)");

    public static void notBlank(@Nullable String text, @Nullable String message) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message == null ? HAS_TEXT_MSG : message);
        }
    }

    public static void notBlank(@Nullable String text) {
        notBlank(text, null);
    }

    public static void notNull(@Nullable Object text, @Nullable String message) {
        if (text == null) {
            throw new IllegalArgumentException(message == null ? NOT_NULL_MSG : message);
        }
    }

    public static void notNull(@Nullable Object value) {
        notNull(value, null);
    }

    public static void lengthGreaterThanOrEqualTo(@Nullable String text, Integer min,
                                                  @Nullable String message) {
        notNull(text);
        int length = text.length();
        if (min > length) {
            throw new IllegalArgumentException(message == null ? GREATER_OR_EQUAL_TO_MSG : message);
        }
    }

    public static void lengthGreaterThanOrEqualTo(@Nullable String text, Integer min) {
        lengthGreaterThanOrEqualTo(text, min, null);
    }

    public static void lengthLessThanOrEqualTo(@Nullable String text, Integer max,
                                               @Nullable String message) {
        if (text != null) {
            int length = text.length();
            if (max < length) {
                throw new IllegalArgumentException(
                    message == null ? LESS_OR_EQUAL_TO_MSG : message);
            }
        }
    }

    public static void lengthLessThanOrEqualTo(@Nullable String text, Integer max) {
        lengthLessThanOrEqualTo(text, max, null);
    }

    public static void whitelistOnly(@Nullable String text) {
        whitelistOnly(text, null);
    }

    public static void whitelistOnly(@Nullable String text, @Nullable String message) {
        if (text != null) {
            Matcher matcher = TEXT_WHITE_LIST.matcher(text);
            if (!matcher.find()) {
                throw new IllegalArgumentException(message == null ? TEXT_WHITE_LIST_MSG : message);
            }
        }
    }

    public static void notEmpty(@Nullable Collection<?> objects) {
        notEmpty(objects, null);
    }

    public static void notEmpty(@Nullable Collection<?> objects, @Nullable String message) {
        notNull(objects);
        noNullMember(objects);
        if (objects.isEmpty()) {
            throw new IllegalArgumentException(message == null ? NOT_EMPTY_MSG : message);
        }

    }

    public static void noNullMember(@Nullable Collection<?> objects) {
        noNullMember(objects, null);
    }

    public static void noNullMember(@Nullable Collection<?> objects, @Nullable String message) {
        notNull(objects);
        if (objects.contains(null)) {
            throw new IllegalArgumentException(message == null ? NO_NULL_MEMBER_MSG : message);
        }

    }

    public static void greaterThanOrEqualTo(int value, int min) {
        greaterThanOrEqualTo(value, min, null);
    }

    public static void greaterThanOrEqualTo(int value, int min, @Nullable String message) {
        if (value < min) {
            throw new IllegalArgumentException(
                message == null ? NUM_GREATER_OR_EQUAL_TO_MSG : message);
        }
    }

    public static void greaterThanOrEqualTo(BigDecimal value, BigDecimal min) {
        greaterThanOrEqualTo(value, min, null);
    }

    public static void greaterThanOrEqualTo(BigDecimal value, BigDecimal min,
                                            @Nullable String message) {
        if (value.compareTo(min) < 0) {
            throw new IllegalArgumentException(
                message == null ? DEC_GREATER_OR_EQUAL_TO_MSG : message);
        }
    }

    public static void equalTo(int value, int target, @Nullable String message) {
        if (value != target) {
            throw new IllegalArgumentException(message == null ? NUM_EQUAL_TO_MSG : message);
        }
    }

    public static void equalTo(int value, int target) {
        equalTo(value, target, null);
    }

    public static void sizeEqualTo(Collection<?> value, Collection<?> target) {
        equalTo(value.size(), target.size(), null);
    }

    public static void sizeEqualTo(Collection<?> value, Collection<?> target,
                                   @Nullable String message) {
        equalTo(value.size(), target.size(), message);
    }

    public static void greaterThan(BigDecimal value, BigDecimal min) {
        greaterThan(value, min, null);
    }

    public static void greaterThan(BigDecimal value, BigDecimal min, @Nullable String message) {
        if (value.compareTo(min) <= 0) {
            throw new IllegalArgumentException(message == null ? DEC_GREATER_TO_MSG : message);
        }
    }

    public static void isEmail(String email) {
        isEmail(email, null);
    }

    public static void isEmail(String email, @Nullable String message) {
        notNull(email);
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(message == null ? EMAIL_MSG : message);
        }
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }


    public static void isHttpUrl(String imageUrlSmall) {
        isHttpUrl(imageUrlSmall, null);
    }

    public static void isHttpUrl(String imageUrlSmall, @Nullable String message) {
        notBlank(imageUrlSmall);
        Matcher matcher = HTTP_URL.matcher(imageUrlSmall);
        Matcher localMatcher = HTTP_URL_LOCAL.matcher(imageUrlSmall);
        if (!matcher.find() && !localMatcher.find()) {
            throw new IllegalArgumentException(message == null ? URL_MSG : message);
        }
    }
}
