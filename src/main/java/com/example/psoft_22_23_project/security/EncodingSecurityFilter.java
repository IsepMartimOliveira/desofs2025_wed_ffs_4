package com.example.psoft_22_23_project.security;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * Security filter to ensure consistent URL decoding and prevent encoding-based attacks
 */
@Component
public class EncodingSecurityFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(EncodingSecurityFilter.class);
    
    // Dangerous patterns that shouldn't appear even after URL decoding
    private static final String[] DANGEROUS_PATTERNS = {
        "..", "/", "\\", ":", "<script", "javascript:", "data:", "http:", "https:", "ftp:", "file:"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest httpRequest) {
            SecureHttpServletRequestWrapper wrappedRequest = new SecureHttpServletRequestWrapper(httpRequest);
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private static class SecureHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String[]> secureParameters;

        public SecureHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            this.secureParameters = sanitizeParameters(request);
        }

        @Override
        public String getParameter(String name) {
            String[] values = secureParameters.get(name);
            return values != null && values.length > 0 ? values[0] : null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return secureParameters.get(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.unmodifiableMap(secureParameters);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(secureParameters.keySet());
        }

        private Map<String, String[]> sanitizeParameters(HttpServletRequest request) {
            Map<String, String[]> sanitized = new HashMap<>();
            Map<String, String[]> originalParams = request.getParameterMap();

            for (Map.Entry<String, String[]> entry : originalParams.entrySet()) {
                String paramName = entry.getKey();
                String[] paramValues = entry.getValue();
                
                if (paramValues != null) {
                    String[] sanitizedValues = Arrays.stream(paramValues)
                            .map(this::sanitizeParameterValue)
                            .toArray(String[]::new);
                    sanitized.put(paramName, sanitizedValues);
                }
            }

            return sanitized;
        }

        private String sanitizeParameterValue(String value) {
            if (value == null || value.isEmpty()) {
                return value;
            }

            try {
                // First decode the URL-encoded value consistently
                String decoded = URLDecoder.decode(value, StandardCharsets.UTF_8);
                
                // Check for dangerous patterns in the decoded value
                for (String pattern : DANGEROUS_PATTERNS) {
                    if (decoded.toLowerCase().contains(pattern.toLowerCase())) {
                        logger.warn("Dangerous pattern '{}' detected in parameter value: {}", pattern, value);
                        throw new SecurityException("Invalid parameter value detected");
                    }
                }
                
                // Return the safely decoded value
                return decoded;
                
            } catch (IllegalArgumentException e) {
                logger.warn("Failed to decode parameter value: {}", value, e);
                throw new SecurityException("Invalid parameter encoding");
            }
        }
    }
}
