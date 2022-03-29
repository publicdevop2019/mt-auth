package com.mt.common.domain.model.sql.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class EnumSetConverter<T extends Enum<T>> implements AttributeConverter<Set<T>, String> {

    private final Class<T> type;

    public EnumSetConverter(Class<T> type) {
        this.type = type;
    }

    @Override
    public String convertToDatabaseColumn(Set<T> ts) {
        if (ObjectUtils.isEmpty(ts)) {
            return null;
        }
        return String.join(",", ts.stream().map(Enum::toString).collect(Collectors.toSet()));
    }

    @Override
    public Set<T> convertToEntityAttribute(String s) {
        if (StringUtils.hasText(s)) {
            return Arrays.stream(s.split(",")).map(e -> T.valueOf(type, e))
                .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}