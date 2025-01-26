package com.mt.common.domain.model.validate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.validator.routines.EmailValidator;

public class Utility {
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
        return Utility.isNull(objects) || Utility.isEmpty(objects);
    }

    public static boolean notNullOrEmpty(Collection<?> objects) {
        return Utility.notNull(objects) && Utility.notEmpty(objects);
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
        return Utility.isNull(str) || "".equalsIgnoreCase(str.trim());
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

    public static <T, R> Set<R> mapToSet(@Nullable Set<T> resourceIds, Function<T, R> fn) {
        return resourceIds != null
            ? resourceIds.stream().map(fn)
            .collect(Collectors.toSet()) : Collections.emptySet();
    }

    public static <T, R> List<R> mapToList(@Nullable Set<T> resourceIds, Function<T, R> fn) {
        return resourceIds != null
            ? resourceIds.stream().map(fn)
            .collect(Collectors.toList()) : Collections.emptyList();
    }


    public static <T> void updateSet(@Nullable Set<T> old, @Nullable Set<T> updated,
                                     Consumer<Set<T>> addCallback,
                                     Consumer<Set<T>> removeCallback) {
        if (!sameAs(old, updated)) {
            if (updated == null) {
                removeCallback.accept(old);
                return;
            }
            if (old == null) {
                addCallback.accept(updated);
                return;
            }
            Set<T> added =
                updated.stream().filter(e -> !old.contains(e))
                    .collect(
                        Collectors.toSet());
            Set<T> removed =
                old.stream().filter(e -> !updated.contains(e))
                    .collect(
                        Collectors.toSet());
            if (!added.isEmpty()) {
                addCallback.accept(added);
            }
            if (!removed.isEmpty()) {
                removeCallback.accept(removed);
            }
        }
    }
}
