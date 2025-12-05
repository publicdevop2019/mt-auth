package com.mt.access.domain.model.i18n;

import com.mt.access.domain.model.user.Language;
import java.util.Locale;

public enum SupportedLocale {
    zhHans("zh", "zh", "CN"),
    enUs("en", "en", "CA");
    public final String fileSuffix;
    public final Locale locale;

    SupportedLocale(String propertyFileSuffix, String language, String country) {
        this.fileSuffix = propertyFileSuffix;
        this.locale = new Locale(language, country);
    }

    public static SupportedLocale parseUserLang(Language language) {
        if (Language.ENGLISH.equals(language)) {
            return SupportedLocale.enUs;
        } else if (Language.MANDARIN.equals(language)) {
            return SupportedLocale.zhHans;
        }
        return SupportedLocale.enUs;
    }

    public static SupportedLocale parseUILang(String language) {
        if ("en".equalsIgnoreCase(language)) {
            return SupportedLocale.enUs;
        } else if ("zh".equalsIgnoreCase(language)) {
            return SupportedLocale.zhHans;
        }
        return SupportedLocale.enUs;
    }
}
