package PlansTest.service;

import com.example.psoft_22_23_project.plansmanagement.api.CreatePlanRequest;
import com.example.psoft_22_23_project.plansmanagement.model.*;
import com.example.psoft_22_23_project.plansmanagement.repositories.PlansRepository;
import com.example.psoft_22_23_project.plansmanagement.services.CreatePlansMapper;
import com.example.psoft_22_23_project.plansmanagement.services.PlansServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CreatePlanTest {

    @Mock
    private PlansRepository plansRepository;


    @Mock
    private CreatePlansMapper createPlansMapper;

    @InjectMocks
    private PlansServiceImpl plansService;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private void setupMockRepository(String planName, Plans plan) {
        when(plansRepository.findByName_Name(planName)).thenReturn(Optional.of(plan));
        when(plansRepository.save(plan)).thenReturn(plan); // Mock the save operation
    }
    private Plans createPlan(String name) {
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
        activeStatus.setActive(true);

        Promoted promotedStatus = new Promoted();
        promotedStatus.setPromoted(true);

        return new Plans(planName, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, activeStatus, promotedStatus);
    }

    @Test
    public void createTest() {

        CreatePlanRequest request = new CreatePlanRequest();

        Name name = new Name();
        request.setName("New Plan");
        name.setName("New Plan");


        Description description = new Description();
        request.setDescription("Plan Description");
        description.setDescription("Plan Description");

        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        request.setNumberOfMinutes("100");
        numberOfMinutes.setNumberOfMinutes("100");

        MaximumNumberOfUsers maximumNumberOfUsers = new MaximumNumberOfUsers();
        request.setMaximumNumberOfUsers(5);
        maximumNumberOfUsers.setMaximumNumberOfUsers(5);

        MusicCollection musicCollection = new MusicCollection();
        request.setMusicCollection(10);
        musicCollection.setMusicCollection(10);

        MusicSuggestion musicSuggestion = new MusicSuggestion();
        request.setMusicSuggestion("personalized");
        musicSuggestion.setMusicSuggestion("personalized");

        AnnualFee annualFee = new AnnualFee();
        request.setAnnualFee(150.00);
        annualFee.setAnnualFee(150.00);

        MonthlyFee monthlyFee = new MonthlyFee();
        request.setMonthlyFee(15.00);
        monthlyFee.setMonthlyFee(15.00);

        Active active = new Active();
        request.setActive(true);
        active.setActive(true);

        Promoted promoted = new Promoted();
        request.setPromoted(false);
        promoted.setPromoted(false);


        Plans plan = new Plans(name, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, active, promoted);

        when(plansRepository.findByName_Name(Mockito.anyString())).thenReturn(Optional.empty());
        when(plansRepository.save(Mockito.any(Plans.class))).thenReturn(plan);
        when(createPlansMapper.create(Mockito.any(CreatePlanRequest.class))).thenReturn(plan);


        Plans plans = plansService.create(request);

        assertEquals(plans.getName().getName(), request.getName());
        assertEquals(plans.getDescription().getDescription(), request.getDescription());
        assertEquals(plans.getNumberOfMinutes().getNumberOfMinutes(), request.getNumberOfMinutes());
        assertEquals(plans.getMaximumNumberOfUsers().getMaximumNumberOfUsers(), request.getMaximumNumberOfUsers());
        assertEquals(plans.getMusicCollection().getMusicCollection(), request.getMusicCollection());
        assertEquals(plans.getMusicSuggestion().getMusicSuggestion(), request.getMusicSuggestion());
        assertEquals(plans.getAnnualFee().getAnnualFee(), request.getAnnualFee());
        assertEquals(plans.getMonthlyFee().getMonthlyFee(), request.getMonthlyFee());
        assertEquals(plans.getActive().getActive(), request.getActive());
        assertEquals(plans.getPromoted().getPromoted(), request.getPromoted());

        when(plansRepository.findByName_Name("New Plan")).thenReturn(Optional.of(plan));

        when(plansRepository.save(Mockito.any(Plans.class))).thenReturn(plan);

    }

    @Test
    public void createPlan_SameName() {

        String planName =  "Plan";

        Plans plan = createPlan(planName);

        setupMockRepository(planName, plan);

        CreatePlanRequest request = new CreatePlanRequest();
        request.setName("Plan");
        assertThrows(IllegalArgumentException.class, () -> plansService.create(request));

    }

}
