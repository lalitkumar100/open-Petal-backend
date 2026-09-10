package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.service.GeminiAiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiAiClientImpl implements GeminiAiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash-latest}")
    private String modelName;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Configure RestTemplate and ObjectMapper directly without requiring Spring beans
    public GeminiAiClientImpl() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 seconds
        factory.setReadTimeout(30000); // 30 seconds
        
        this.restTemplate = new RestTemplate(factory);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String generateContent(String systemPrompt, String payload) {
        String cleanModel = modelName != null ? modelName.replace("models/", "") : "gemini-1.5-flash";
        String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                cleanModel, apiKey);

        log.info("Sending request to Gemini API. Model: {}", cleanModel);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build Gemini API payload
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", systemPrompt))
        );
        requestBody.put("system_instruction", systemInstruction);

        Map<String, Object> contents = Map.of(
                "parts", List.of(Map.of("text", payload))
        );
        requestBody.put("contents", List.of(contents));

        Map<String, Object> generationConfig = Map.of(
                "response_mime_type", "application/json"
        );
        requestBody.put("generationConfig", generationConfig);

        int maxRetries = 3;
        int retryDelayMs = 3000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                String response = restTemplate.postForObject(url, entity, String.class);

                JsonNode rootNode = objectMapper.readTree(response);
                JsonNode candidatesNode = rootNode.path("candidates");

                if (candidatesNode.isMissingNode() || !candidatesNode.isArray() || candidatesNode.isEmpty()) {
                    log.error("Empty candidates returned from Gemini. Response: {}", response);
                    throw new RuntimeException("Gemini API returned no candidates. Check safety ratings or prompt filters.");
                }

                return candidatesNode.get(0)
                        .path("content")
                        .path("parts").get(0)
                        .path("text").asText();

            } catch (HttpStatusCodeException e) {
                int status = e.getStatusCode().value();
                if ((status == 503 || status == 429) && attempt < maxRetries) {
                    log.warn("Gemini API busy (HTTP {}). Retrying attempt {}/{} in {}ms...", status, attempt, maxRetries, retryDelayMs);
                    sleep(retryDelayMs);
                    retryDelayMs *= 2;
                    continue;
                }
                log.error("Gemini API HTTP Error - Status: {}, Body: {}", status, e.getResponseBodyAsString(), e);
                throw new RuntimeException("Gemini HTTP call failed: " + e.getResponseBodyAsString(), e);

            } catch (ResourceAccessException e) {
                // Catches socket timeouts and connection timeouts
                if (attempt < maxRetries) {
                    log.warn("Gemini API network timeout. Retrying attempt {}/{} in {}ms...", attempt, maxRetries, retryDelayMs);
                    sleep(retryDelayMs);
                    retryDelayMs *= 2;
                    continue;
                }
                log.error("Gemini network timeout exceeded maximum retries", e);
                throw new RuntimeException("Gemini API network error: " + e.getMessage(), e);

            } catch (Exception e) {
                log.error("Unexpected error interacting with Gemini API", e);
                throw new RuntimeException("Failed to process Gemini API request", e);
            }
        }

        throw new RuntimeException("Failed to generate content from Gemini after " + maxRetries + " attempts.");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted during retry backoff", ie);
        }
    }
}