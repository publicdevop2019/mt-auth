package com.mt.access.domain.model.cors_profile;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.io.IOException;
import java.io.Serializable;
import javax.persistence.AttributeConverter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.validator.routines.UrlValidator;

@NoArgsConstructor
@EqualsAndHashCode
public class Origin implements Serializable {
    private static final UrlValidator defaultValidator =
        new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    private static final String ALL = "*";
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String value;

    public Origin(String url) {
        if (defaultValidator.isValid(url) || ALL.equalsIgnoreCase(url)) {
            value = url;
        } else {
            throw new DefinedRuntimeException("invalid origin value", "1039",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public static class OriginConverter implements AttributeConverter<Origin, String> {
        @Override
        public String convertToDatabaseColumn(Origin origin) {
            if (origin == null) {
                return null;
            }
            return origin.value;
        }

        @Override
        public Origin convertToEntityAttribute(String origin) {
            if (origin == null || origin.isBlank()) {
                return null;
            }
            return new Origin(origin);
        }
    }
}
