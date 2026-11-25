package com.crimsonlogic.open_petal_backend.common.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ApiResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void successFactoryPreservesCodeMessageAndGenericData() {
        ApiResponse<Map<String, String>> response =
                ApiResponse.success("AUTH_LOGIN_SUCCESS", "Login successful", Map.of("accessToken", "token"));

        assertTrue(response.success());
        assertEquals("AUTH_LOGIN_SUCCESS", response.messageCode());
        assertEquals("Login successful", response.message());
        assertEquals("token", response.data().get("accessToken"));
    }

    @Test
    void failureFactoryUsesNullData() {
        ApiResponse<Void> response = ApiResponse.failure("COMMON_ERROR", "Something went wrong");

        assertFalse(response.success());
        assertEquals("COMMON_ERROR", response.messageCode());
        assertNull(response.data());
    }

    @Test
    void recordSerializationUsesConsistentFourFieldShapeAndKeepsNullData() throws Exception {
        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsBytes(
                ApiResponse.success("USER_PASSWORD_CHANGED", "Password changed", null)));

        assertEquals(4, json.size());
        assertTrue(json.get("success").asBoolean());
        assertEquals("USER_PASSWORD_CHANGED", json.get("messageCode").asText());
        assertEquals("Password changed", json.get("message").asText());
        assertTrue(json.has("data"));
        assertTrue(json.get("data").isNull());
    }
}
