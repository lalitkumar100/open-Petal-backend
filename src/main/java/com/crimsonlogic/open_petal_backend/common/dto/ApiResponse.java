package com.crimsonlogic.open_petal_backend.common.dto;

public record ApiResponse<T>(
        boolean success,
        String messageCode,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(String messageCode, String message, T data) {
        return new ApiResponse<>(true, messageCode, message, data);
    }

    public static <T> ApiResponse<T> failure(String messageCode, String message) {
        return new ApiResponse<>(false, messageCode, message, null);
    }

    public static <T> ApiResponse<T> failure(String messageCode, String message, T data) {
        return new ApiResponse<>(false, messageCode, message, data);
    }
}
