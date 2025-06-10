package security.fileUpload;

import com.example.psoft_22_23_project.filestoragemanagement.sanitize.SanitizeImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SanitizeImageTest {

    @Test
    @DisplayName("Valid JPEG file should pass validation")
    void isValidImage_validJpg_shouldReturnTrue() throws Exception {
        String fileName = "image.jpg";
        // More complete JPEG header for better Tika detection
        byte[] jpgHeader = new byte[]{
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
                0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01
        };
        try (InputStream stream = new ByteArrayInputStream(jpgHeader)) {
            assertTrue(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    @DisplayName("Invalid MIME type should be rejected")
    void isValidImage_invalidMimeType_shouldReturnFalse() throws Exception {
        String fileName = "image.fake";
        byte[] fake = new byte[]{0x00, 0x01, 0x02};
        try (InputStream stream = new ByteArrayInputStream(fake)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    @DisplayName("Invalid extension should be rejected")
    void isValidImage_invalidExtension_shouldReturnFalse() throws Exception {
        String fileName = "image.exe";
        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xD8};
        try (InputStream stream = new ByteArrayInputStream(data)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    @DisplayName("Invalid filename format should be rejected")
    void isValidImage_invalidFileName_shouldReturnFalse() throws Exception {
        String fileName = "bad.name.jpg";
        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xD8};
        try (InputStream stream = new ByteArrayInputStream(data)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    @DisplayName("Valid filename should return correct extension")
    void getExtension_validFileName_shouldReturnExtension() {
        assertEquals(Optional.of("png"), SanitizeImage.getExtension("file.png"));
    }

    @Test
    @DisplayName("Multiple dots in filename should return empty")
    void getExtension_multipleDots_shouldReturnEmpty() {
        assertEquals(Optional.empty(), SanitizeImage.getExtension("file.bad.png"));
    }

    @Test
    @DisplayName("Valid PNG file should pass validation")
    void isValidImage_validPng_shouldReturnTrue() throws Exception {
        String fileName = "image.png";
        // PNG signature: 89 50 4E 47 0D 0A 1A 0A
        byte[] pngHeader = new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        };
        try (InputStream stream = new ByteArrayInputStream(pngHeader)) {
            assertTrue(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    @DisplayName("Valid GIF file should pass validation")
    void isValidImage_validGif_shouldReturnTrue() throws Exception {
        String fileName = "image.gif";
        // GIF87a or GIF89a header
        byte[] gifHeader = new byte[]{
                0x47, 0x49, 0x46, 0x38, 0x39, 0x61  // "GIF89a"
        };
        try (InputStream stream = new ByteArrayInputStream(gifHeader)) {
            assertTrue(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    @DisplayName("Extension-MIME type mismatch should be rejected")
    void isValidImage_extensionMimeTypeMismatch_shouldReturnFalse() throws Exception {
        String fileName = "image.jpg";  // Claims to be JPEG
        // But content is PNG
        byte[] pngHeader = new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        };
        try (InputStream stream = new ByteArrayInputStream(pngHeader)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "file with spaces.jpg",
            "file@symbol.jpg",
            "file#hash.png",
            "file$.gif",
            "file%.jpg",
            "file&.png",
            "file*.gif",
            "file+.jpg",
            "file=.png",
            "file[].gif",
            "file{}.jpg",
            "file().png"
    })
    @DisplayName("Filenames with invalid characters should be rejected")
    void isValidImage_invalidCharactersInFilename_shouldReturnFalse(String fileName) throws Exception {
        byte[] jpegHeader = new byte[]{
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0
        };
        try (InputStream stream = new ByteArrayInputStream(jpegHeader)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "file.j, jpg",
            "file.toolong, jpg",
            "file., jpg",
            "file, jpg",
            ".jpg, jpg"
    })
    @DisplayName("Invalid filename formats should be rejected")
    void isValidImage_invalidFilenameFormats_shouldReturnFalse(String fileName, String headerType) throws Exception {
        byte[] header = getValidHeaderForType(headerType);
        try (InputStream stream = new ByteArrayInputStream(header)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"valid-file.jpg", "valid_file.png", "ValidFile123.gif", "file123.webp"})
    @DisplayName("Valid filename formats should be accepted")
    void isValidImage_validFilenameFormats_shouldReturnTrue(String fileName) throws Exception {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        byte[] header = getValidHeaderForType(extension);
        try (InputStream stream = new ByteArrayInputStream(header)) {
            assertTrue(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    @DisplayName("Null filename should be rejected")
    void isValidImage_nullFileName_shouldReturnFalse() throws Exception {
        byte[] jpegHeader = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        try (InputStream stream = new ByteArrayInputStream(jpegHeader)) {
            assertFalse(SanitizeImage.isValidImage(null, stream));
        }
    }

    @Test
    @DisplayName("Empty filename should be rejected")
    void isValidImage_emptyFileName_shouldReturnFalse() throws Exception {
        byte[] jpegHeader = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        try (InputStream stream = new ByteArrayInputStream(jpegHeader)) {
            assertFalse(SanitizeImage.isValidImage("", stream));
        }
    }

    @Test
    @DisplayName("Malicious double extension should be rejected")
    void isValidImage_doubleExtension_shouldReturnFalse() throws Exception {
        String fileName = "image.jpg.exe";
        byte[] jpegHeader = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        try (InputStream stream = new ByteArrayInputStream(jpegHeader)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    // Additional extension tests
    @ParameterizedTest
    @CsvSource({
            "image.jpg, jpg",
            "image.jpeg, jpeg",
            "image.png, png",
            "image.gif, gif",
            "image.webp, webp",
            "image.tiff, tiff",
            "image.bmp, bmp"
    })
    @DisplayName("All supported extensions should be recognized")
    void getExtension_supportedExtensions_shouldReturnCorrectExtension(String filename, String expectedExtension) {
        assertEquals(Optional.of(expectedExtension), SanitizeImage.getExtension(filename));
    }



    @Test
    @DisplayName("Null filename should return empty")
    void getExtension_nullFilename_shouldReturnEmpty() {
        assertEquals(Optional.empty(), SanitizeImage.getExtension(null));
    }

    @Test
    @DisplayName("Empty filename should return empty")
    void getExtension_emptyFilename_shouldReturnEmpty() {
        assertEquals(Optional.empty(), SanitizeImage.getExtension(""));
    }




    @Test
    @DisplayName("Text file masquerading as image should be rejected")
    void isValidImage_textFileWithImageExtension_shouldReturnFalse() throws Exception {
        String fileName = "fake.jpg";
        byte[] textContent = "This is just text content".getBytes();
        try (InputStream stream = new ByteArrayInputStream(textContent)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    @DisplayName("HTML file with image extension should be rejected")
    void isValidImage_htmlFileWithImageExtension_shouldReturnFalse() throws Exception {
        String fileName = "malicious.jpg";
        byte[] htmlContent = "<html><script>alert('xss')</script></html>".getBytes();
        try (InputStream stream = new ByteArrayInputStream(htmlContent)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    private byte[] getValidHeaderForType(String type) {
        return switch (type.toLowerCase()) {
            case "jpg", "jpeg" -> new byte[]{
                    (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
                    0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01
            };
            case "png" -> new byte[]{
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
            };
            case "gif" -> new byte[]{
                    0x47, 0x49, 0x46, 0x38, 0x39, 0x61  // "GIF89a"
            };
            case "webp" -> new byte[]{
                    0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00,
                    0x57, 0x45, 0x42, 0x50  // "RIFF....WEBP"
            };
            case "bmp" -> new byte[]{
                    0x42, 0x4D  // "BM"
            };
            default -> new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        };
    }
}