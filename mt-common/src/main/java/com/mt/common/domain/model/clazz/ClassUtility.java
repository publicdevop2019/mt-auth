package com.mt.common.domain.model.clazz;

public class ClassUtility {
    public static <T> String getShortName(Class<T> clazz) {
        return getShortName(clazz.getName());
    }

    public static <T> String getShortName(String stringClassName) {
        String[] split = stringClassName.split("\\.");
        return split[split.length - 1];
    }
}
