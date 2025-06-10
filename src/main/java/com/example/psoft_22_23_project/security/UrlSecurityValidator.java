package com.example.psoft_22_23_project.security;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * URL Validator to prevent SSRF attacks by ensuring consistent URL parsing and validation
 */
@Component
public class UrlSecurityValidator {

    private static final Logger logger = LoggerFactory.getLogger(UrlSecurityValidator.class);

    // Allowed protocols
    private static final List<String> ALLOWED_PROTOCOLS = Arrays.asList("https");
    
    // Blocked IP ranges (private networks, localhost, etc.)
    private static final List<String> BLOCKED_IP_RANGES = Arrays.asList(
        "127.", "10.", "172.16.", "172.17.", "172.18.", "172.19.", 
        "172.20.", "172.21.", "172.22.", "172.23.", "172.24.", "172.25.",
        "172.26.", "172.27.", "172.28.", "172.29.", "172.30.", "172.31.",
        "192.168.", "169.254.", "0.0.0.0", "::1", "fc00:", "fe80:"
    );
    
    // Allowed domains (whitelist approach)
    private static final List<String> ALLOWED_DOMAINS = Arrays.asList(
        "api.trusted-service.com",
        "cdn.example.com"
        // Add your trusted domains here
    );

    /**
     * Validates a URL to prevent SSRF attacks
     * @param urlString the URL to validate
     * @return true if the URL is safe, false otherwise
     */
    public boolean isValidUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return false;
        }

        try {
            // First, consistently decode the URL
            String decodedUrl = URLDecoder.decode(urlString, StandardCharsets.UTF_8);
            
            // Check for double encoding attacks
            String doubleDecoded = URLDecoder.decode(decodedUrl, StandardCharsets.UTF_8);
            if (!decodedUrl.equals(doubleDecoded)) {
                logger.warn("Double encoding detected in URL: {}", urlString);
                return false;
            }

            // Parse the URL
            URI uri = URI.create(decodedUrl);
            URL url = uri.toURL();

            // Validate protocol
            if (!ALLOWED_PROTOCOLS.contains(url.getProtocol().toLowerCase())) {
                logger.warn("Invalid protocol in URL: {}", url.getProtocol());
                return false;
            }

            // Validate domain against whitelist
            String host = url.getHost();
            if (host == null || !ALLOWED_DOMAINS.contains(host.toLowerCase())) {
                logger.warn("Domain not in whitelist: {}", host);
                return false;
            }

            // Additional check for IP address instead of domain
            try {
                InetAddress address = InetAddress.getByName(host);
                String ipAddress = address.getHostAddress();
                
                // Check against blocked IP ranges
                for (String blockedRange : BLOCKED_IP_RANGES) {
                    if (ipAddress.startsWith(blockedRange)) {
                        logger.warn("Blocked IP range detected: {}", ipAddress);
                        return false;
                    }
                }
            } catch (java.net.UnknownHostException e) {
                logger.warn("Could not resolve host: {}", host);
                return false;
            }

            // Validate port (if specified)
            int port = url.getPort();
            if (port != -1 && (port < 80 || port > 443)) {
                logger.warn("Invalid port in URL: {}", port);
                return false;
            }

            return true;

        } catch (MalformedURLException | IllegalArgumentException e) {
            logger.warn("Invalid URL format: {}", urlString, e);
            return false;
        }
    }

    /**
     * Validates a file path to prevent path traversal attacks
     * @param filePath the file path to validate
     * @return true if the path is safe, false otherwise
     */
    public boolean isValidFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        try {
            // Decode the path consistently
            String decodedPath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
            
            // Normalize the path
            String normalizedPath = java.nio.file.Paths.get(decodedPath).normalize().toString();
            
            // Check for path traversal attempts
            if (normalizedPath.contains("..") || 
                normalizedPath.startsWith("/") || 
                normalizedPath.contains("\\") ||
                !normalizedPath.equals(decodedPath)) {
                logger.warn("Path traversal attempt detected: {}", filePath);
                return false;
            }
            
            return true;
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid file path: {}", filePath, e);
            return false;
        }
    }

    /**
     * Sanitizes a URI parameter by decoding and validating it
     * @param parameter the parameter to sanitize
     * @return sanitized parameter or null if invalid
     */
    public String sanitizeUriParameter(String parameter) {
        if (parameter == null) {
            return null;
        }

        try {
            // Decode consistently
            String decoded = URLDecoder.decode(parameter, StandardCharsets.UTF_8);
            
            // Remove dangerous characters
            String sanitized = decoded.replaceAll("[<>\"'&|;$`\\\\]", "");
            
            // Check length limits
            if (sanitized.length() > 1000) {
                logger.warn("Parameter too long, truncating: {}", parameter);
                sanitized = sanitized.substring(0, 1000);
            }
            
            return sanitized;
            
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to sanitize parameter: {}", parameter, e);
            return null;
        }
    }
}
