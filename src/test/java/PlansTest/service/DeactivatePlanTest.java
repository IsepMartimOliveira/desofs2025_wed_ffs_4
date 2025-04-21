package PlansTest.service;

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

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeactivatePlanTest {

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

    private Plans createPlan(String name, boolean active) {
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
        promotedStatus.setPromoted(true);

        return new Plans(planName, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, activeStatus, promotedStatus);
    }

    private void setupMockRepository(String planName, Plans plan) {
        when(plansRepository.findByName_Name(planName)).thenReturn(Optional.of(plan));
        when(plansRepository.save(plan)).thenReturn(plan); // Mock the save operation
        when(plansRepository.ceaseByPlan(plan, 0L)).thenReturn(1);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Plan Name" })
    public void deactivateTest(String planName) {
        Plans plan = createPlan(planName, true);
        setupMockRepository(planName, plan);
        Plans plans1 = plansService.deactivate(planName, 0L);
        assertEquals(plan.getActive().getActive(),plans1.getActive().getActive());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Invalid Plan" })
    public void deactivateTest_InvalidPlan(String planName) {
        when(plansRepository.findByName_Name(planName)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> plansService.deactivate(planName, 0L));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Invalid Plan" })
    public void deactivateTest_PlanAlreadyDeac(String planName) {
        Plans plan = createPlan(planName, false);
        setupMockRepository(planName, plan);
        assertThrows(IllegalArgumentException.class, () -> plansService.deactivate(planName, 0L));
    }
}
