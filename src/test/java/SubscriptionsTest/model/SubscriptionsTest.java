package SubscriptionsTest.model;

import java.time.LocalDate;

import org.hibernate.StaleObjectStateException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.psoft_22_23_project.devicemanagement.repositories.DeviceRepository;
import com.example.psoft_22_23_project.plansmanagement.model.Active;
import com.example.psoft_22_23_project.plansmanagement.model.AnnualFee;
import com.example.psoft_22_23_project.plansmanagement.model.MaximumNumberOfUsers;
import com.example.psoft_22_23_project.plansmanagement.model.MonthlyFee;
import com.example.psoft_22_23_project.plansmanagement.model.MusicCollection;
import com.example.psoft_22_23_project.plansmanagement.model.MusicSuggestion;
import com.example.psoft_22_23_project.plansmanagement.model.NumberOfMinutes;
import com.example.psoft_22_23_project.plansmanagement.model.Plans;
import com.example.psoft_22_23_project.plansmanagement.model.Promoted;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.usermanagement.model.User;

public class SubscriptionsTest {

    @Mock
    private DeviceRepository deviceRepository;

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

    private Subscriptions createMonthlySubscription(String plan){

        PaymentType paymentType = new PaymentType("monthly");

        return new Subscriptions(createPlan(plan), paymentType, createUser());

    }

    private Subscriptions createAnnuallySubscription(String plan){

        PaymentType paymentType = new PaymentType("annually");

        return new Subscriptions(createPlan(plan), paymentType, createUser());

    }

    private User createUser(){

        return new User("alex@mail.com", "alexpass");

    }

    @Test
    public void createMonthlySubscriptionTest()     {

        Subscriptions subscriptions= createMonthlySubscription("teste");

        LocalDate date = LocalDate.now();

        assertEquals("monthly", subscriptions.getPaymentType().getPaymentType());
        assertEquals("teste", subscriptions.getPlan().getName().getName());
        assertEquals(date.toString(), subscriptions.getStartDate().getStartDate());
        assertEquals("Not yet defined", subscriptions.getEndDate().getEndDate());
        assertTrue(subscriptions.getActiveStatus().isActive());

    }

    @Test
    public void creatAnnuallySubscriptionTest()     {

        Subscriptions subscriptions= createAnnuallySubscription("teste");

        LocalDate date = LocalDate.now();

        assertEquals("annually", subscriptions.getPaymentType().getPaymentType());
        assertEquals("teste", subscriptions.getPlan().getName().getName());
        assertEquals(date.toString(), subscriptions.getStartDate().getStartDate());
        assertEquals(date.plusYears(1).toString(), subscriptions.getEndDate().getEndDate());
        assertTrue(subscriptions.getActiveStatus().isActive());

    }

    @Test
    public void testDeactivate_ValidVersion_ShouldDeactivateSubscription() {

        Subscriptions subscription = createMonthlySubscription("teste");
        long currentVersion = subscription.getVersion();

        subscription.deactivate(currentVersion);

        assertFalse(subscription.getActiveStatus().isActive());
    }

    @Test
    public void testDeactivate_InvalidVersion_ShouldThrowStaleObjectStateException() {

        Subscriptions subscription = createMonthlySubscription("teste");
        long invalidVersion = subscription.getVersion() + 1;

        StaleObjectStateException exception = assertThrows(
                StaleObjectStateException.class,
                () -> subscription.deactivate(invalidVersion)
        );

        assertTrue(exception.getMessage().contains("Object was already modified by another user"));
        assertTrue(subscription.getActiveStatus().isActive());
    }

    @Test
    public void testCheckChange_ValidVersion_ShouldNotThrowException() {

        Subscriptions subscription = createMonthlySubscription("teste");
        long currentVersion = subscription.getVersion();

        assertDoesNotThrow(() -> subscription.checkChange(currentVersion));
    }

    @Test
    public void testCheckChange_InvalidVersion_ShouldThrowStaleObjectStateException() {

        Subscriptions subscription = createMonthlySubscription("teste");
        long invalidVersion = subscription.getVersion() + 1;

        StaleObjectStateException exception = assertThrows(
                StaleObjectStateException.class,
                () -> subscription.checkChange(invalidVersion)
        );

        assertTrue(exception.getMessage().contains("Object was already modified by another user"));
    }

    @Test
    public void testChangePlan_ValidVersion_ShouldChangePlan() {

        Subscriptions subscription = createMonthlySubscription("original-plan");
        Plans newPlan = createPlan("new-plan");
        long currentVersion = subscription.getVersion();

        subscription.changePlan(currentVersion, newPlan);

        assertEquals("new-plan", subscription.getPlan().getName().getName());
    }

    @Test
    public void testChangePlan_InvalidVersion_ShouldThrowStaleObjectStateException() {

        Subscriptions subscription = createMonthlySubscription("original-plan");
        Plans newPlan = createPlan("new-plan");
        long invalidVersion = subscription.getVersion() + 1;
        String originalPlanName = subscription.getPlan().getName().getName();

        StaleObjectStateException exception = assertThrows(
                StaleObjectStateException.class,
                () -> subscription.changePlan(invalidVersion, newPlan)
        );

        assertTrue(exception.getMessage().contains("Object was already modified by another user"));
        assertEquals(originalPlanName, subscription.getPlan().getName().getName());
    }

    @Test
    public void testChangePlan_NullPlan_ShouldSetPlanToNull() {

        Subscriptions subscription = createMonthlySubscription("original-plan");
        long currentVersion = subscription.getVersion();

        subscription.changePlan(currentVersion, null);

        assertNull(subscription.getPlan());
    }

    @Test
    public void testVersionControlConsistency_MultipleOperations() {

        Subscriptions subscription = createMonthlySubscription("teste");
        long initialVersion = subscription.getVersion();

        assertDoesNotThrow(() -> subscription.checkChange(initialVersion));

        StaleObjectStateException exception = assertThrows(StaleObjectStateException.class,
                () -> subscription.deactivate(initialVersion + 1));

        assertTrue(exception.getMessage().contains("Object was already modified by another user"));
        assertTrue(subscription.getActiveStatus().isActive());
    }

    @Test
    public void testDefaultConstructor_ShouldCreateEmptySubscription() {

        assertDoesNotThrow(() -> {

            java.lang.reflect.Constructor<Subscriptions> constructor = 
                Subscriptions.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            
            Subscriptions subscription = constructor.newInstance();
            
            assertNotNull(subscription);
            assertTrue(subscription instanceof Subscriptions);
            
            assertEquals(0L, subscription.getVersion());
        });
    }

}
