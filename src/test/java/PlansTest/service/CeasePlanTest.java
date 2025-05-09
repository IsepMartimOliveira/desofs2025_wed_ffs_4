package PlansTest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.psoft_22_23_project.plansmanagement.model.*;
import com.example.psoft_22_23_project.plansmanagement.repositories.PlansRepository;
import com.example.psoft_22_23_project.plansmanagement.services.PlansServiceImpl;
import com.example.psoft_22_23_project.subscriptionsmanagement.repositories.SubscriptionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

public class CeasePlanTest {

    @Mock
    private PlansRepository plansRepository;

    @Mock
    private SubscriptionsRepository subscriptionsRepository;

    @InjectMocks
    private PlansServiceImpl plansService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Plans createPlan(String name, boolean active, boolean promoted) {
        Name planName = new Name();
        planName.setName(name);

        Description description = new Description();
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
        activeStatus.setActive(active);

        Promoted promotedStatus = new Promoted();
        promotedStatus.setPromoted(promoted);

        return new Plans(planName, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, activeStatus, promotedStatus);
    }

    private void setupMockRepository(String planName, Plans plan, boolean hasSubscriptions) {
        when(plansRepository.findByName_Name(planName)).thenReturn(Optional.of(plan));
        when(subscriptionsRepository.existsByPlanAndUserNotNull(plan)).thenReturn(hasSubscriptions);
        when(plansRepository.save(plan)).thenReturn(plan); // Mock the save operation
        when(plansRepository.ceaseByPlan(plan, 0L)).thenReturn(1);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Plan Name" })
    public void ceaseTest(String planName) {
        Plans plan = createPlan(planName, false, false);
        setupMockRepository(planName, plan, false);

        int result = plansService.cease(planName, 0L);

        assertEquals(1, result);
        verify(plansRepository).ceaseByPlan(plan, 0L);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Invalid Plan" })
    public void ceaseTest_InvalidPlan(String planName) {
        when(plansRepository.findByName_Name(planName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> plansService.cease(planName, 0L));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Active Plan", "Promoted Plan" })
    public void ceaseTest_ActiveOrPromotedPlan(String planName) {
        Plans plan = createPlan(planName, true, true);
        setupMockRepository(planName, plan, false);

        assertThrows(IllegalArgumentException.class, () -> plansService.cease(planName, 0L));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Plan with Subscriptions" })
    public void ceaseTest_ActiveSubscriptions(String planName) {
        Plans plan = createPlan(planName, false, false);
        setupMockRepository(planName, plan, true);

        assertThrows(IllegalArgumentException.class, () -> plansService.cease(planName, 0L));
    }
}
