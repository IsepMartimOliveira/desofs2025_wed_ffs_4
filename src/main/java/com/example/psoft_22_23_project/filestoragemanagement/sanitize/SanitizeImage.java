package com.example.psoft_22_23_project.filestoragemanagement.sanitize;

import org.apache.tika.Tika;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class SanitizeImage {

    private static final Tika tika = new Tika();

    private static boolean isValidExtension(String fileName) {
        String extension = getExtension(fileName).orElse("");
        return !extension.isEmpty() && AllowedImage.isAllowedExtension(extension);
    }

    public static Optional<String> getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return Optional.empty();
        }

        long dotCount = filename.chars().filter(ch -> ch == '.').count();

        if (dotCount > 1) {
            return Optional.empty();
        }

        return Optional.of(filename.substring(filename.lastIndexOf('.') + 1));
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

    private static boolean isValidFileName(String fileName) {
        return fileName != null && fileName.matches("^[a-zA-Z0-9_-]+\\.[a-zA-Z]{3,4}$");
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