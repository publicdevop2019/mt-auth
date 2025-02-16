package com.mt.common.domain.model.validate;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.commons.validator.routines.EmailValidator;

public class Checker {
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isTrue(Boolean obj) {
        return Boolean.TRUE.equals(obj);
    }

    public static boolean isFalse(Boolean obj) {
        return Boolean.FALSE.equals(obj);
    }

    public static boolean notNull(Object obj) {
        return obj != null;
    }

    public static boolean isEmpty(Collection<?> objects) {
        return objects.isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> objects) {
        return Checker.isNull(objects) || Checker.isEmpty(objects);
    }

    public static boolean notNullOrEmpty(Collection<?> objects) {
        return Checker.notNull(objects) && Checker.notEmpty(objects);
    }

    public static boolean notEmpty(Collection<?> objects) {
        return !objects.isEmpty();
    }

    public static boolean sizeEquals(Collection<?> a, Collection<?> b) {
        return a.size() == b.size();
    }

    public static boolean sizeNotEquals(Collection<?> a, Collection<?> b) {
        return !sizeEquals(a, b);
    }

    public static boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    public static boolean isEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isBlank(@Nullable String str) {
        return Checker.isNull(str) || "".equalsIgnoreCase(str.trim());
    }

    public static boolean notBlank(@Nullable String str) {
        return !isBlank(str);
    }

    /**
     * check if two collections are same, null and empty collection will be treated as same
     *
     * @param a   a collection
     * @param b   b collection
     * @param <T> type of collection
     * @return same or not
     */
    public static <T> boolean sameAs(@Nullable Collection<T> a, @Nullable Collection<T> b
    ) {
        if (a == null && b == null) {
            return true;
        } else if (a != null && b == null) {
            return a.isEmpty();
        } else if (a == null) {
            return b.isEmpty();
        } else {
            return a.equals(b);
        }
    }


}
