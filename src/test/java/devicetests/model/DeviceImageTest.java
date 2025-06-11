package devicetests.model;

import com.example.psoft_22_23_project.devicemanagement.model.DeviceImage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class DeviceImageTest {
    @Test
    public void testDefaultConstructor() {
        DeviceImage image = new DeviceImage();
        assertNotNull(image);
    }

    @Test
    public void testParameterizedConstructor() {
        String prefix = "img";
        String fileName = "device123.png";
        String uri = "http://localhost/download/device123.png";
        String contentType = "image/png";
        long fileSize = 2048L;

        DeviceImage image = new DeviceImage(prefix, fileName, uri, contentType, fileSize);

        assertEquals(prefix, image.getPrefix());
        assertEquals(fileName, image.getFileName());
        assertEquals(uri, image.getFileDownloadUri());
        assertEquals(contentType, image.getContentType());
        assertEquals(fileSize, image.getFileSize());
    }

    @Test
    public void testSettersAndGetters() {
        DeviceImage image = new DeviceImage();

        image.setPrefix("thumb");
        image.setFileName("thumb123.jpg");
        image.setFileDownloadUri("http://localhost/download/thumb123.jpg");
        image.setContentType("image/jpeg");
        image.setFileSize(1024L);

        assertEquals("thumb", image.getPrefix());
        assertEquals("thumb123.jpg", image.getFileName());
        assertEquals("http://localhost/download/thumb123.jpg", image.getFileDownloadUri());
        assertEquals("image/jpeg", image.getContentType());
        assertEquals(1024L, image.getFileSize());
    }

}
