package com.example.psoft_22_23_project.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Filter to validate Content-Type headers for REST API endpoints
 * Ensures that API endpoints receive the expected Content-Type
 */
@Component
@Slf4j
public class ContentTypeValidationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Define which endpoints require specific Content-Type validation
    private static final List<String> JSON_ENDPOINTS = Arrays.asList(
            "/api/public/login",
            "/api/user/account",
            "/api/user/password",
            "/api/user/personal-data",
            "/api/subscriptions/create",
            "/api/plans"
    );

    // Define which HTTP methods require Content-Type validation
    private static final List<String> VALIDATION_METHODS = Arrays.asList("POST", "PUT", "PATCH");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String contentType = request.getContentType();

        // Only validate Content-Type for specific endpoints and methods
        if (shouldValidateContentType(requestUri, method)) {
            if (!isValidContentType(contentType, requestUri)) {
                handleUnsupportedMediaType(response, requestUri, contentType);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldValidateContentType(String requestUri, String method) {
        // Skip validation for multipart requests (file uploads)
        if (requestUri.contains("/photo") || requestUri.contains("/upload")) {
            return false;
        }

        // Only validate for specified HTTP methods
        if (!VALIDATION_METHODS.contains(method)) {
            return false;
        }

        // Check if the URI matches any of our API endpoints
        return JSON_ENDPOINTS.stream().anyMatch(endpoint -> 
            requestUri.startsWith(endpoint) || requestUri.matches(endpoint.replace("*", ".*")));
    }

    private boolean isValidContentType(String contentType, String requestUri) {
        if (contentType == null || contentType.trim().isEmpty()) {
            log.warn("Missing Content-Type header for endpoint: {}", requestUri);
            return false;
        }

        // Remove charset and other parameters for validation
        String baseContentType = contentType.split(";")[0].trim();

        // For JSON endpoints, validate application/json
        if (JSON_ENDPOINTS.stream().anyMatch(endpoint -> requestUri.startsWith(endpoint))) {
            boolean isValid = MediaType.APPLICATION_JSON_VALUE.equals(baseContentType);
            if (!isValid) {
                log.warn("Invalid Content-Type '{}' for JSON endpoint: {}", contentType, requestUri);
            }
            return isValid;
        }

        return true;
    }

    private void handleUnsupportedMediaType(HttpServletResponse response, String requestUri, String contentType) 
            throws IOException {
        
        log.warn("Rejecting request to {} with unsupported Content-Type: {}", requestUri, contentType);
        
        response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unsupported Media Type");
        errorResponse.put("message", "Content-Type '" + contentType + "' is not supported for this endpoint");
        errorResponse.put("expectedContentType", MediaType.APPLICATION_JSON_VALUE);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip filter for static resources, actuator endpoints, and documentation
        return path.startsWith("/swagger-ui") || 
               path.startsWith("/api-docs") || 
               path.startsWith("/actuator") ||
               path.startsWith("/h2") ||
               path.startsWith("/static") ||
               path.equals("/") ||
               path.equals("/favicon.ico");
    }
}
