package com.example.psoft_22_23_project.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class Utils {

    // Gets the id of current authenticated user
    public static String getAuthId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return username.split(",")[0];
    }

    public static String transformSpaces(String input) {
        String trimmed = input.trim();
        String replaced = trimmed.replaceAll("\\s+", " ");
        return replaced.replaceAll(" ", "_");
    }

    public static String sanitize(String input) {
        if (input == null) return null;
        
        // First decode any URL encoding consistently
        try {
            String decoded = java.net.URLDecoder.decode(input, java.nio.charset.StandardCharsets.UTF_8);
            
            // Check for path traversal and injection patterns
            String[] dangerousPatterns = {
                "..", "/", "\\", ":", "<", ">", "\"", "'", "&", "|", ";", "$", "`",
                "javascript:", "data:", "http:", "https:", "ftp:", "file:"
            };
            
            for (String pattern : dangerousPatterns) {
                if (decoded.toLowerCase().contains(pattern)) {
                    // Return sanitized version by removing dangerous characters
                    decoded = decoded.replaceAll("[<>\"'&|;$`:/\\\\]", "_");
                    break;
                }
            }
            
            // Replace control characters
            return decoded.replaceAll("[\n\r\t\0]", "_");
            
        } catch (Exception e) {
            // If decoding fails, sanitize the original input
            return input.replaceAll("[\n\r\t\0<>\"'&|;$`:/\\\\]", "_");
        }
    }


}
