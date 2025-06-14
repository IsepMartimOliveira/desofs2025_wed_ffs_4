package devicetests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.psoft_22_23_project.devicemanagement.model.Description;
import com.example.psoft_22_23_project.devicemanagement.model.Device;
import com.example.psoft_22_23_project.devicemanagement.model.MacAddress;
import com.example.psoft_22_23_project.devicemanagement.model.Name;
import com.example.psoft_22_23_project.devicemanagement.repositories.DeviceRepository;
import com.example.psoft_22_23_project.devicemanagement.services.DeviceServiceImpl;
import com.example.psoft_22_23_project.plansmanagement.model.*;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.utils.Utils;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

public class DeleteDeviceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    private final String userIdString = "1";
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

    private Subscriptions createSubscription(String plan){

        PaymentType paymentType = new PaymentType("monthly");

        return new Subscriptions(createPlan(plan), paymentType, createUser("1"));

    }


    private User createUser(String idValue) {
        User user = new User("testuser@mail.com", "securePassword123", "testuser@mail.com", 123456789, 30);

        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, Long.valueOf(idValue));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set User ID via reflection", e);
        }

        return user;
    }
    private Device createDeviceForUser(String userId) {


        Subscriptions subscription = createSubscription("basic");


        MacAddress macAddress = new MacAddress();
        macAddress.setMacAddress("AA:BB:CC:DD:EE:FF");

        Name name = new Name();
        name.setName("Test Device");

        Description description = new Description();
        description.setDescription("Test Device Description");

        Device device = new Device(macAddress, name, description, subscription);

        // The id and version need to be set via reflection or a setter if you add one,
        // because your id is private and no setter exists in the class.
        // For testing, you can use reflection or a constructor with id/version if available.

        // Using reflection to set id and version:
        try {
            java.lang.reflect.Field idField = Device.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(device, 100L);

            java.lang.reflect.Field versionField = Device.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.setLong(device, 5L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return device;
    }
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeleteDevice_Success() {
        Device device = createDeviceForUser(userIdString);
        String macAddress = "AA:BB:CC:DD:EE:FF";
        long desiredVersion = 5L;

        when(deviceRepository.findByMacAddress_MacAddress(macAddress)).thenReturn(Optional.of(device));
        when(deviceRepository.deleteByIdIfMatches(device.getId(), desiredVersion)).thenReturn(1);

        try (MockedStatic<Utils> utilsMockedStatic = mockStatic(Utils.class)) {
            utilsMockedStatic.when(Utils::getAuthId).thenReturn(userIdString);

            int result = deviceService.deleteDevice(macAddress, desiredVersion);

            assertEquals(1, result);

            verify(deviceRepository).findByMacAddress_MacAddress(macAddress);
            verify(deviceRepository).deleteByIdIfMatches(device.getId(), desiredVersion);
        }
    }

    @Test
    public void testDeleteDevice_DeviceNotFound() {
        String macAddress = "AA:BB:CC:DD:EE:FF";
        long desiredVersion = 1L;

        when(deviceRepository.findByMacAddress_MacAddress(macAddress)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            deviceService.deleteDevice(macAddress, desiredVersion);
        });

        assertEquals("Device not found with macAddress " + macAddress, thrown.getMessage());

        verify(deviceRepository).findByMacAddress_MacAddress(macAddress);
        verifyNoMoreInteractions(deviceRepository);
    }



    @Test
    public void testDeleteDevice_VersionMismatch() {
        Device device = createDeviceForUser(userIdString);
        String macAddress = "AA:BB:CC:DD:EE:FF";
        long desiredVersion = 999L; // Assume wrong version

        when(deviceRepository.findByMacAddress_MacAddress(macAddress)).thenReturn(Optional.of(device));
        when(deviceRepository.deleteByIdIfMatches(device.getId(), desiredVersion)).thenReturn(0); // no rows deleted

        try (MockedStatic<Utils> utilsMockedStatic = mockStatic(Utils.class)) {
            utilsMockedStatic.when(Utils::getAuthId).thenReturn(userIdString);

            int result = deviceService.deleteDevice(macAddress, desiredVersion);

            assertEquals(0, result);

            verify(deviceRepository).findByMacAddress_MacAddress(macAddress);
            verify(deviceRepository).deleteByIdIfMatches(device.getId(), desiredVersion);
        }
    }
}
