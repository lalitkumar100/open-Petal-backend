package com.crimsonlogic.open_petal_backend.common.message.model;

import java.util.Map;

public record MessageEntry(String code, Map<String, String> translations) {

    public MessageEntry {
        translations = Map.copyOf(translations);
    }

    public String translation(String language) {
        return translations.get(language);
    }
}
