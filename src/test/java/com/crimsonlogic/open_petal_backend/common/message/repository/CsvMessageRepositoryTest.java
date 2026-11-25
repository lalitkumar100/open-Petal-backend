package com.crimsonlogic.open_petal_backend.common.message.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import java.io.StringReader;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.context.i18n.LocaleContextHolder;

class CsvMessageRepositoryTest {

    private CsvMessageRepository repository;
    private MessageService messageService;

    @BeforeEach
    void loadTestCatalog() {
        repository = new CsvMessageRepository(new DefaultResourceLoader(), "classpath:messages/test-messages.csv");
        repository.loadMessages();
        messageService = new MessageService(repository);
    }

    @AfterEach
    void clearLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void loadsEnglishHindiAndKannadaTranslations() {
        assertEquals("Hello Ada", messageService.getMessage("GREETING", Locale.ENGLISH, "Ada"));
        assertEquals("नमस्ते Ada", messageService.getMessage("GREETING", Locale.forLanguageTag("hi"), "Ada"));
        assertEquals("ನಮಸ್ಕಾರ Ada", messageService.getMessage("GREETING", Locale.forLanguageTag("kn"), "Ada"));
    }

    @Test
    void fallsBackToEnglishWhenTranslationIsMissing() {
        assertEquals("English fallback", messageService.getMessage("FALLBACK", Locale.forLanguageTag("hi")));
    }

    @Test
    void unknownCodesReturnSafeFallback() {
        assertEquals("Something went wrong", messageService.getMessage("UNKNOWN_CODE", Locale.ENGLISH));
    }

    @Test
    void usesLocaleContextHolderForLanguageDetection() {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("hi"));
        assertEquals("नमस्ते Ada", messageService.getMessage("GREETING", "Ada"));
    }

    @Test
    void rejectsDuplicateMessageCodes() {
        String csv = "code,en,hi,kn\nA,First,पहला,ಮೊದಲ\nA,Second,दूसरा,ಎರಡನೇ\n";
        assertThrows(IllegalStateException.class, () -> repository.parse(new StringReader(csv)));
    }

    @Test
    void rejectsMissingEnglishTranslation() {
        String csv = "code,en,hi,kn\nA,,हिंदी,ಕನ್ನಡ\n";
        assertThrows(IllegalStateException.class, () -> repository.parse(new StringReader(csv)));
    }

    @Test
    void rejectsInvalidCsvStructure() {
        String csv = "code,en,hi\nA,English,हिंदी\n";
        assertThrows(IllegalStateException.class, () -> repository.parse(new StringReader(csv)));
    }

    @Test
    void loadsTheApplicationCatalogAtStartup() {
        CsvMessageRepository applicationRepository = new CsvMessageRepository(
                new DefaultResourceLoader(), "classpath:messages/application-messages.csv");
        applicationRepository.loadMessages();
        assertNotNull(applicationRepository.findByCode("AUTH_LOGIN_SUCCESS"));
    }
}
