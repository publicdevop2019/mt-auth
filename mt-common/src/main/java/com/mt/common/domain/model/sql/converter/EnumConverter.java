package com.mt.common.domain.model.sql.converter;

import javax.persistence.AttributeConverter;

public class EnumConverter<E extends Enum<E>> implements AttributeConverter<E, String> {
    private final Class<E> type;

    public EnumConverter(Class<E> type) {
        this.type = type;
    }

    @Override
    public String convertToDatabaseColumn(E orderState) {
        if (orderState != null) {
            return orderState.name();
        }
        return null;
    }

    @Override
    public E convertToEntityAttribute(String s) {
        if (s != null) {
            return E.valueOf(type, s);
        }
        return null;
    }
}
