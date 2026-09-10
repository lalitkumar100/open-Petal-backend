package com.crimsonlogic.open_petal_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Production-ready HTTP Request & Response Logging Filter.
 * Logs clean single-line summaries to console, while persisting detailed API audit logs 
 * to a dedicated log file ('logs/api.log') which is cleared on server startup.
 */
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger consoleLog = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final Logger apiLog = LoggerFactory.getLogger("API_LOGGER");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip logging for static assets or actuator endpoints if any
        String uri = request.getRequestURI();
        if (isStaticOrDocAsset(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 10000);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();

            String requestBody = getStringValue(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
            String responseBody = isTextOrJson(responseWrapper.getContentType())
                    ? getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding())
                    : "[BINARY OR NON-TEXT CONTENT]";

            // 1. Clean, concise single-line console output
            consoleLog.info("[API] {} {} -> Status {} ({} ms)", request.getMethod(), uri, status, timeTaken);

            // 2. Detailed audit entry in dedicated 'logs/api.log' file
            logApiDetailsToFile(request, requestBody, status, responseBody, timeTaken);

            // Copy cached response body back to the actual HTTP response stream
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logApiDetailsToFile(HttpServletRequest request, String requestBody, int status, String responseBody, long timeTaken) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n==================================================\n");
        builder.append("HTTP REQUEST DETAILS\n");
        builder.append("Client IP : ").append(getClientIp(request)).append("\n");
        builder.append("Method    : ").append(request.getMethod()).append("\n");
        builder.append("URI       : ").append(request.getRequestURI());
        if (request.getQueryString() != null) {
            builder.append("?").append(request.getQueryString());
        }
        builder.append("\n");

        if (requestBody != null && !requestBody.trim().isEmpty()) {
            builder.append("Request Body:\n").append(maskSensitiveData(requestBody)).append("\n");
        }

        builder.append("--------------------------------------------------\n");
        builder.append("HTTP RESPONSE DETAILS\n");
        builder.append("Status    : ").append(status).append("\n");
        builder.append("Duration  : ").append(timeTaken).append(" ms\n");

        if (responseBody != null && !responseBody.trim().isEmpty()) {
            builder.append("Response Body:\n").append(maskSensitiveData(responseBody)).append("\n");
        }
        builder.append("==================================================");

        apiLog.info(builder.toString());
    }

    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
        try {
            if (contentAsByteArray == null || contentAsByteArray.length == 0) {
                return "";
            }
            if (characterEncoding == null) {
                characterEncoding = StandardCharsets.UTF_8.name();
            }
            return new String(contentAsByteArray, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            return "[unknown encoding]";
        }
    }

    private boolean isTextOrJson(String contentType) {
        if (contentType == null) return true;
        String lower = contentType.toLowerCase();
        return lower.contains("json") || lower.contains("text") || lower.contains("xml") || lower.contains("form");
    }

    private boolean isStaticOrDocAsset(String uri) {
        if (uri == null) return false;
        return uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".ico") || uri.endsWith(".png")
                || uri.endsWith(".jpg") || uri.endsWith(".svg") || uri.contains("/v3/api-docs") || uri.contains("/swagger-ui");
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private String maskSensitiveData(String body) {
        if (body == null || body.isEmpty()) {
            return body;
        }
        String masked = body;
        masked = masked.replaceAll("(?i)\"(password|oldPassword|newPassword|confirmPassword)\"\\s*:\\s*\"[^\"]+\"", "\"$1\": \"[PROTECTED]\"");
        masked = masked.replaceAll("(?i)\"(token|accessToken|refreshToken|jwt|secret|apiKey)\"\\s*:\\s*\"[^\"]+\"", "\"$1\": \"[PROTECTED]\"");
        masked = masked.replaceAll("(?i)Bearer\\s+[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+", "Bearer [PROTECTED]");
        return masked;
    }
}
