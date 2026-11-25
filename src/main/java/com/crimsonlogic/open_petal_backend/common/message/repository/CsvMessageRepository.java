package com.crimsonlogic.open_petal_backend.common.message.repository;

import com.crimsonlogic.open_petal_backend.common.message.model.MessageEntry;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

@Repository
public class CsvMessageRepository {

    private static final Set<String> REQUIRED_HEADERS = Set.of("code", "en", "hi", "kn");

    private final ResourceLoader resourceLoader;
    private final String resourceLocation;
    private volatile Map<String, MessageEntry> messages = Map.of();

    public CsvMessageRepository(
            ResourceLoader resourceLoader,
            @Value("${app.messages.resource:classpath:messages/application-messages.csv}") String resourceLocation) {
        this.resourceLoader = resourceLoader;
        this.resourceLocation = resourceLocation;
    }

    @PostConstruct
    public void loadMessages() {
        Resource resource = resourceLoader.getResource(resourceLocation);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            messages = parse(reader);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load application message catalog", ex);
        }
    }

    Map<String, MessageEntry> parse(Reader reader) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .build();

        try (CSVParser parser = format.parse(reader)) {
            if (parser.getHeaderMap().size() != REQUIRED_HEADERS.size()
                    || !parser.getHeaderMap().keySet().equals(REQUIRED_HEADERS)) {
                throw new IllegalStateException("Message CSV must contain code,en,hi,kn columns");
            }

            Map<String, MessageEntry> loaded = new LinkedHashMap<>();
            for (CSVRecord record : parser) {
                if (record.size() != REQUIRED_HEADERS.size()) {
                    throw new IllegalStateException("Message CSV contains a record with an invalid number of columns");
                }
                String code = record.get("code").trim();
                String english = record.get("en").trim();
                if (code.isEmpty()) {
                    throw new IllegalStateException("Message CSV contains an empty message code");
                }
                if (english.isEmpty()) {
                    throw new IllegalStateException("Message CSV has no English translation for " + code);
                }

                Map<String, String> translations = new HashMap<>();
                for (String language : Set.of("en", "hi", "kn")) {
                    String translation = record.get(language).trim();
                    if (!translation.isEmpty()) {
                        translations.put(language, translation);
                    }
                }
                MessageEntry previous = loaded.putIfAbsent(code, new MessageEntry(code, translations));
                if (previous != null) {
                    throw new IllegalStateException("Duplicate message code in CSV: " + code);
                }
            }
            return Map.copyOf(loaded);
        }
    }

    public MessageEntry findByCode(String code) {
        return messages.get(code);
    }
}
