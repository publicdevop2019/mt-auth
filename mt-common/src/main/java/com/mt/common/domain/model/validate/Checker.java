package com.mt.common.domain.model.validate;

import java.util.Collection;

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
}
