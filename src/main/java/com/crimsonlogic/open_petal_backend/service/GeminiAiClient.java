package com.crimsonlogic.open_petal_backend.service;

/**
 * Service interface for gemini ai client operations.
 */
public interface GeminiAiClient {
    /**
     * Generate Content.
     */
    String generateContent(String systemPrompt, String payload);
}
