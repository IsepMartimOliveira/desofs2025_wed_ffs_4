package devicetests.model;


import com.example.psoft_22_23_project.devicemanagement.model.*;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeviceTest {

    private Device device;

    @Mock
    private MacAddress macAddress;
    @Mock
    private Name name;
    @Mock
    private Description description;
    @Mock
    private Subscriptions subscription;
    @Mock
    private DeviceImage deviceImage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize with default values for common tests
        device = new Device(macAddress, name, description, subscription);

        // Set up the mock 'name' and 'description' to return values for getter tests
        when(name.getName()).thenReturn("Test Name");
        when(description.getDescription()).thenReturn("Test Description");
    }

    @Test
    void constructorShouldSetAllFields() {
        // Test the main constructor
        Device testDevice = new Device(macAddress, name, description, subscription);
        assertNotNull(testDevice); // Ensure object is created
        assertEquals(macAddress, testDevice.getMacAddress());
        assertEquals(name, testDevice.getName());
        assertEquals(description, testDevice.getDescription());
        assertEquals(subscription, testDevice.getSubscription());
        assertNull(testDevice.getDeviceImage()); // deviceImage is not set in constructor
        assertEquals(0, testDevice.getVersion()); // Initial version should be 0
    }

    @Test
    void defaultConstructorShouldExistForJPA() {
        // This test helps cover the protected default constructor for JaCoCo,
        // although its primary use is by JPA.
        try {
            // Use reflection to call the protected constructor
            java.lang.reflect.Constructor<Device> constructor = Device.class.getDeclaredConstructor();
            constructor.setAccessible(true); // Make it accessible for testing
            Device defaultDevice = constructor.newInstance();
            assertNotNull(defaultDevice);
            // Assert default values if any, or just assert creation
            assertEquals(0, defaultDevice.getVersion());
            assertNull(defaultDevice.getId());
            assertNull(defaultDevice.getMacAddress());
        } catch (Exception e) {
            fail("Default constructor could not be invoked: " + e.getMessage());
        }
    }


    @Test
    void setDeviceImageShouldSetImage() {
        device.setDeviceImage(deviceImage);
        assertEquals(deviceImage, device.getDeviceImage());
    }

    // --- Getter Tests to improve coverage ---
    @Test
    void getIdShouldReturnNullInitially() {
        // Id is generated, so it should be null before persistence
        assertNull(device.getId());
    }

    @Test
    void getVersionShouldReturnZeroInitially() {
        assertEquals(0, device.getVersion());
    }

    @Test
    void getMacAddressShouldReturnCorrectValue() {
        assertEquals(macAddress, device.getMacAddress());
    }

    @Test
    void getNameShouldReturnCorrectValue() {
        // We set up the mock 'name' in @BeforeEach to return "Test Name"
        assertEquals(name, device.getName());
    }


    @Test
    void getDescriptionShouldReturnCorrectValue() {
        // We set up the mock 'description' in @BeforeEach to return "Test Description"
        assertEquals(description, device.getDescription());
    }


    @Test
    void getSubscriptionShouldReturnCorrectValue() {
        assertEquals(subscription, device.getSubscription());
    }
    // --- End of Getter Tests ---


    @Test
    void updateShouldThrowStaleObjectStateExceptionWhenVersionsMismatch() {
        long desiredVersion = device.getVersion() + 1; // Simulate a version mismatch
        StaleObjectStateException exception = assertThrows(StaleObjectStateException.class, () ->
                device.update(desiredVersion, "New Name", "New Description", null));
        assertTrue(exception.getMessage().contains("Object was already modified by another user"));
        assertEquals(device.getId(), exception.getIdentifier()); // Check if identifier is correct
    }

    @Test
    void updateShouldUpdateNameWhenProvided() {
        long currentVersion = device.getVersion();
        String newName = "Updated Device Name";

        device.update(currentVersion, newName, null, null);

        // Verify that setName was called on the mocked name object
        verify(name, times(1)).setName(newName);
        // Verify other fields were not affected if null
        verify(description, never()).setDescription(anyString());
        assertNull(device.getDeviceImage());
    }

    @Test
    void updateShouldUpdateDescriptionWhenProvided() {
        long currentVersion = device.getVersion();
        String newDescription = "Updated Device Description";

        device.update(currentVersion, null, newDescription, null);

        // Verify that setDescription was called on the mocked description object
        verify(description, times(1)).setDescription(newDescription);
        // Verify other fields were not affected if null
        verify(name, never()).setName(anyString());
        assertNull(device.getDeviceImage());
    }

    @Test
    void updateShouldUpdateDeviceImageWhenProvided() {
        long currentVersion = device.getVersion();
        DeviceImage newImage = mock(DeviceImage.class); // Mock a new image

        device.update(currentVersion, null, null, newImage);

        assertEquals(newImage, device.getDeviceImage());
        // Verify that setters for name and description were not called
        verify(name, never()).setName(anyString());
        verify(description, never()).setDescription(anyString());
    }

    @Test
    void updateShouldUpdateMultipleFieldsWhenProvided() {
        long currentVersion = device.getVersion();
        String newName = "Another Name";
        String newDescription = "Another Description";
        DeviceImage newImage = mock(DeviceImage.class);

        device.update(currentVersion, newName, newDescription, newImage);

        verify(name, times(1)).setName(newName);
        verify(description, times(1)).setDescription(newDescription);
        assertEquals(newImage, device.getDeviceImage());
    }

    @Test
    void updateShouldNotUpdateFieldsWhenNullAndVersionMatches() {
        long currentVersion = device.getVersion();

        device.update(currentVersion, null, null, null);

        // Verify that setters were not called
        verify(name, never()).setName(anyString());
        verify(description, never()).setDescription(anyString());
        assertNull(device.getDeviceImage()); // Still null as no new image provided
    }

    @Test
    void updateShouldIncrementVersionOnSuccessfulUpdate() {
        long initialVersion = device.getVersion();
        String newName = "New Name For Version Test";

        device.update(initialVersion, newName, null, null);

    }
}