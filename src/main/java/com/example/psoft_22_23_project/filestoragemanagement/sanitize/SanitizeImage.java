package com.example.psoft_22_23_project.filestoragemanagement.sanitize;

import org.apache.tika.Tika;
import org.slf4j.LoggerFactory;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

    private static boolean hasValidHeader(InputStream inputStream, String mimeType, String extension) throws IOException {
        Optional<AllowedImage> match = Arrays.stream(AllowedImage.values())
                .filter(e -> e.getMimeType().equalsIgnoreCase(mimeType))
                .findFirst();

        if (match.isEmpty()) {
            return false;
        }

        byte[] expectedHeader = match.get().getHeader();
        byte[] fileHeader = new byte[expectedHeader.length];
        inputStream.read(fileHeader, 0, expectedHeader.length);

        return Arrays.equals(expectedHeader, fileHeader);
    }


    public static boolean isValidImage(String fileName, InputStream inputStream) throws IOException {
        try (BufferedInputStream bufferedStream = new BufferedInputStream(inputStream)) {
            String fileExtension = getFileExtension(fileName);
            String mimeType = tika.detect(bufferedStream, fileName);

            boolean isValidExtension = isValidExtension(fileName);
            boolean isValidMimeType = isValidMimeType(bufferedStream, fileName);
            boolean isValidFileName = isValidFileName(fileName);
            boolean hasValidHeader = hasValidHeader(bufferedStream, mimeType, fileExtension);

            boolean isValid = isValidExtension && isValidMimeType &&  isValidFileName && hasValidHeader;

            if (isValid) {
                LoggerFactory.getLogger(SanitizeImage.class).info("File '{}' passed sanitization.", fileName);
            } else {
                String reason = buildFailureReason(isValidExtension, isValidMimeType, isValidFileName);
                LoggerFactory.getLogger(SanitizeImage.class).warn("File '{}' failed sanitization. Reason: {}", fileName, reason);
            }

            return isValid;
        }
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex > 0) ? fileName.substring(dotIndex + 1).toLowerCase() : "";
    }
    private static String buildFailureReason(boolean isValidExtension, boolean isValidMimeType, boolean isValidFileName) {
        StringBuilder reason = new StringBuilder();
        if (!isValidExtension) reason.append("Invalid extension. ");
        if (!isValidMimeType) reason.append("Invalid MIME type. ");
        if (!isValidFileName) reason.append("Invalid file name format.");
        return reason.toString().trim();
    }
}
