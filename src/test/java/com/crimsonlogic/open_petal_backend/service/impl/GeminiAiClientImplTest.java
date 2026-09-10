package com.crimsonlogic.open_petal_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("GeminiAiClientImpl Mockito Unit Tests")
class GeminiAiClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GeminiAiClientImpl geminiAiClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(geminiAiClient, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(geminiAiClient, "modelName", "gemini-1.5-flash");
        ReflectionTestUtils.setField(geminiAiClient, "restTemplate", restTemplate);
    }

    /**
     * Purpose: Tests successful JSON parsing of response candidate text from Gemini API.
     * Preconditions: Gemini API returns valid candidates JSON response structure.
     * Expected Behavior: Returns extracted text string from candidate.
     */
    @Test
    @DisplayName("Should extract text response from Gemini API response candidates")
    void givenValidApiResponse_whenGenerateContent_thenReturnsExtractedText() {
        // Arrange
        String mockResponse = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      { "text": "{\\"title\\": \\"Skill Analytics\\"}" }
                    ]
                  }
                }
              ]
            }
            """;

        given(restTemplate.postForObject(anyString(), any(), any())).willReturn(mockResponse);

        // Act
        String result = geminiAiClient.generateContent("System Prompt", "User Prompt");

        // Assert
        assertThat(result).isEqualTo("{\"title\": \"Skill Analytics\"}");
    }

    /**
     * Purpose: Verifies error throwing when Gemini returns empty candidates list.
     * Preconditions: Gemini API returns response without candidates.
     * Expected Behavior: Throws RuntimeException wrapped error.
     */
    @Test
    @DisplayName("Should throw RuntimeException when candidates array is empty")
    void givenEmptyCandidates_whenGenerateContent_thenThrowsRuntimeException() {
        // Arrange
        String mockResponse = "{\"candidates\": []}";

        given(restTemplate.postForObject(anyString(), any(), any())).willReturn(mockResponse);

        // Act & Assert
        assertThatThrownBy(() -> geminiAiClient.generateContent("System Prompt", "User Prompt"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to process Gemini API request");
    }
}
