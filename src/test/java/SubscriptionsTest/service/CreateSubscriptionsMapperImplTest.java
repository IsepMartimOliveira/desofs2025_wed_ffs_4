package SubscriptionsTest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.example.psoft_22_23_project.subscriptionsmanagement.api.CreateSubscriptionsRequest;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.subscriptionsmanagement.services.CreateSubscriptionsMapperImpl;
import com.example.psoft_22_23_project.usermanagement.model.User;

@ExtendWith(MockitoExtension.class)
public class CreateSubscriptionsMapperImplTest {

    @InjectMocks
    private CreateSubscriptionsMapperImpl createSubscriptionsMapper;

    private User testUser;
    private Plans testPlan;
    private CreateSubscriptionsRequest createRequest;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        testUser = createTestUser();
        testPlan = createTestPlan("Test Plan");
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

    @Test
    void testCreate_ValidInputs_ShouldCreateSubscription() {

        Subscriptions result = createSubscriptionsMapper.create(testUser, testPlan, createRequest);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testPlan, result.getPlan());
        assertNotNull(result.getPaymentType());
        assertEquals("monthly", result.getPaymentType().getPaymentType());
    }

    @Test
    void testCreate_AnnualPaymentType_ShouldCreateSubscriptionWithAnnualPayment() {

        CreateSubscriptionsRequest annualRequest = new CreateSubscriptionsRequest("Test Plan", "annually");

        Subscriptions result = createSubscriptionsMapper.create(testUser, testPlan, annualRequest);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testPlan, result.getPlan());
        assertNotNull(result.getPaymentType());
        assertEquals("annually", result.getPaymentType().getPaymentType());
    }

    @Test
    void testCreate_MonthlyPaymentType_ShouldCreateSubscriptionWithMonthlyPayment() {

        CreateSubscriptionsRequest monthlyRequest = new CreateSubscriptionsRequest("Test Plan", "monthly");

        Subscriptions result = createSubscriptionsMapper.create(testUser, testPlan, monthlyRequest);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testPlan, result.getPlan());
        assertNotNull(result.getPaymentType());
        assertEquals("monthly", result.getPaymentType().getPaymentType());
    }

    @Test
    void testCreate_AllParametersNull_ShouldReturnNull() {

        Subscriptions result = createSubscriptionsMapper.create(null, null, null);

        assertNull(result);
    }

    @Test
    void testCreate_UserNull_ShouldCreateSubscriptionWithNullUser() {

        Subscriptions result = createSubscriptionsMapper.create(null, testPlan, createRequest);

        assertNotNull(result);
        assertNull(result.getUser());
        assertEquals(testPlan, result.getPlan());
        assertNotNull(result.getPaymentType());
        assertEquals("monthly", result.getPaymentType().getPaymentType());
    }

    @Test
    void testCreate_PlanNull_ShouldCreateSubscriptionWithNullPlan() {

        Subscriptions result = createSubscriptionsMapper.create(testUser, null, createRequest);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertNull(result.getPlan());
        assertNotNull(result.getPaymentType());
        assertEquals("monthly", result.getPaymentType().getPaymentType());
    }

    @Test
    void testCreate_RequestNull_ShouldThrowNullPointerException() {

        assertThrows(NullPointerException.class, () ->
            createSubscriptionsMapper.create(testUser, testPlan, null)
        );
    }

    @Test
    void testCreate_RequestWithNullPaymentType_ShouldThrowNullPointerException() {

        CreateSubscriptionsRequest requestWithNullPayment = new CreateSubscriptionsRequest("Test Plan", null);

        assertThrows(NullPointerException.class, () ->
            createSubscriptionsMapper.create(testUser, testPlan, requestWithNullPayment)
        );
    }

    @Test
    void testCreate_RequestWithEmptyPaymentType_ShouldCreateSubscriptionWithEmptyPaymentType() {

        CreateSubscriptionsRequest requestWithEmptyPayment = new CreateSubscriptionsRequest("Test Plan", "");

        Subscriptions result = createSubscriptionsMapper.create(testUser, testPlan, requestWithEmptyPayment);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testPlan, result.getPlan());
        assertNotNull(result.getPaymentType());
        assertEquals("", result.getPaymentType().getPaymentType());
    }

    @Test
    void testCreate_DifferentUserAndPlanCombinations_ShouldWorkCorrectly() {

        User anotherUser = new User("another@example.com", "password2");
        Plans anotherPlan = createTestPlan("Another Plan");
        CreateSubscriptionsRequest anotherRequest = new CreateSubscriptionsRequest("Another Plan", "annually");

        Subscriptions result = createSubscriptionsMapper.create(anotherUser, anotherPlan, anotherRequest);

        assertNotNull(result);
        assertEquals(anotherUser, result.getUser());
        assertEquals(anotherPlan, result.getPlan());
        assertNotNull(result.getPaymentType());
        assertEquals("annually", result.getPaymentType().getPaymentType());
        assertEquals("Another Plan", result.getPlan().getName().getName());
    }

    @Test
    void testCreate_ValidInputs_ShouldCorrectlyAssignVariables() {

        Subscriptions result = createSubscriptionsMapper.create(testUser, testPlan, createRequest);

        assertNotNull(result);

        assertNotNull(result.getUser());
        assertEquals(testUser.getUsername(), result.getUser().getUsername());

        assertNotNull(result.getPlan());
        assertEquals(testPlan.getName().getName(), result.getPlan().getName().getName());
        assertEquals(testPlan.getDescription().getDescription(), result.getPlan().getDescription().getDescription());

        assertNotNull(result.getPaymentType());
        assertEquals(createRequest.getPaymentType(), result.getPaymentType().getPaymentType());
    }

    @Test
    void testCreate_ConditionLogic_SingleNullParameter_ShouldNotReturnNull() {

        Subscriptions result1 = createSubscriptionsMapper.create(null, testPlan, createRequest);
        assertNotNull(result1);

        Subscriptions result2 = createSubscriptionsMapper.create(testUser, null, createRequest);
        assertNotNull(result2);

        assertThrows(NullPointerException.class, () ->
            createSubscriptionsMapper.create(testUser, testPlan, null)
        );
    }

    @Test
    void testCreate_ConditionLogic_TwoNullParameters_ShouldNotReturnNull() {

        Subscriptions result1 = createSubscriptionsMapper.create(null, null, createRequest);
        assertNotNull(result1);

        assertThrows(NullPointerException.class, () ->
            createSubscriptionsMapper.create(null, testPlan, null)
        );

        assertThrows(NullPointerException.class, () ->
            createSubscriptionsMapper.create(testUser, null, null)
        );
    }
}
