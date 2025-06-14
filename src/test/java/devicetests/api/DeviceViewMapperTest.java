package devicetests.api;


import com.example.psoft_22_23_project.devicemanagement.api.DeviceView;
import com.example.psoft_22_23_project.devicemanagement.api.DeviceViewMapper;
import com.example.psoft_22_23_project.devicemanagement.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DeviceViewMapperTest {

    private DeviceViewMapper mapper;

    @BeforeEach
    void setUp() {
        // Get the MapStruct generated implementation
        mapper = Mappers.getMapper(DeviceViewMapper.class);
    }

    @Test
    void testToDeviceView() {
        // Setup nested objects as per mapping
        MacAddress macAddress = new MacAddress();
        macAddress.setMacAddress("00:11:22:33:44:55");

        Name name = new Name();
        name.setName("DeviceName");

        Description description = new Description();
        description.setDescription("A test device");

        DeviceImage deviceImage = new DeviceImage();
        deviceImage.setFileName("image.png");

        Device device = new Device(macAddress, name, description, null);

        device.setDeviceImage(deviceImage);

        DeviceView deviceView = mapper.toDeviceView(device);

        assertNotNull(deviceView);
        assertEquals("00:11:22:33:44:55", deviceView.getMacAddress());
        assertEquals("DeviceName", deviceView.getName());
        assertEquals("A test device", deviceView.getDescription());
        assertEquals("image.png", deviceView.getFileName());
    }

    @Test
    void testToDevicesView() {
        MacAddress macAddress = new MacAddress();
        macAddress.setMacAddress("00:11:22:33:44:55");

        Device device = new Device(macAddress,null,null,null);


        Iterable<Device> devices = Collections.singletonList(device);
        Iterable<DeviceView> deviceViews = mapper.toDevicesView(devices);

        assertNotNull(deviceViews);
        assertTrue(deviceViews.iterator().hasNext());
        assertEquals("00:11:22:33:44:55", deviceViews.iterator().next().getMacAddress());
    }

    @Test
    void testMapOptInt() {
        assertEquals(Integer.valueOf(5), mapper.mapOptInt(Optional.of(5)));
        assertNull(mapper.mapOptInt(Optional.empty()));
    }

    @Test
    void testMapOptLong() {
        assertEquals(Long.valueOf(10L), mapper.mapOptLong(Optional.of(10L)));
        assertNull(mapper.mapOptLong(Optional.empty()));
    }

    @Test
    void testMapOptString() {
        assertEquals("test", mapper.mapOptString(Optional.of("test")));
        assertNull(mapper.mapOptString(Optional.empty()));
    }
}
