package com.mt.access.domain.model;

import com.mt.access.domain.model.i18n.SupportedLocale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class I18nService {
    @Autowired
    MessageSource messageSource;

    public String getI18nValue(String code, SupportedLocale locale) {
        return messageSource.getMessage(code, null, null, locale.locale);
    }
}
