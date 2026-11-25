package com.crimsonlogic.open_petal_backend.common.message.service;

import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.model.MessageEntry;
import com.crimsonlogic.open_petal_backend.common.message.repository.CsvMessageRepository;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.context.i18n.LocaleContextHolder;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private static final String UNKNOWN_CODE_FALLBACK = "Something went wrong";
    private final CsvMessageRepository repository;
    private final Set<String> unknownCodesLogged = ConcurrentHashMap.newKeySet();

    public MessageService(CsvMessageRepository repository) {
        this.repository = repository;
    }

    public String getMessage(String code, Object... arguments) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(code, locale, arguments);
    }

    public String getMessage(String code, Locale locale, Object... arguments) {
        MessageEntry entry = repository.findByCode(code);
        if (entry == null) {
            String unknownCode = code == null ? "<null>" : code;
            if (unknownCodesLogged.add(unknownCode)) {
                log.warn("Unknown application message code: {}", code);
            }
            MessageEntry fallback = repository.findByCode(MessageCodes.COMMON_ERROR);
            return format(fallback == null ? UNKNOWN_CODE_FALLBACK : resolve(fallback, locale), locale, arguments);
        }

        return format(resolve(entry, locale), locale, arguments);
    }

    private String resolve(MessageEntry entry, Locale locale) {
        String language = locale == null ? "en" : locale.getLanguage();
        String message = entry.translation(language);
        if (message == null || message.isBlank()) {
            message = entry.translation("en");
        }
        return message;
    }

    private String format(String message, Locale locale, Object... arguments) {
        return arguments == null || arguments.length == 0
                ? message
                : new MessageFormat(message, locale == null ? Locale.ENGLISH : locale).format(arguments);
    }
}
