package com.mt.common.infrastructure;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class CommonUtility {

    /**
     * map raw string to domain id collections, return null if input is null
     *
     * @param rawIds raw ids
     * @param fn     id's constructor
     * @param <T>    type of converted set
     * @return converted set
     */
    public static <T> Set<T> map(Set<String> rawIds, Function<String, T> fn) {
        return rawIds == null ? null :
            rawIds.stream().map(fn).collect(
                Collectors.toSet());
    }
    //@todo find better fix
    /**
     * update DB collection without trigger unnecessary sql update
     * <p>
     * hibernate by default create empty set instead null
     * <p>
     * when we pass null trying to set value
     * <p>
     * it will create unnecessary update to DB
     * <p>
     * below logic is added to avoid this update
     *
     * @param source source collection, if null
     * @param updateTo target collection
     * @param ifSourceNull invoke if source is null
     * @param <T>    type of collection
     */
    public static <T> void updateCollection(@Nullable Collection<T> source,
                                            @Nullable Collection<T> updateTo,
                                            Runnable ifSourceNull
    ) {
        if (source == null) {
            if (updateTo == null) {
                return;
            } else {
                if (updateTo.isEmpty()) {
                    return;
                } else {
                    ifSourceNull.run();
                }
            }
        } else {
            //source != null
            if (source.isEmpty()) {
                if (updateTo == null) {
                    return;
                } else {
                    if (updateTo.isEmpty()) {
                        return;
                    } else {
                        if (!updateTo.equals(source)) {
                            source.addAll(updateTo);
                        } else {
                            return;
                        }
                    }
                }
            } else {
                //source != empty
                if (updateTo == null) {
                    source.clear();
                } else {
                    if (updateTo.isEmpty()) {
                        source.clear();
                    } else {
                        if (!updateTo.equals(source)) {
                            source.clear();
                            source.addAll(updateTo);
                        } else {
                            return;
                        }
                    }
                }

            }
        }
    }
    /**
     * check if update DB collection required
     *
     * @param source source collection
     * @param updateTo target collection
     * @param <T>    type of collection
     * @return if update required
     */
    public static <T> boolean collectionWillChange(@Nullable Collection<T> source,
                                                   @Nullable Collection<T> updateTo
    ) {
        if (source == null) {
            if (updateTo == null) {
                return false;
            } else {
                if (updateTo.isEmpty()) {
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            //source != null
            if (source.isEmpty()) {
                if (updateTo == null) {
                    return false;
                } else {
                    if (updateTo.isEmpty()) {
                        return false;
                    } else {
                        if (!updateTo.equals(source)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                //source != empty
                if (updateTo == null) {
                    return true;
                } else {
                    if (updateTo.isEmpty()) {
                        return true;
                    } else {
                        if (!updateTo.equals(source)) {
                           return true;
                        } else {
                            return false;
                        }
                    }
                }

            }
        }
    }
}
