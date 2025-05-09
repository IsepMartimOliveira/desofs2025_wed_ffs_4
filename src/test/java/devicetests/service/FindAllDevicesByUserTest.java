package devicetests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.psoft_22_23_project.devicemanagement.model.Device;
import com.example.psoft_22_23_project.devicemanagement.model.MacAddress;
import com.example.psoft_22_23_project.devicemanagement.repositories.DeviceRepository;
import com.example.psoft_22_23_project.devicemanagement.services.DeviceServiceImpl;
import com.example.psoft_22_23_project.plansmanagement.model.*;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.subscriptionsmanagement.repositories.SubscriptionsRepository;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.EntityNotFoundException;

public class FindAllDevicesByUserTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private SubscriptionsRepository subscriptionsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

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

        return new Subscriptions(createPlan(plan), paymentType, createUser());

    }

    private User createUser(){

        return new User("tomas@mail.com", "tomaspass");

    }

    private Device createDevice(String macAddresss, String plan){
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

    @Test
    public void testFindAllDevicesByUser_Success() {
        // Mock the necessary objects and data
        String userIdString = "1";
        User user = createUser();

        Subscriptions subscription = createSubscription("Plan Name");

        List<Device> devices = new ArrayList<>();
        devices.add(createDevice("00:11:22:33:44:55", "Plan Name"));
        devices.add(createDevice("00:11:22:33:44:56", "Plan Name"));

        when(userRepository.findById(Long.valueOf(userIdString))).thenReturn(Optional.of(user));
        when(subscriptionsRepository.findByUser(user)).thenReturn(Optional.of(subscription));
        when(deviceRepository.findAllBySubscription(subscription)).thenReturn(devices);


        // Mock the authentication
        Authentication authentication = new TestingAuthenticationToken("1", "password");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        // Call the method being tested
        List<Device> result = deviceService.findAllDevicesByUser();

        // Assert the results
        assertNotNull(result);
        assertEquals(2, result.size());
        // Add additional assertions as per your requirements

        // Verify that the mock methods were called as expected
        verify(userRepository).findById(Long.valueOf(userIdString));
        verify(subscriptionsRepository).findByUser(user);
        verify(deviceRepository).findAllBySubscription(subscription);
    }

    @Test
    public void testFindAllDevicesByUser_UserNotFound() {
        String userIdString = "1";

        when(userRepository.findById(Long.valueOf(userIdString))).thenReturn(Optional.empty());

        // Mock the authentication
        Authentication authentication = new TestingAuthenticationToken("1", "password");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        // Call the method being tested and assert that it throws the expected exception
        assertThrows(EntityNotFoundException.class, () -> deviceService.findAllDevicesByUser());

        // Verify that the mock method was called as expected
        verify(userRepository).findById(Long.valueOf(userIdString));
        verifyNoInteractions(subscriptionsRepository, deviceRepository);
    }

    @Test
    public void testFindAllDevicesByUser_SubscriptionNotFound() {
        String userIdString = "1";
        User user = createUser();

        // Mock the authentication
        Authentication authentication = new TestingAuthenticationToken("1", "password");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        when(userRepository.findById(Long.valueOf(userIdString))).thenReturn(Optional.of(user));
        when(subscriptionsRepository.findByUser(user)).thenReturn(Optional.empty());

        // Call the method being tested and assert that it throws the expected exception
        assertThrows(EntityNotFoundException.class, () -> deviceService.findAllDevicesByUser());

        // Verify that the mock methods were called as expected
        verify(userRepository).findById(Long.valueOf(userIdString));
        verify(subscriptionsRepository).findByUser(user);
        verifyNoInteractions(deviceRepository);
    }

}

