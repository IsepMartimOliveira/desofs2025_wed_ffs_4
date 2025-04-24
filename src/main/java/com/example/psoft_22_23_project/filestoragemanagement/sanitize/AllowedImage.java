package com.example.psoft_22_23_project.filestoragemanagement.sanitize;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AllowedImage {

    JPG("image/jpeg", "jpg", new byte[] { (byte) 0xFF, (byte) 0xD8 }),
    JPEG("image/jpeg", "jpeg", new byte[] { (byte) 0xFF, (byte) 0xD8 }),
    PNG("image/png", "png", new byte[] { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47 }),
    GIF("image/gif", "gif", new byte[] { (byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38 }),
    WEBP("image/webp", "webp", new byte[] { (byte) 0x52, (byte) 0x49, (byte) 0x46, (byte) 0x46 }),
    TIFF("image/tiff", "tiff", new byte[] { (byte) 0x49, (byte) 0x49, (byte) 0x2A, (byte) 0x00 }),
    BMP("image/bmp", "bmp", new byte[] { (byte) 0x42, (byte) 0x4D });

    private final String mimeType;
    private final String extension;
    private final byte[] header;

    AllowedImage(String mimeType, String extension, byte[] header) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.header = header;
    }

    public static boolean isAllowedMimeType(String mimeType) {
        return Arrays.stream(values())
                .anyMatch(e -> e.getMimeType().equalsIgnoreCase(mimeType));
    }

    public static boolean isAllowedExtension(String extension) {
        return Arrays.stream(values())
                .anyMatch(e -> e.getExtension().equalsIgnoreCase(extension));
    }

    public static boolean hasValidHeader(String mimeType, byte[] fileHeader) {
        return Arrays.stream(values())
                .filter(e -> e.getMimeType().equalsIgnoreCase(mimeType))
                .anyMatch(e -> Arrays.equals(e.getHeader(), fileHeader));
    }

    public static boolean isValidImage(String mimeType, String extension, byte[] fileHeader) {
        return isAllowedMimeType(mimeType) && isAllowedExtension(extension) && hasValidHeader(mimeType, fileHeader);
    }
}
