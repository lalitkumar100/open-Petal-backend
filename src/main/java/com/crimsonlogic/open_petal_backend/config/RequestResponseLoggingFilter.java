package com.crimsonlogic.open_petal_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap the request and response to cache their content
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 10000);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            // Proceed with the next filter in the chain
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;

            // Extract the cached bodies
            String requestBody = getStringValue(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
            String responseBody = getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());

            logRequest(request, requestBody);
            logResponse(response, responseBody, timeTaken);

            // Important: copy the cached response body back to the actual response
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(HttpServletRequest request, String body) {
        log.debug("==================================================");
        log.debug("REQUEST");
        log.debug("{} {}", request.getMethod(), request.getRequestURI());

        if (body != null && !body.trim().isEmpty()) {
            log.debug("Body:\n{}", maskSensitiveData(body));
        }
        log.debug("==================================================");
    }

    private void logResponse(HttpServletResponse response, String body, long timeTaken) {
        log.debug("==================================================");
        log.debug("RESPONSE");
        log.debug("Status: {}", response.getStatus());

        if (body != null && !body.trim().isEmpty()) {
            log.debug("Body:\n{}", maskSensitiveData(body));
        }
        
        log.debug("Time: {} ms", timeTaken);
        log.debug("==================================================");
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
            log.warn("Exception while reading request/response body", e);
            return "[unknown encoding]";
        }
    }

    private String maskSensitiveData(String body) {
        return body.replaceAll(
                "(?i)(\"(?:[^\"]*(?:password|token|secret)|authorization)\"\\s*:\\s*\")[^\"]*(\")",
                "$1[PROTECTED]$2");
    }
}
