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

}
