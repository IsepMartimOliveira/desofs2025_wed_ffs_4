package SubscriptionsTest.model;

import com.example.psoft_22_23_project.devicemanagement.repositories.DeviceRepository;
import com.example.psoft_22_23_project.plansmanagement.model.*;

import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.usermanagement.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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

}
