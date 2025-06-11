package devicetests.service;


import com.example.psoft_22_23_project.devicemanagement.api.CreateDeviceRequest;
import com.example.psoft_22_23_project.devicemanagement.model.Description;
import com.example.psoft_22_23_project.devicemanagement.model.Device;
import com.example.psoft_22_23_project.devicemanagement.model.MacAddress;
import com.example.psoft_22_23_project.devicemanagement.model.Name;
import com.example.psoft_22_23_project.devicemanagement.services.CreateDeviceMapper;
import com.example.psoft_22_23_project.plansmanagement.model.*;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class CreateDeviceMapperTest {

    // Get the MapStruct generated implementation.
    // MapStruct generates an implementation class with the name of the interface/abstract class + "Impl".
    // Make sure this class is accessible in your test classpath.
    private CreateDeviceMapper createDeviceMapper = Mappers.getMapper(CreateDeviceMapper.class);
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
    private User createUser(){

        return new User("tomas@mail.com", "tomaspass");

    }
    private Subscriptions createSubscription(String plan){

        PaymentType paymentType = new PaymentType("monthly");

        return new Subscriptions(createPlan(plan), paymentType, createUser());

    }
    @Test
    void testCreateDeviceFromRequest_AllFieldsPopulated() {
        // Given
        String testMacAddress = "00:1A:2B:3C:4D:5E";
        String testName = "My Smart Device";
        String testDescription = "A device for home automation.";

        CreateDeviceRequest request = new CreateDeviceRequest(testMacAddress, testName, testDescription);

        Subscriptions subscription = createSubscription("Premium");
        // Assuming Subscriptions has an ID
        // subscription.setId(1L);
        // If Subscriptions needs more fields for the mapping, populate them here

        // When
        Device device = createDeviceMapper.create(subscription, request);

        // Then
        assertNotNull(device, "Device should not be null");
        assertNotNull(device.getMacAddress(), "MacAddress should not be null");
        assertNotNull(device.getName(), "Name should not be null");
        assertNotNull(device.getDescription(), "Description should not be null");
        assertNotNull(device.getSubscription(), "Subscription should be set");

        assertEquals(testMacAddress, device.getMacAddress().getMacAddress(), "MAC Address should match");
        assertEquals(testName, device.getName().getName(), "Name should match");
        assertEquals(testDescription, device.getDescription().getDescription(), "Description should match");
        assertEquals(subscription, device.getSubscription(), "Subscription object should be the same");
    }

    @Test
    void testCreateDeviceFromRequest_NullRequestFields() {
        // Given
        CreateDeviceRequest request = new CreateDeviceRequest(null, null, null); // All fields are null

        Subscriptions subscription = createSubscription("Basic");
        // subscription.setId(2L);

        // When
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            createDeviceMapper.create(subscription, request);
        });


        assertEquals("'macAddress' is a mandatory attribute of Device", thrown.getMessage());

    }


    @Test
    void testCreateDeviceFromRequest_NullSubscription() {
        // Given
        String testMacAddress = "00:1A:2B:3C:4D:5F";
        String testName = "Another Device";
        String testDescription = "Just another device.";

        CreateDeviceRequest request = new CreateDeviceRequest(testMacAddress, testName, testDescription);

        // When
        Device device = createDeviceMapper.create(null, request);

        // Then
        assertNotNull(device, "Device should not be null");
        assertNotNull(device.getMacAddress(), "MacAddress should not be null");
        assertNotNull(device.getName(), "Name should not be null");
        assertNotNull(device.getDescription(), "Description should not be null");
        assertNull(device.getSubscription(), "Subscription should be null"); // Expect subscription to be null

        assertEquals(testMacAddress, device.getMacAddress().getMacAddress(), "MAC Address should match");
        assertEquals(testName, device.getName().getName(), "Name should match");
        assertEquals(testDescription, device.getDescription().getDescription(), "Description should match");
    }

}