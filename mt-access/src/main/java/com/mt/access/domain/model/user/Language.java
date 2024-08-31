package com.mt.access.domain.model.user;

import java.io.Serializable;
import lombok.Getter;

@Getter
public enum Language implements Serializable {
    ENGLISH("english"),
    MANDARIN("mandarin");
    private final String label;

    public static Language parse(String language) {
        if ("english".equalsIgnoreCase(language)) {
            return ENGLISH;
        } else if ("mandarin".equalsIgnoreCase(language)) {
            return MANDARIN;
        }
        return null;
    }

    Language(String label) {
        this.label = label;
    }
}
