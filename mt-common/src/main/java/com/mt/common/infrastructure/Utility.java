package com.mt.common.infrastructure;

import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.validate.Checker;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utility {

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

    public static <T, R> Set<R> mapToSet(@Nullable Set<T> resourceIds, Function<T, R> fn) {
        return resourceIds != null
            ? resourceIds.stream().map(fn)
            .collect(Collectors.toCollection(LinkedHashSet::new)) : Collections.emptySet();
    }

    public static <T, R> List<R> mapToList(@Nullable Set<T> resourceIds, Function<T, R> fn) {
        return resourceIds != null
            ? resourceIds.stream().map(fn)
            .collect(Collectors.toList()) : Collections.emptyList();
    }

    public static <T> void updateSet(@Nullable Set<T> oldSet, @Nullable Set<T> newSet,
                                     Consumer<Set<T>> addCallback,
                                     Consumer<Set<T>> removeCallback) {
        if (!Checker.sameAs(oldSet, newSet)) {
            if (newSet == null) {
                removeCallback.accept(oldSet);
                return;
            }
            if (oldSet == null) {
                addCallback.accept(newSet);
                return;
            }
            Set<T> added =
                newSet.stream().filter(e -> !oldSet.contains(e))
                    .collect(
                        Collectors.toSet());
            Set<T> removed =
                oldSet.stream().filter(e -> !newSet.contains(e))
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

    public static String getClientIpAddress(HttpServletRequest request) {
        log.trace("--start of get client ip address");
        request.getHeaderNames().asIterator().forEachRemaining(e -> {
            log.trace("header name [{}] and value: {}", e, request.getHeader(e));
        });
        String ip = request.getHeader(AppInfo.X_FORWARDED_FOR);
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            //get first ip as real ip
            return ip.split(",")[0].trim();
        }
        log.trace("--end of get client ip address");
        return request.getRemoteAddr();
    }
}
