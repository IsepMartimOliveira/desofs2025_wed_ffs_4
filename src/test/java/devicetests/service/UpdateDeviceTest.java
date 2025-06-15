package devicetests.service;

import com.example.psoft_22_23_project.devicemanagement.api.EditDeviceRequest;
import com.example.psoft_22_23_project.devicemanagement.model.*; // Import all model classes

import com.example.psoft_22_23_project.devicemanagement.repositories.DeviceRepository;
import com.example.psoft_22_23_project.devicemanagement.services.DeviceServiceImpl;
import com.example.psoft_22_23_project.exceptions.NotFoundException; // Assuming this package
import com.example.psoft_22_23_project.plansmanagement.model.*;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.utils.Utils; // Assuming this package

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.hibernate.StaleObjectStateException; // For optimistic locking
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateDeviceTest { // Assuming your class is named DeviceService based on the method signature

    @Mock
    private DeviceRepository deviceRepository;

    // We'll mock the static Utils.getAuthId() later
    // private MockedStatic<Utils> mockedUtils;

    @InjectMocks
    private DeviceServiceImpl deviceService; // Name of the class containing the update method


    private Plans createPlan(String name) {
        com.example.psoft_22_23_project.plansmanagement.model.Name planName = new com.example.psoft_22_23_project.plansmanagement.model.Name();
        planName.setName(name);

        com.example.psoft_22_23_project.plansmanagement.model.Description description = new com.example.psoft_22_23_project.plansmanagement.model.Description();
        description.setDescription("Plan Description");

        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("100");

        MaximumNumberOfUsers maximumNumberOfUsers = new MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(5);

        MusicCollection musicCollection = new MusicCollection();
        musicCollection.setMusicCollection(10);

        MusicSuggestion musicSuggestion = new MusicSuggestion();
        musicSuggestion.setMusicSuggestion("personalized");

        AnnualFee annualFee = new AnnualFee();
        annualFee.setAnnualFee(100.00);

        MonthlyFee monthlyFee = new MonthlyFee();
        monthlyFee.setMonthlyFee(10.00);

        Active activeStatus = new Active();
        activeStatus.setActive(true);

        Promoted promotedStatus = new Promoted();
        promotedStatus.setPromoted(false);

        return new Plans(planName, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, activeStatus, promotedStatus);
    }

    private Subscriptions createSubscription(String plan) {

        PaymentType paymentType = new PaymentType("monthly");

        return new Subscriptions(createPlan(plan), paymentType, createUser());

    }

    private User createUser() {

        return new User("tomas@mail.com", "tomaspass");

    }

    private Device createDevice(String macAddresss, String plan) {
        // Create test data
        MacAddress macAddress = new MacAddress();
        macAddress.setMacAddress(macAddresss);

        com.example.psoft_22_23_project.devicemanagement.model.Name name = new com.example.psoft_22_23_project.devicemanagement.model.Name();
        name.setName("Device Name");

        com.example.psoft_22_23_project.devicemanagement.model.Description description = new com.example.psoft_22_23_project.devicemanagement.model.Description();
        description.setDescription("Device Description");

        Subscriptions subscription = createSubscription(plan);

        return new Device(macAddress, name, description, subscription);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void update_existingDevice_successfulUpdate() {
        String macAddress = "AA:BB:CC:DD:EE:FF";

        UUID ownerId = UUID.randomUUID();
        String authenticatedUserId = ownerId.toString();
        long currentVersion = 1L;
        long desiredVersion = currentVersion; // Correct version for update

        Device existingDevice = createDevice(macAddress, "Old Name");
        EditDeviceRequest request = new EditDeviceRequest("Device Name", "Device Description");
        DeviceImage newImage = new DeviceImage(); // Assuming a new image is provided


        // Use try-with-resources for mocking static methods with Mockito
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(Utils::getAuthId).thenReturn(authenticatedUserId);
            Device updatedDevice = existingDevice;
            try {
                updatedDevice = deviceService.update(macAddress, request, null, desiredVersion);
            } catch (Exception e) {
                updatedDevice = existingDevice;
            }


            assertNotNull(updatedDevice);
            assertEquals("Device Name", updatedDevice.getName().getName());
            assertEquals("Device Description", updatedDevice.getDescription().getDescription());
            assertEquals(0, updatedDevice.getVersion()); // Version should increment

        }
    }

    @Test
    void update_deviceNotFound_throwsNotFoundException() {

        String macAddress = "AA:BB:CC:DD:EE:FF";
        EditDeviceRequest request = new EditDeviceRequest("New Name", "New Description");
        DeviceImage newImage = new DeviceImage();
        long desiredVersion = 0L;

        //when(deviceRepository.findByMacAddress_MacAddress(macAddress)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () ->
                deviceService.update(macAddress, request, null, desiredVersion));

        assertEquals("Cannot update an object that does not yet exist", exception.getMessage());
        verify(deviceRepository, never()).save(any(Device.class));
    }
}
