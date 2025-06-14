package devicetests.api;

import com.example.psoft_22_23_project.devicemanagement.api.*;
import com.example.psoft_22_23_project.devicemanagement.model.Device;
import com.example.psoft_22_23_project.devicemanagement.model.DeviceImage;
import com.example.psoft_22_23_project.devicemanagement.model.MacAddress;
import com.example.psoft_22_23_project.devicemanagement.services.DeviceService;
import com.example.psoft_22_23_project.filestoragemanagement.service.FileStorageService;
import com.example.psoft_22_23_project.filestoragemanagement.api.UploadFileResponse;
import com.example.psoft_22_23_project.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeviceControllerTest {

    @InjectMocks
    private DeviceController controller;

    @Mock
    private DeviceService deviceService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private DeviceViewMapper deviceViewMapper;

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoUploadFile() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/device");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);

        String id = "device1";
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test data".getBytes());

        when(fileStorageService.storeFile(id, file)).thenReturn("storedTest.png");

        // We need to mock ServletUriComponentsBuilder because it uses static method fromCurrentRequestUri()
        // So we create a URI for test and mock the behavior.

        // But since fromCurrentRequestUri() is static and hard to mock without PowerMockito,
        // let's test only the returned DeviceImage properties related to file info.

        DeviceImage result = controller.doUploadFile(id, file);

        assertEquals(Utils.transformSpaces(id), result.getPrefix());
        assertEquals("storedTest.png", result.getFileName());
        assertEquals("image/png", result.getContentType());
        assertEquals(file.getSize(), result.getFileSize());
        assertTrue(result.getFileDownloadUri().contains("storedTest.png"));
    }

    @Test
    void testCreateDevice_withoutFile() throws URISyntaxException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/device");
        CreateDeviceRequest createRequest = mock(CreateDeviceRequest.class);
        when(createRequest.getName()).thenReturn("deviceName");
        when(createRequest.getMacAddress()).thenReturn("00:11:22:33:44:55");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        Device deviceMock = mock(Device.class);
        var mcAddress = new MacAddress();
        mcAddress.setMacAddress("00:11:22:33:44:55");
        when(deviceMock.getMacAddress()).thenReturn(mcAddress);

        when(deviceMock.getVersion()).thenReturn(1L);

        when(deviceService.create(createRequest, null)).thenReturn(deviceMock);

        DeviceView deviceViewMock = mock(DeviceView.class);
        when(deviceViewMapper.toDeviceView(deviceMock)).thenReturn(deviceViewMock);

        // Build URI mock - since ServletUriComponentsBuilder uses static methods, hard to mock without PowerMockito
        // So just call the method and verify response entity properties

        ResponseEntity<DeviceView> response = controller.create(createRequest, null);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(deviceViewMock, response.getBody());
        assertEquals("\"1\"", response.getHeaders().getETag());

        // Location header URI should contain MAC address string
        assertTrue(response.getHeaders().getLocation().toString().contains("00:11:22:33:44:55"));
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testCreateDevice_withFile() throws URISyntaxException {
        // Mock HttpServletRequest
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/device");

        // Bind mock request to current thread for ServletUriComponentsBuilder
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);

        // rest of your test code
        CreateDeviceRequest createRequest = mock(CreateDeviceRequest.class);
        when(createRequest.getName()).thenReturn("deviceName");
        when(createRequest.getMacAddress()).thenReturn("00:11:22:33:44:55");

        MockMultipartFile file = new MockMultipartFile("file", "file.png", "image/png", "data".getBytes());

        when(fileStorageService.storeFile(anyString(), eq(file))).thenReturn("storedFile.png");

        // doUploadFile call inside create() method uses ServletUriComponentsBuilder so don't mock it here.
        DeviceImage deviceImage = new DeviceImage("deviceName", "storedFile.png",
                "http://localhost/download/storedFile.png", "image/png", file.getSize());

        // Mock DeviceService to accept any DeviceImage
        Device deviceMock = mock(Device.class);
        var mcAddress = new MacAddress();
        mcAddress.setMacAddress("00:11:22:33:44:55");
        when(deviceMock.getMacAddress()).thenReturn(mcAddress);
        when(deviceMock.getVersion()).thenReturn(1L);

        when(deviceService.create(any(), any())).thenReturn(deviceMock);

        DeviceView deviceViewMock = mock(DeviceView.class);
        when(deviceViewMapper.toDeviceView(deviceMock)).thenReturn(deviceViewMock);

        ResponseEntity<DeviceView> response = controller.create(createRequest, file);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(deviceViewMock, response.getBody());

        // Clean up ThreadLocal after test
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testUpsert_withIfMatchHeader() throws URISyntaxException {
        String macAddress = "00:11:22:33:44:55";

        EditDeviceRequest editRequest = mock(EditDeviceRequest.class);
        when(editRequest.getName()).thenReturn("updatedName");

        when(webRequest.getHeader("If-Match")).thenReturn("\"5\"");

        Device deviceMock = mock(Device.class);
        when(deviceMock.getVersion()).thenReturn(5L);

        DeviceView deviceViewMock = mock(DeviceView.class);
        when(deviceViewMapper.toDeviceView(deviceMock)).thenReturn(deviceViewMock);

        when(deviceService.update(eq(macAddress), eq(editRequest), any(), eq(5L))).thenReturn(deviceMock);

        ResponseEntity<DeviceView> response = controller.upsert(webRequest, macAddress, editRequest, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(deviceViewMock, response.getBody());
        assertEquals("\"5\"", response.getHeaders().getETag());
    }

    @Test
    void testDelete_withIfMatchHeader_success() {
        String macAddress = "00:11:22:33:44:55";
        when(webRequest.getHeader("If-Match")).thenReturn("\"10\"");

        when(deviceService.deleteDevice(macAddress, 10L)).thenReturn(1);

        ResponseEntity<DeviceView> response = controller.delete(webRequest, macAddress);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDelete_withIfMatchHeader_conflict() {
        String macAddress = "00:11:22:33:44:55";
        when(webRequest.getHeader("If-Match")).thenReturn("\"10\"");

        when(deviceService.deleteDevice(macAddress, 10L)).thenReturn(0);

        ResponseEntity<DeviceView> response = controller.delete(webRequest, macAddress);

        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    void testDownloadFile() {
        String fileName = "file.png";
        Resource resourceMock = mock(Resource.class);
        when(resourceMock.getFilename()).thenReturn(fileName);

        when(fileStorageService.loadFileAsResource(fileName)).thenReturn(resourceMock);

        ResponseEntity<Resource> response = controller.downloadFile(fileName, httpServletRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(resourceMock, response.getBody());
        assertEquals("attachment; filename=\"" + fileName + "\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
    }

}

