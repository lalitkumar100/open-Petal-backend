package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.service.GeminiAiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the gemini ai client service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiAiClientImpl implements GeminiAiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash-latest}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generateContent(String systemPrompt, String payload) {
        String cleanModel = modelName != null ? modelName.replace("models/", "") : "gemini-1.5-flash";
        String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", cleanModel, apiKey);
        
        log.info("Sending request to Gemini API. URL (without key): https://generativelanguage.googleapis.com/v1beta/models/{}:generateContent", cleanModel);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build the request body according to Gemini API specs
        Map<String, Object> requestBody = new HashMap<>();
        
        // System instructions
        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", systemPrompt)));
        requestBody.put("system_instruction", systemInstruction);

        // User input payload
        Map<String, Object> contents = new HashMap<>();
        contents.put("parts", List.of(Map.of("text", payload)));
        requestBody.put("contents", List.of(contents));

        // Enforce JSON output format
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("response_mime_type", "application/json");
        requestBody.put("generationConfig", generationConfig);

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.postForObject(url, entity, String.class);
            
            // Extract the actual text from the Gemini response JSON
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidatesNode = rootNode.path("candidates");
            
            if (candidatesNode.isMissingNode() || !candidatesNode.isArray() || candidatesNode.isEmpty()) {
                log.error("Empty candidates returned from Gemini. Full response: {}", response);
                throw new RuntimeException("Gemini API returned no candidates. Possible safety block or empty response.");
            }
            
            return candidatesNode.get(0)
                           .path("content")
                           .path("parts").get(0)
                           .path("text").asText();
                           
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            log.error("Gemini API HTTP Error - Status: {}, Response Body: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to generate content from Gemini: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Error communicating with Gemini API", e);
            throw new RuntimeException("Failed to generate content from Gemini", e);
        }
    }
}
