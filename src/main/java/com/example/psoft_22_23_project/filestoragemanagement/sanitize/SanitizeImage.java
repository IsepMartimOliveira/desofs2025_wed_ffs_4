package com.example.psoft_22_23_project.filestoragemanagement.sanitize;

import org.apache.tika.Tika;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;

public class SanitizeImage {

    private static final Tika tika = new Tika();

    private static boolean isValidExtension(String fileName) {
        String extension = getExtension(fileName).orElse("");
        return !extension.isEmpty() && AllowedImage.isAllowedExtension(extension);
    }

    /**
     * Securely extracts file extension with path traversal protection
     * @param filename the filename to extract extension from
     * @return Optional containing the extension if valid, empty otherwise
     */
    public static Optional<String> getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return Optional.empty();
        }

        try {
            // Normalize the path and extract just the filename component
            // This prevents path traversal attacks like "../../../file.jpg"
            String normalizedFilename = Paths.get(filename).getFileName().toString();

            // Ensure the normalized filename matches the original (no path components were removed)
            if (!normalizedFilename.equals(filename)) {
                LoggerFactory.getLogger(SanitizeImage.class)
                        .warn("Potential path traversal attempt detected in filename: {}", filename);
                return Optional.empty();
            }
        } catch (Exception e) {
            // Invalid path characters or other path-related issues
            LoggerFactory.getLogger(SanitizeImage.class)
                    .warn("Invalid filename format detected: {}", filename);
            return Optional.empty();
        }

        // Additional validation against dangerous patterns
        if (containsDangerousPatterns(filename)) {
            LoggerFactory.getLogger(SanitizeImage.class)
                    .warn("Dangerous patterns detected in filename: {}", filename);
            return Optional.empty();
        }

        // Count dots - should be exactly 1 for a valid file with extension
        long dotCount = filename.chars().filter(ch -> ch == '.').count();
        if (dotCount != 1) {
            LoggerFactory.getLogger(SanitizeImage.class)
                    .warn("Invalid dot count in filename: {} (count: {})", filename, dotCount);
            return Optional.empty();
        }

        // Extract and validate extension
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return Optional.empty();
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();

        // Validate extension format (only alphanumeric characters, reasonable length)
        if (!extension.matches("^[a-zA-Z0-9]{2,5}$")) {
            LoggerFactory.getLogger(SanitizeImage.class)
                    .warn("Invalid extension format: {}", extension);
            return Optional.empty();
        }

        return Optional.of(extension);
    }

    /**
     * Checks for dangerous patterns that could indicate path traversal or other attacks
     * @param filename the filename to check
     * @return true if dangerous patterns are found
     */
    private static boolean containsDangerousPatterns(String filename) {
        String[] dangerousPatterns = {
                "..",           // Path traversal
                "/",            // Unix path separator
                "\\",           // Windows path separator
                ":",            // Drive separator (Windows) or alternate data streams
                "*",            // Wildcards
                "?",            // Wildcards
                "\"",           // Quote characters
                "<",            // Redirection/HTML
                ">",            // Redirection/HTML
                "|",            // Pipe character
                "\0",           // Null byte
                "\r",           // Carriage return
                "\n"            // Line feed
        };

        for (String pattern : dangerousPatterns) {
            if (filename.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isValidMimeType(InputStream imageData, String fileName) {
        try {
            String mimeType = tika.detect(imageData, fileName);
            return mimeType != null && AllowedImage.isAllowedMimeType(mimeType);
        } catch (Exception e) {
            LoggerFactory.getLogger(SanitizeImage.class).error("Error detecting MIME type for file '{}'", fileName, e);
            return false;
        }
    }

    /**
     * Enhanced filename validation with security considerations
     * @param fileName the filename to validate
     * @return true if filename is valid and secure
     */
    private static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        // Check reasonable length limits
        if (fileName.length() > 255 || fileName.length() < 5) { // min 5 for "a.jpg"
            return false;
        }

        // Check for dangerous patterns first
        if (containsDangerousPatterns(fileName)) {
            return false;
        }

        // Enhanced regex pattern that's more restrictive
        // Allows: letters, numbers, hyphens, underscores, and exactly one dot
        // Format: name.extension where name is 1-200 chars, extension is 2-5 chars
        return fileName.matches("^[a-zA-Z0-9_-]{1,200}\\.[a-zA-Z0-9]{2,5}$");
    }

    public static boolean isValidImage(String fileName, InputStream inputStream) throws IOException {
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }

        try {
            // Mark the stream for MIME detection
            inputStream.mark(8192);
            String mimeType = tika.detect(inputStream, fileName);
            inputStream.reset();

            boolean isValidExtension = isValidExtension(fileName);
            boolean isValidMimeType = mimeType != null && AllowedImage.isAllowedMimeType(mimeType);
            boolean isValidFileName = isValidFileName(fileName);

            // Additional validation: ensure extension matches MIME type
            boolean extensionMimeTypeMatch = isExtensionMimeTypeConsistent(fileName, mimeType);

            boolean isValid = isValidExtension && isValidMimeType && isValidFileName && extensionMimeTypeMatch;

            if (isValid) {
                LoggerFactory.getLogger(SanitizeImage.class).info("File '{}' passed sanitization. MIME type: {}", fileName, mimeType);
            } else {
                String reason = buildFailureReason(isValidExtension, isValidMimeType, isValidFileName, extensionMimeTypeMatch);
                LoggerFactory.getLogger(SanitizeImage.class).warn("File '{}' failed sanitization. Reason: {} MIME type: {}", fileName, reason, mimeType);
            }

            return isValid;
        } catch (Exception e) {
            LoggerFactory.getLogger(SanitizeImage.class).error("Error validating image file '{}'", fileName, e);
            return false;
        }
    }

    private static boolean isExtensionMimeTypeConsistent(String fileName, String mimeType) {
        if (mimeType == null) return false;

        String extension = getExtension(fileName).orElse("").toLowerCase();

        return switch (mimeType.toLowerCase()) {
            case "image/jpeg" -> extension.equals("jpg") || extension.equals("jpeg");
            case "image/png" -> extension.equals("png");
            case "image/gif" -> extension.equals("gif");
            case "image/webp" -> extension.equals("webp");
            case "image/tiff" -> extension.equals("tiff") || extension.equals("tif");
            case "image/bmp" -> extension.equals("bmp");
            default -> false;
        };
    }

    private static String buildFailureReason(boolean isValidExtension, boolean isValidMimeType,
                                             boolean isValidFileName, boolean extensionMimeTypeMatch) {
        StringBuilder reason = new StringBuilder();
        if (!isValidExtension) reason.append("Invalid extension. ");
        if (!isValidMimeType) reason.append("Invalid MIME type. ");
        if (!isValidFileName) reason.append("Invalid file name format. ");
        if (!extensionMimeTypeMatch) reason.append("Extension doesn't match MIME type. ");
        return reason.toString().trim();
    }
}