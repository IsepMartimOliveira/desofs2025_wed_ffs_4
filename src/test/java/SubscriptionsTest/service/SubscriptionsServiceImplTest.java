package SubscriptionsTest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.psoft_22_23_project.devicemanagement.repositories.DeviceRepository;
import com.example.psoft_22_23_project.plansmanagement.model.Active;
import com.example.psoft_22_23_project.plansmanagement.model.AnnualFee;
import com.example.psoft_22_23_project.plansmanagement.model.Description;
import com.example.psoft_22_23_project.plansmanagement.model.MaximumNumberOfUsers;
import com.example.psoft_22_23_project.plansmanagement.model.MonthlyFee;
import com.example.psoft_22_23_project.plansmanagement.model.MusicCollection;
import com.example.psoft_22_23_project.plansmanagement.model.MusicSuggestion;
import com.example.psoft_22_23_project.plansmanagement.model.Name;
import com.example.psoft_22_23_project.plansmanagement.model.NumberOfMinutes;
import com.example.psoft_22_23_project.plansmanagement.model.Plans;
import com.example.psoft_22_23_project.plansmanagement.model.Promoted;
import com.example.psoft_22_23_project.plansmanagement.repositories.PlansRepository;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.CreateSubscriptionsRequest;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PlansDetails;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.subscriptionsmanagement.repositories.SubscriptionsRepository;
import com.example.psoft_22_23_project.subscriptionsmanagement.services.CreateSubscriptionsMapper;
import com.example.psoft_22_23_project.subscriptionsmanagement.services.SubscriptionsServiceImpl;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class SubscriptionsServiceImplTest {

    @Mock
    private SubscriptionsRepository subscriptionsRepository;

    @Mock
    private PlansRepository plansRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreateSubscriptionsMapper createSubscriptionsMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SubscriptionsServiceImpl subscriptionsService;

    private User testUser;
    private Plans testPlan;
    private Subscriptions testSubscription;
    private CreateSubscriptionsRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testPlan = createTestPlan("Test Plan");
        testSubscription = createTestSubscription();
        createRequest = new CreateSubscriptionsRequest("Test Plan", "monthly");
    }

    private User createTestUser() {
        return new User("test@example.com", "password");
    }

    private Plans createTestPlan(String name) {
        Name planName = new Name();
        planName.setName(name);

        Description description = new Description();
        description.setDescription("Test Plan Description");

        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("120");

        MaximumNumberOfUsers maximumNumberOfUsers = new MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(5);

        MusicCollection musicCollection = new MusicCollection();
        musicCollection.setMusicCollection(15);

        MusicSuggestion musicSuggestion = new MusicSuggestion();
        musicSuggestion.setMusicSuggestion("automatic");

        AnnualFee annualFee = new AnnualFee();
        annualFee.setAnnualFee(120.00);

        MonthlyFee monthlyFee = new MonthlyFee();
        monthlyFee.setMonthlyFee(12.00);

        Active activeStatus = new Active();
        activeStatus.setActive(true);

        Promoted promotedStatus = new Promoted();
        promotedStatus.setPromoted(false);

        return new Plans(planName, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, activeStatus, promotedStatus);
    }

    private Subscriptions createTestSubscription() {
        PaymentType paymentType = new PaymentType("monthly");
        return new Subscriptions(testPlan, paymentType, testUser);
    }

    private void setupSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("1");
        SecurityContextHolder.setContext(securityContext);
    }

    private Subscriptions createTestSubscriptionWithStartDate(String startDateString) {
        PaymentType paymentType = new PaymentType("monthly");
        Subscriptions subscription = new Subscriptions(testPlan, paymentType, testUser);
        
        try {
            java.lang.reflect.Field startDateField = subscription.getStartDate().getClass().getDeclaredField("startDate");
            startDateField.setAccessible(true);
            startDateField.set(subscription.getStartDate(), startDateString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set start date", e);
        }
        
        return subscription;
    }

    @Test
    void testFindAll_ShouldReturnAllSubscriptions() {

        List<Subscriptions> expectedSubscriptions = new ArrayList<>();
        expectedSubscriptions.add(testSubscription);
        expectedSubscriptions.add(createTestSubscription());
        
        when(subscriptionsRepository.findAll()).thenReturn(expectedSubscriptions);

        Iterable<Subscriptions> result = subscriptionsService.findAll();

        assertNotNull(result);
        assertEquals(expectedSubscriptions, result);
        verify(subscriptionsRepository).findAll();
    }

    @Test
    void testCreate_ValidRequest_ShouldCreateSubscription() {

        setupSecurityContext();
        
        when(plansRepository.findByActive_ActiveAndName_Name(true, "Test Plan"))
                .thenReturn(Optional.of(testPlan));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.empty());
        when(createSubscriptionsMapper.create(testUser, testPlan, createRequest))
                .thenReturn(testSubscription);
        when(subscriptionsRepository.save(testSubscription)).thenReturn(testSubscription);

        Subscriptions result = subscriptionsService.create(createRequest);

        assertNotNull(result);
        assertEquals(testSubscription, result);
        verify(plansRepository).findByActive_ActiveAndName_Name(true, "Test Plan");
        verify(userRepository).findById(1L);
        verify(subscriptionsRepository).save(testSubscription);
    }

    @Test
    void testCreate_PlanNotFound_ShouldThrowEntityNotFoundException() {

        when(plansRepository.findByActive_ActiveAndName_Name(true, "Test Plan"))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subscriptionsService.create(createRequest));
        assertEquals("Plan not found with name Test Plan", exception.getMessage());
    }

    @Test
    void testCreate_UserNotFound_ShouldThrowUsernameNotFoundException() {

        setupSecurityContext();
        
        when(plansRepository.findByActive_ActiveAndName_Name(true, "Test Plan"))
                .thenReturn(Optional.of(testPlan));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> subscriptionsService.create(createRequest));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testCreate_UserHasActiveSubscription_ShouldThrowIllegalArgumentException() {

        setupSecurityContext();
        
        when(plansRepository.findByActive_ActiveAndName_Name(true, "Test Plan"))
                .thenReturn(Optional.of(testPlan));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(testSubscription));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> subscriptionsService.create(createRequest));
        assertEquals("You need to let your active subscription end in order to subscribe", exception.getMessage());
    }

    @Test
    void testCancelSubscription_UserNotFound_ShouldThrowEntityNotFoundException() {

        long version = 1L;
        setupSecurityContext();
        
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subscriptionsService.cancelSubscription(version));
        assertEquals("User not found with ID 1", exception.getMessage());
    }

    @Test
    void testCancelSubscription_NoActiveSubscription_ShouldThrowEntityNotFoundException() {

        long version = 1L;
        setupSecurityContext();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subscriptionsService.cancelSubscription(version));
        assertEquals("No subscriptions associated with this user", exception.getMessage());
    }

    @Test
    void testCancelSubscription_MonthlySubscriptionSameMonth_ShouldCancelSuccessfully() {

        long version = 0L;
        setupSecurityContext();
        
        Subscriptions monthlySubscription = createTestSubscriptionWithStartDate("2025-06-01");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(monthlySubscription));
        when(subscriptionsRepository.save(monthlySubscription)).thenReturn(monthlySubscription);

        Subscriptions result = subscriptionsService.cancelSubscription(version);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(subscriptionsRepository).findByActiveStatus_ActiveAndUser(true, testUser);
        verify(subscriptionsRepository).save(monthlySubscription);
    }

    @Test
    void testCancelSubscription_MonthlySubscriptionPreviousMonth_ShouldCancelSuccessfully() {

        long version = 0L;
        setupSecurityContext();
        
        Subscriptions monthlySubscription = createTestSubscriptionWithStartDate("2025-05-15");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(monthlySubscription));
        when(subscriptionsRepository.save(monthlySubscription)).thenReturn(monthlySubscription);

        Subscriptions result = subscriptionsService.cancelSubscription(version);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(subscriptionsRepository).findByActiveStatus_ActiveAndUser(true, testUser);
        verify(subscriptionsRepository).save(monthlySubscription);
    }

    @Test
    void testCancelSubscription_MonthlySubscriptionDifferentYear_ShouldCancelSuccessfully() {

        long version = 0L;
        setupSecurityContext();
        
        Subscriptions monthlySubscription = createTestSubscriptionWithStartDate("2024-06-01");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(monthlySubscription));
        when(subscriptionsRepository.save(monthlySubscription)).thenReturn(monthlySubscription);

        Subscriptions result = subscriptionsService.cancelSubscription(version);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(subscriptionsRepository).findByActiveStatus_ActiveAndUser(true, testUser);
        verify(subscriptionsRepository).save(monthlySubscription);
    }

    @Test
    void testCancelSubscription_MonthlySubscriptionEarlierDay_ShouldCalculateCorrectEndDate() {

        long version = 0L;
        setupSecurityContext();
        
        Subscriptions monthlySubscription = createTestSubscriptionWithStartDate("2025-05-05");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(monthlySubscription));
        when(subscriptionsRepository.save(monthlySubscription)).thenReturn(monthlySubscription);

        Subscriptions result = subscriptionsService.cancelSubscription(version);

        assertNotNull(result);
        verify(subscriptionsRepository).save(monthlySubscription);
    }

    @Test
    void testCancelSubscription_MonthlySubscriptionPreviousYear_ShouldCalculateCorrectEndDate() {

        long version = 0L;
        setupSecurityContext();
        
        Subscriptions monthlySubscription = createTestSubscriptionWithStartDate("2024-04-15");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(monthlySubscription));
        when(subscriptionsRepository.save(monthlySubscription)).thenReturn(monthlySubscription);

        Subscriptions result = subscriptionsService.cancelSubscription(version);

        assertNotNull(result);
        verify(subscriptionsRepository).save(monthlySubscription);
    }

    @Test
    void testCancelSubscription_MonthlySubscriptionLaterDaySameMonth_ShouldCalculateCorrectEndDate() {

        long version = 0L;
        setupSecurityContext();
        
        Subscriptions monthlySubscription = createTestSubscriptionWithStartDate("2025-06-15");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(monthlySubscription));
        when(subscriptionsRepository.save(monthlySubscription)).thenReturn(monthlySubscription);

        Subscriptions result = subscriptionsService.cancelSubscription(version);

        assertNotNull(result);
        verify(subscriptionsRepository).save(monthlySubscription);
    }

    @Test
    void testCancelSubscription_SameMonthDifferentYear_ShouldCalculateCorrectEndDate() {

        long version = 0L;
        setupSecurityContext();
        
        Subscriptions monthlySubscription = createTestSubscriptionWithStartDate("2024-06-05");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(monthlySubscription));
        when(subscriptionsRepository.save(monthlySubscription)).thenReturn(monthlySubscription);

        Subscriptions result = subscriptionsService.cancelSubscription(version);

        assertNotNull(result);
        verify(subscriptionsRepository).save(monthlySubscription);
    }

    @Test
    void testRenewAnualSubscription_MonthlySubscription_ShouldThrowIllegalArgumentException() {

        long version = 1L;
        setupSecurityContext();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(testSubscription));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> subscriptionsService.renewAnualSubscription(version));
        assertEquals("You can not renew a monthly subscription", exception.getMessage());
    }

    @Test
    void testChangePlan_TooManyDevices_ShouldThrowIllegalArgumentException() {

        long version = 1L;
        String newPlanName = "New Plan";
        Plans newPlan = createTestPlan(newPlanName);
        
        setupSecurityContext();

        when(plansRepository.findByActive_ActiveAndName_Name(true, newPlanName))
                .thenReturn(Optional.of(newPlan));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(testSubscription));
        when(deviceRepository.countBySubscription(testSubscription)).thenReturn(10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> subscriptionsService.changePlan(version, newPlanName));
        assertEquals("The plan you are trying to change has a number of devices lower than the actual number of active devices. Please remove them and try again", exception.getMessage());
    }

    @Test
    void testChangePlan_SamePlan_ShouldThrowIllegalArgumentException() {

        long version = 1L;
        String samePlanName = "Test Plan";
        
        setupSecurityContext();

        when(plansRepository.findByActive_ActiveAndName_Name(true, samePlanName))
                .thenReturn(Optional.of(testPlan));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(testSubscription));
        when(deviceRepository.countBySubscription(testSubscription)).thenReturn(2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> subscriptionsService.changePlan(version, samePlanName));
        assertEquals("You are already subscribed to this plan", exception.getMessage());
    }

    @Test
    void testMigrateAllToPlan_ValidRequest_ShouldMigrateSuccessfully() {

        long desiredVersion = 0L;
        String actualPlan = "Basic Plan";
        String newPlan = "Premium Plan";
        
        Plans oldPlan = createTestPlan(actualPlan);
        Plans newPlan_obj = createTestPlan(newPlan);
        
        List<Subscriptions> subscriptions = new ArrayList<>();
        PaymentType paymentType = new PaymentType();
        paymentType.setPaymentType("monthly");
        
        Subscriptions sub1 = new Subscriptions(oldPlan, paymentType, testUser);
        Subscriptions sub2 = new Subscriptions(oldPlan, paymentType, testUser);
        subscriptions.add(sub1);
        subscriptions.add(sub2);

        when(plansRepository.findByActive_ActiveAndName_Name(true, actualPlan))
                .thenReturn(Optional.of(oldPlan));
        when(plansRepository.findByActive_ActiveAndName_Name(true, newPlan))
                .thenReturn(Optional.of(newPlan_obj));
        when(subscriptionsRepository.findAllByPlanAndActiveStatus_Active(oldPlan, true))
                .thenReturn(subscriptions);
        when(deviceRepository.countBySubscription(sub1)).thenReturn(2);
        when(deviceRepository.countBySubscription(sub2)).thenReturn(1);
        when(subscriptionsRepository.save(sub1)).thenReturn(sub1);
        when(subscriptionsRepository.save(sub2)).thenReturn(sub2);

        assertDoesNotThrow(() -> subscriptionsService.migrateAllToPlan(desiredVersion, actualPlan, newPlan));

        verify(plansRepository).findByActive_ActiveAndName_Name(true, actualPlan);
        verify(plansRepository).findByActive_ActiveAndName_Name(true, newPlan);
        verify(subscriptionsRepository).findAllByPlanAndActiveStatus_Active(oldPlan, true);
        verify(deviceRepository).countBySubscription(sub1);
        verify(deviceRepository).countBySubscription(sub2);
        verify(subscriptionsRepository).save(sub1);
        verify(subscriptionsRepository).save(sub2);
    }

    @Test
    void testMigrateAllToPlan_SamePlanNames_ShouldThrowDuplicateRequestException() {

        long desiredVersion = 1L;
        String planName = "Basic Plan";

        DuplicateRequestException exception = assertThrows(DuplicateRequestException.class,
                () -> subscriptionsService.migrateAllToPlan(desiredVersion, planName, planName));
        assertEquals("You are trying to migrate all the users to their actual plan", exception.getMessage());
    }

    @Test
    void testMigrateAllToPlan_OldPlanNotFound_ShouldThrowEntityNotFoundException() {

        long desiredVersion = 1L;
        String actualPlan = "Nonexistent Plan";
        String newPlan = "Premium Plan";

        when(plansRepository.findByActive_ActiveAndName_Name(true, actualPlan))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subscriptionsService.migrateAllToPlan(desiredVersion, actualPlan, newPlan));
        assertEquals("Plan not found with name " + actualPlan, exception.getMessage());
    }

    @Test
    void testMigrateAllToPlan_NewPlanNotFound_ShouldThrowEntityNotFoundException() {

        long desiredVersion = 1L;
        String actualPlan = "Basic Plan";
        String newPlan = "Nonexistent Plan";
        
        Plans oldPlan = createTestPlan(actualPlan);

        when(plansRepository.findByActive_ActiveAndName_Name(true, actualPlan))
                .thenReturn(Optional.of(oldPlan));
        when(plansRepository.findByActive_ActiveAndName_Name(true, newPlan))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subscriptionsService.migrateAllToPlan(desiredVersion, actualPlan, newPlan));
        assertEquals("Plan not found with name " + newPlan, exception.getMessage());
    }

    @Test
    void testMigrateAllToPlan_NoSubscriptions_ShouldThrowEntityNotFoundException() {

        long desiredVersion = 1L;
        String actualPlan = "Basic Plan";
        String newPlan = "Premium Plan";
        
        Plans oldPlan = createTestPlan(actualPlan);
        Plans newPlan_obj = createTestPlan(newPlan);

        when(plansRepository.findByActive_ActiveAndName_Name(true, actualPlan))
                .thenReturn(Optional.of(oldPlan));
        when(plansRepository.findByActive_ActiveAndName_Name(true, newPlan))
                .thenReturn(Optional.of(newPlan_obj));
        when(subscriptionsRepository.findAllByPlanAndActiveStatus_Active(oldPlan, true))
                .thenReturn(new ArrayList<>());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subscriptionsService.migrateAllToPlan(desiredVersion, actualPlan, newPlan));
        assertEquals("No subscriptions associated with plan " + actualPlan, exception.getMessage());
    }

    @Test
    void testMigrateAllToPlan_TooManyDevices_ShouldThrowIllegalArgumentException() {

        long desiredVersion = 1L;
        String actualPlan = "Premium Plan";
        String newPlan = "Basic Plan";
        
        Plans oldPlan = createTestPlan(actualPlan);

        Plans newPlan_obj = createTestPlanWithDeviceLimit(newPlan, 2);
        
        PaymentType paymentType = new PaymentType();
        paymentType.setPaymentType("monthly");
        
        List<Subscriptions> subscriptions = new ArrayList<>();
        Subscriptions subscription = new Subscriptions(oldPlan, paymentType, testUser);
        subscriptions.add(subscription);

        when(plansRepository.findByActive_ActiveAndName_Name(true, actualPlan))
                .thenReturn(Optional.of(oldPlan));
        when(plansRepository.findByActive_ActiveAndName_Name(true, newPlan))
                .thenReturn(Optional.of(newPlan_obj));
        when(subscriptionsRepository.findAllByPlanAndActiveStatus_Active(oldPlan, true))
                .thenReturn(subscriptions);
        when(deviceRepository.countBySubscription(subscription)).thenReturn(5);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> subscriptionsService.migrateAllToPlan(desiredVersion, actualPlan, newPlan));
        assertEquals("There are users with a number of active devices higher than the number of devices allowed in the subscription you are trying to move them. Ask the user to remove them and try again", 
                exception.getMessage());
    }

    private Plans createTestPlanWithDeviceLimit(String name, int deviceLimit) {
        Name planName = new Name();
        planName.setName(name);

        Description description = new Description();
        description.setDescription("Test Plan Description");

        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("120");

        MaximumNumberOfUsers maximumNumberOfUsers = new MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(deviceLimit);

        MusicCollection musicCollection = new MusicCollection();
        musicCollection.setMusicCollection(15);

        MusicSuggestion musicSuggestion = new MusicSuggestion();
        musicSuggestion.setMusicSuggestion("automatic");

        AnnualFee annualFee = new AnnualFee();
        annualFee.setAnnualFee(120.00);

        MonthlyFee monthlyFee = new MonthlyFee();
        monthlyFee.setMonthlyFee(12.00);

        Active activeStatus = new Active();
        activeStatus.setActive(true);

        Promoted promotedStatus = new Promoted();
        promotedStatus.setPromoted(false);

        return new Plans(planName, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, activeStatus, promotedStatus);
    }

    @Test
    void testPlanDetails_ValidUser_ShouldReturnPlansDetails() {

        setupSecurityContext();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(testSubscription));

        PlansDetails result = subscriptionsService.planDetails();

        assertNotNull(result);
        assertEquals(testPlan, result.getPlans());
        verify(userRepository).findById(1L);
        verify(subscriptionsRepository).findByActiveStatus_ActiveAndUser(true, testUser);
    }

    @Test
    void testPlanDetails_UserNotFound_ShouldThrowEntityNotFoundException() {

        setupSecurityContext();
        
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subscriptionsService.planDetails());
        assertEquals("You need to login in order to check the plan details", exception.getMessage());
    }

    @Test
    void testPlanDetails_NoSubscription_ShouldThrowEntityNotFoundException() {

        setupSecurityContext();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subscriptionsService.planDetails());
        assertEquals("No subscriptions associated with this user", exception.getMessage());
    }

    @Test
    void testAuthenticationParsing_WithComma_ShouldParseCorrectly() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("1,ROLE_USER");
        SecurityContextHolder.setContext(securityContext);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(testSubscription));

        PlansDetails result = subscriptionsService.planDetails();

        assertNotNull(result);
        verify(userRepository).findById(1L);
    }

    @Test
    void testAuthenticationParsing_WithoutComma_ShouldParseCorrectly() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("1");
        SecurityContextHolder.setContext(securityContext);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionsRepository.findByActiveStatus_ActiveAndUser(true, testUser))
                .thenReturn(Optional.of(testSubscription));

        PlansDetails result = subscriptionsService.planDetails();

        assertNotNull(result);
        verify(userRepository).findById(1L);
    }
}
