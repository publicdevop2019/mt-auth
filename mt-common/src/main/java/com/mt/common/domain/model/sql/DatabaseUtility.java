package com.mt.common.domain.model.sql;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DatabaseUtility {
    public static void checkUpdate(Integer rowCount) {
        if (!Checker.equals(rowCount, 1)) {
            throw new DefinedRuntimeException("db update failed, expected 1 but got " + rowCount,
                "0064", HttpResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    public static Boolean getNullableBoolean(ResultSet rs, String fieldName) throws SQLException {
        boolean aBoolean = rs.getBoolean(fieldName);
        if (rs.wasNull()) {
            return null;
        }
        return aBoolean;
    }

    public static Long getNullableLong(ResultSet rs, String fieldName) throws SQLException {
        long aLong = rs.getLong(fieldName);
        if (rs.wasNull()) {
            return null;
        }
        return aLong;
    }

    public static Integer getNullableInteger(ResultSet rs, String fieldName) throws SQLException {
        int anInt = rs.getInt(fieldName);
        if (rs.wasNull()) {
            return null;
        }
        return anInt;
    }

    public static <T> void updateMap(Collection<T> old, Collection<T> updated,
                                     Consumer<Collection<T>> addCallback,
                                     Consumer<Collection<T>> removeCallback) {
        if (!Objects.equals(old, updated)) {
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

    public static String getInClause(int size) {
        return String.join(",", Collections.nCopies(size, "?"));
    }

    public static class ExtractCount implements ResultSetExtractor<Long> {
        @Override
        public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) {
                return 0L;
            } else {
                return rs.getLong("count");
            }
        }
    }
}
