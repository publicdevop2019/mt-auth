package com.mt.access.port.adapter.persistence.client;

import com.mt.access.domain.model.client.RedirectUrl;
import javax.persistence.AttributeConverter;

public class RedirectUrlConverter
    implements AttributeConverter<RedirectUrl, String> {
    @Override
    public String convertToDatabaseColumn(RedirectUrl redirectUrls) {
        if (redirectUrls == null) {
            return null;
        }
        return redirectUrls.getValue();
    }

    @Override
    public RedirectUrl convertToEntityAttribute(String string) {
        if (string == null) {
            return null;
        }
        return new RedirectUrl(string);
    }
}
