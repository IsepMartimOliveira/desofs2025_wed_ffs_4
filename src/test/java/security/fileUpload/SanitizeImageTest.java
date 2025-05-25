package security.fileUpload;

import com.example.psoft_22_23_project.filestoragemanagement.sanitize.SanitizeImage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SanitizeImageTest {

    @Test
    void isValidImage_validJpg_shouldReturnTrue() throws Exception {
        String fileName = "image.jpg";
        byte[] jpgHeader = new byte[]{(byte) 0xFF, (byte) 0xD8, 0x00}; // Valid JPEG header
        try (InputStream stream = new ByteArrayInputStream(jpgHeader)) {
            assertTrue(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    void isValidImage_invalidMimeType_shouldReturnFalse() throws Exception {
        String fileName = "image.fake";
        byte[] fake = new byte[]{0x00, 0x01, 0x02};
        try (InputStream stream = new ByteArrayInputStream(fake)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    void isValidImage_invalidExtension_shouldReturnFalse() throws Exception {
        String fileName = "image.exe";
        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xD8};
        try (InputStream stream = new ByteArrayInputStream(data)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    void isValidImage_invalidFileName_shouldReturnFalse() throws Exception {
        String fileName = "bad.name.jpg";
        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xD8};
        try (InputStream stream = new ByteArrayInputStream(data)) {
            assertFalse(SanitizeImage.isValidImage(fileName, stream));
        }
    }

    @Test
    void getExtension_validFileName_shouldReturnExtension() {
        assertEquals(Optional.of("png"), SanitizeImage.getExtension("file.png"));
    }

    @Test
    void getExtension_multipleDots_shouldReturnEmpty() {
        assertEquals(Optional.empty(), SanitizeImage.getExtension("file.bad.png"));
    }
}
