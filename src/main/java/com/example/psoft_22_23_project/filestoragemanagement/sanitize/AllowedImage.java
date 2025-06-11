package com.example.psoft_22_23_project.filestoragemanagement.sanitize;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

@Getter
public enum AllowedImage {

    JPG("image/jpeg", "jpg"),
    JPEG("image/jpeg", "jpeg"),
    PNG("image/png", "png"),
    GIF("image/gif", "gif"),
    WEBP("image/webp", "webp"),
    TIFF("image/tiff", "tiff"),
    BMP("image/bmp", "bmp");

    private final String mimeType;
    private final String extension;

    AllowedImage(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public static boolean isAllowedMimeType(String mimeType) {
        return Arrays.stream(values())
                .anyMatch(e -> e.getMimeType().equalsIgnoreCase(mimeType));
    }

    public static boolean isAllowedExtension(String extension) {
        return Arrays.stream(values())
                .anyMatch(e -> e.getExtension().equalsIgnoreCase(extension));
    }

    // Get all allowed MIME types for documentation/debugging
    public static Set<String> getAllowedMimeTypes() {
        return Arrays.stream(values())
                .map(AllowedImage::getMimeType)
                .collect(java.util.stream.Collectors.toSet());
    }

    // Get all allowed extensions for documentation/debugging
    public static Set<String> getAllowedExtensions() {
        return Arrays.stream(values())
                .map(AllowedImage::getExtension)
                .collect(java.util.stream.Collectors.toSet());
    }
}