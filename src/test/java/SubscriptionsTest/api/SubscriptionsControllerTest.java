package SubscriptionsTest.api;

import com.example.psoft_22_23_project.plansmanagement.model.Plans;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.SubscriptionsController;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.SubscriptionsViewMapper;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.PlansDetailsViewMapper;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.CreateSubscriptionsRequest;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.subscriptionsmanagement.services.SubscriptionsService;
import com.example.psoft_22_23_project.usermanagement.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SubscriptionsControllerTest {

    @Mock
    private SubscriptionsService subscriptionsService;

    @Mock
    private SubscriptionsViewMapper subscriptionsViewMapper;

    @Mock
    private PlansDetailsViewMapper plansDetailsViewMapper;

    @Mock
    private WebRequest webRequest;

    @Mock
    private Authentication authentication;

    private SubscriptionsController subscriptionsController;
    private User testUser;
    private Plans testPlan;
    private Subscriptions testSubscription;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        subscriptionsController = new SubscriptionsController(subscriptionsService, subscriptionsViewMapper, plansDetailsViewMapper);
        testUser = new User("alex@mail.com", "alexpass");

        // Create testPlan using the factory method from other tests
        testPlan = createPlan("Premium");
        testSubscription = new Subscriptions(testPlan, new PaymentType("monthly"), testUser);

        when(authentication.getName()).thenReturn(testUser.getEmail());
    }

    // Helper method from other test classes
    private Plans createPlan(String name) {
        com.example.psoft_22_23_project.plansmanagement.model.Name planName = new com.example.psoft_22_23_project.plansmanagement.model.Name();
        planName.setName(name);

        com.example.psoft_22_23_project.plansmanagement.model.Description description = new com.example.psoft_22_23_project.plansmanagement.model.Description();
        description.setDescription("Plan Description");

        com.example.psoft_22_23_project.plansmanagement.model.NumberOfMinutes numberOfMinutes = new com.example.psoft_22_23_project.plansmanagement.model.NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("100");

        com.example.psoft_22_23_project.plansmanagement.model.MaximumNumberOfUsers maximumNumberOfUsers = new com.example.psoft_22_23_project.plansmanagement.model.MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(5);

        com.example.psoft_22_23_project.plansmanagement.model.MusicCollection musicCollection = new com.example.psoft_22_23_project.plansmanagement.model.MusicCollection();
        musicCollection.setMusicCollection(10);

        com.example.psoft_22_23_project.plansmanagement.model.MusicSuggestion musicSuggestion = new com.example.psoft_22_23_project.plansmanagement.model.MusicSuggestion();
        musicSuggestion.setMusicSuggestion("personalized");

        com.example.psoft_22_23_project.plansmanagement.model.AnnualFee annualFee = new com.example.psoft_22_23_project.plansmanagement.model.AnnualFee();
        annualFee.setAnnualFee(100.00);

        com.example.psoft_22_23_project.plansmanagement.model.MonthlyFee monthlyFee = new com.example.psoft_22_23_project.plansmanagement.model.MonthlyFee();
        monthlyFee.setMonthlyFee(10.00);

        com.example.psoft_22_23_project.plansmanagement.model.Active activeStatus = new com.example.psoft_22_23_project.plansmanagement.model.Active();
        activeStatus.setActive(true);

        com.example.psoft_22_23_project.plansmanagement.model.Promoted promotedStatus = new com.example.psoft_22_23_project.plansmanagement.model.Promoted();
        promotedStatus.setPromoted(false);

        return new Plans(planName, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, activeStatus, promotedStatus);
    }

    // 1. Subscription Cancellation Tests
    @Test
    public void cancelSubscription_Success() {
        // Arrange
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.cancelSubscription(eq(1L))).thenReturn(testSubscription);

        // Act
        ResponseEntity<?> response = subscriptionsController.cancelSubscription(webRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(subscriptionsService).cancelSubscription(eq(1L));
    }

    @Test
    public void cancelSubscription_MissingIfMatchHeader() {
        // Arrange
        when(webRequest.getHeader("If-Match")).thenReturn(null);

        // Act & Assert
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () ->
                subscriptionsController.cancelSubscription(webRequest)
        );
    }

    @Test
    public void cancelSubscription_EntityNotFound() {
        // Arrange
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.cancelSubscription(anyLong())).thenThrow(
                new EntityNotFoundException("No subscriptions found")
        );

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                subscriptionsController.cancelSubscription(webRequest)
        );
    }

    // 2. Plan Change Tests
    @Test
    public void changePlan_Success() {
        // Arrange
        String planName = "Premium Plus";
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.changePlan(anyLong(), eq(planName))).thenReturn(testSubscription);

        // Act
        ResponseEntity<?> response = subscriptionsController.changePlan(webRequest, planName);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(subscriptionsService).changePlan(anyLong(), eq(planName));
    }

    @Test
    public void changePlan_MissingIfMatchHeader() {
        // Arrange
        String planName = "Premium Plus";
        when(webRequest.getHeader("If-Match")).thenReturn(null);

        // Act & Assert
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () ->
                subscriptionsController.changePlan(webRequest, planName)
        );
    }

    @Test
    public void changePlan_IllegalArgumentException() {
        // Arrange
        String planName = "Current Plan";
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.changePlan(anyLong(), eq(planName))).thenThrow(
                new IllegalArgumentException("You are already subscribed to this plan")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                subscriptionsController.changePlan(webRequest, planName)
        );
    }

    // 3. Subscription Renewal Tests
    @Test
    public void renewSubscription_Success() {
        // Arrange
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.renewAnualSubscription(anyLong())).thenReturn(testSubscription);

        // Act
        ResponseEntity<?> response = subscriptionsController.renewAnualSubscription(webRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(subscriptionsService).renewAnualSubscription(anyLong());
    }

    @Test
    public void renewSubscription_MissingIfMatchHeader() {
        // Arrange
        when(webRequest.getHeader("If-Match")).thenReturn(null);

        // Act & Assert
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () ->
                subscriptionsController.renewAnualSubscription(webRequest)
        );
    }

    @Test
    public void renewSubscription_MonthlySubscription() {
        // Arrange
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.renewAnualSubscription(anyLong())).thenThrow(
                new IllegalArgumentException("You can not renew a monthly subscription")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                subscriptionsController.renewAnualSubscription(webRequest)
        );
    }




    @Test
    public void createSubscription_UserAlreadyHasActiveSubscription() {
        // Arrange
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest();
        request.setName("Premium");
        request.setPaymentType("monthly");

        when(subscriptionsService.create(any(CreateSubscriptionsRequest.class))).thenThrow(
                new IllegalArgumentException("You need to let your active subscription end in order to subscribe")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                subscriptionsController.create(request)
        );
    }
}