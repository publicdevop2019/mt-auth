package com.mt.common.domain.model.sql.converter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * use LinkedHashSet to keep order of elements
 */
public class StringSetConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> strings) {
        if (ObjectUtils.isEmpty(strings)) {
            return null;
        }
        return String.join(",", strings);
    }

    @Override
    public Set<String> convertToEntityAttribute(String s) {
        if (StringUtils.hasText(s)) {
            return Arrays.stream(s.split(",")).collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return new LinkedHashSet<>();
    }
}
