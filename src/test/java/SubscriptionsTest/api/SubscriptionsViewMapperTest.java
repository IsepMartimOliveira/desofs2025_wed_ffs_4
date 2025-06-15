package SubscriptionsTest.api;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import com.example.psoft_22_23_project.subscriptionsmanagement.api.SubscriptionsView;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.SubscriptionsViewMapper;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.usermanagement.model.User;

public class SubscriptionsViewMapperTest {

    private SubscriptionsViewMapper subscriptionsViewMapper = new com.example.psoft_22_23_project.subscriptionsmanagement.api.SubscriptionsViewMapperImpl();

    private User testUser;
    private Plans testPlan;
    private Subscriptions testSubscription;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testPlan = createTestPlan();
        testSubscription = createTestSubscription();
    }

    private User createTestUser() {
        return new User("testuser@example.com", "password123");
    }

    private Plans createTestPlan() {
        Name name = new Name();
        name.setName("Premium Plan");

        Description description = new Description();
        description.setDescription("Premium plan with unlimited features");

        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("unlimited");

        MaximumNumberOfUsers maximumNumberOfUsers = new MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(10);

        MusicCollection musicCollection = new MusicCollection();
        musicCollection.setMusicCollection(1000);

        MusicSuggestion musicSuggestion = new MusicSuggestion();
        musicSuggestion.setMusicSuggestion("personalized");

        AnnualFee annualFee = new AnnualFee();
        annualFee.setAnnualFee(120.00);

        MonthlyFee monthlyFee = new MonthlyFee();
        monthlyFee.setMonthlyFee(12.00);

        Active active = new Active();
        active.setActive(true);

        Promoted promoted = new Promoted();
        promoted.setPromoted(true);

        return new Plans(name, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, active, promoted);
    }

    private Subscriptions createTestSubscription() {
        PaymentType paymentType = new PaymentType();
        paymentType.setPaymentType("monthly");

        return new Subscriptions(testPlan, paymentType, testUser);
    }

    @Test
    void testToSubscriptionView_ValidSubscription_ShouldMapCorrectly() {

        SubscriptionsView result = subscriptionsViewMapper.toSubscriptionView(testSubscription);

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testPlan.getName().getName(), result.getPlanName());
        assertEquals(testPlan.getDescription().getDescription(), result.getPlanDescription());
        assertEquals("monthly", result.getPaymentType());
        assertNotNull(result.getStartDate());
        assertNotNull(result.getEndDate());
        assertNotNull(result.getActiveStatus());
    }

    @Test
    void testToSubscriptionView_NullSubscription_ShouldReturnNull() {

        SubscriptionsView result = subscriptionsViewMapper.toSubscriptionView(null);

        assertNull(result);
    }

    @Test
    void testToSubscriptionView_SubscriptionWithNullUser_ShouldHandleGracefully() {

        PaymentType paymentType = new PaymentType();
        paymentType.setPaymentType("annually");
        Subscriptions subscriptionWithNullUser = new Subscriptions(testPlan, paymentType, null);

        SubscriptionsView result = subscriptionsViewMapper.toSubscriptionView(subscriptionWithNullUser);

        assertNotNull(result);
        assertNull(result.getUsername());
        assertEquals(testPlan.getName().getName(), result.getPlanName());
        assertEquals(testPlan.getDescription().getDescription(), result.getPlanDescription());
        assertEquals("annually", result.getPaymentType());
    }

    @Test
    void testToSubscriptionView_SubscriptionWithNullPlan_ShouldHandleGracefully() {

        PaymentType paymentType = new PaymentType();
        paymentType.setPaymentType("monthly");
        Subscriptions subscriptionWithNullPlan = new Subscriptions(null, paymentType, testUser);

        SubscriptionsView result = subscriptionsViewMapper.toSubscriptionView(subscriptionWithNullPlan);

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertNull(result.getPlanName());
        assertNull(result.getPlanDescription());
        assertEquals("monthly", result.getPaymentType());
    }

    @Test
    void testToSubscriptionsView_ValidList_ShouldMapAll() {

        PaymentType annualPaymentType = new PaymentType();
        annualPaymentType.setPaymentType("annually");
        
        User anotherUser = new User("anotheruser@example.com", "password456");
        Subscriptions anotherSubscription = new Subscriptions(testPlan, annualPaymentType, anotherUser);
        
        List<Subscriptions> subscriptionsList = Arrays.asList(testSubscription, anotherSubscription);

        Iterable<SubscriptionsView> result = subscriptionsViewMapper.toSubscriptionsView(subscriptionsList);

        assertNotNull(result);
        List<SubscriptionsView> resultList = (List<SubscriptionsView>) result;
        assertEquals(2, resultList.size());
        
        SubscriptionsView view1 = resultList.get(0);
        assertEquals(testUser.getUsername(), view1.getUsername());
        assertEquals("monthly", view1.getPaymentType());
        
        SubscriptionsView view2 = resultList.get(1);
        assertEquals(anotherUser.getUsername(), view2.getUsername());
        assertEquals("annually", view2.getPaymentType());
    }

    @Test
    void testToSubscriptionsView_EmptyList_ShouldReturnEmptyIterable() {

        List<Subscriptions> emptyList = Arrays.asList();

        Iterable<SubscriptionsView> result = subscriptionsViewMapper.toSubscriptionsView(emptyList);

        assertNotNull(result);
        List<SubscriptionsView> resultList = (List<SubscriptionsView>) result;
        assertEquals(0, resultList.size());
    }

    @Test
    void testToSubscriptionsView_NullList_ShouldReturnNull() {

        Iterable<SubscriptionsView> result = subscriptionsViewMapper.toSubscriptionsView(null);

        assertNull(result);
    }

    @Test
    void testMapOptInt_WithValue_ShouldReturnValue() {

        Optional<Integer> optionalValue = Optional.of(42);

        Integer result = subscriptionsViewMapper.mapOptInt(optionalValue);

        assertEquals(42, result);
    }

    @Test
    void testMapOptInt_Empty_ShouldReturnNull() {

        Optional<Integer> emptyOptional = Optional.empty();

        Integer result = subscriptionsViewMapper.mapOptInt(emptyOptional);

        assertNull(result);
    }

    @Test
    void testMapOptLong_WithValue_ShouldReturnValue() {

        Optional<Long> optionalValue = Optional.of(123L);

        Long result = subscriptionsViewMapper.mapOptLong(optionalValue);

        assertEquals(123L, result);
    }

    @Test
    void testMapOptLong_Empty_ShouldReturnNull() {

        Optional<Long> emptyOptional = Optional.empty();

        Long result = subscriptionsViewMapper.mapOptLong(emptyOptional);

        assertNull(result);
    }

    @Test
    void testMapOptString_WithValue_ShouldReturnValue() {

        Optional<String> optionalValue = Optional.of("test string");

        String result = subscriptionsViewMapper.mapOptString(optionalValue);

        assertEquals("test string", result);
    }

    @Test
    void testMapOptString_Empty_ShouldReturnNull() {

        Optional<String> emptyOptional = Optional.empty();

        String result = subscriptionsViewMapper.mapOptString(emptyOptional);

        assertNull(result);
    }

    @Test
    void testToSubscriptionView_DifferentPaymentTypes_ShouldMapCorrectly() {

        PaymentType annualPaymentType = new PaymentType();
        annualPaymentType.setPaymentType("annually");
        Subscriptions annualSubscription = new Subscriptions(testPlan, annualPaymentType, testUser);

        SubscriptionsView result = subscriptionsViewMapper.toSubscriptionView(annualSubscription);

        assertNotNull(result);
        assertEquals("annually", result.getPaymentType());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testPlan.getName().getName(), result.getPlanName());
    }

    @Test
    void testToSubscriptionView_WithComplexPlanData_ShouldMapAllFields() {

        SubscriptionsView result = subscriptionsViewMapper.toSubscriptionView(testSubscription);

        assertNotNull(result);
        assertEquals("testuser@example.com", result.getUsername());
        assertEquals("Premium Plan", result.getPlanName());
        assertEquals("Premium plan with unlimited features", result.getPlanDescription());
        assertEquals("monthly", result.getPaymentType());
        
        assertNotNull(result.getStartDate());
        assertNotNull(result.getEndDate());
        assertNotNull(result.getActiveStatus());
    }

    @Test
    void testMapOptMethods_NullInput_ShouldReturnNull() {

        assertNull(subscriptionsViewMapper.mapOptInt(null));
        assertNull(subscriptionsViewMapper.mapOptLong(null));
        assertNull(subscriptionsViewMapper.mapOptString(null));
    }
}
