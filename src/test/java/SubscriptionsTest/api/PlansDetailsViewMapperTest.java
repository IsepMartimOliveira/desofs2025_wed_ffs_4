package SubscriptionsTest.api;

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
import com.example.psoft_22_23_project.subscriptionsmanagement.api.PlansDetailsView;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.PlansDetailsViewMapper;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PlansDetails;

public class PlansDetailsViewMapperTest {

    private PlansDetailsViewMapper plansDetailsViewMapper = new com.example.psoft_22_23_project.subscriptionsmanagement.api.PlansDetailsViewMapperImpl();

    private Plans testPlan;
    private PlansDetails testPlansDetails;

    @BeforeEach
    void setUp() {
        testPlan = createTestPlan();
        testPlansDetails = new PlansDetails(testPlan);
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

    @Test
    void testToPlansDetailsView_ValidPlansDetails_ShouldMapCorrectly() {
    
        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(testPlansDetails);

        assertNotNull(result);
        assertEquals("Premium Plan", result.getName());
        assertEquals("Premium plan with unlimited features", result.getDescription());
        assertEquals("unlimited", result.getNumberOfMinutes());
        assertEquals("10", result.getMaximumNumberOfUsers());
        assertEquals("1000", result.getMusicCollection());
        assertEquals("personalized", result.getMusicSuggestion());
        assertEquals("120.0 " + "€", result.getAnnualFee());
        assertEquals("12.0 " + "€", result.getMonthlyFee());
        assertEquals("true", result.getActive());
        assertEquals("true", result.getPromoted());
    }

    @Test
    void testToPlansDetailsView_NullPlansDetails_ShouldReturnNull() {
      
        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(null);

        assertNull(result);
    }

    @Test
    void testToPlansDetailsView_PlansDetailsWithNullPlan_ShouldHandleGracefully() {

        PlansDetails plansDetailsWithNullPlan = new PlansDetails(null);

        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(plansDetailsWithNullPlan);

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getDescription());
        assertNull(result.getNumberOfMinutes());
        assertNull(result.getMaximumNumberOfUsers());
        assertNull(result.getMusicCollection());
        assertNull(result.getMusicSuggestion());
        assertNull(result.getAnnualFee());
        assertNull(result.getMonthlyFee());
        assertNull(result.getActive());
        assertNull(result.getPromoted());
    }

    @Test
    void testToPlansDetailsView_BasicPlan_ShouldMapCorrectly() {
        
        Plans basicPlan = createBasicPlan();
        PlansDetails basicPlansDetails = new PlansDetails(basicPlan);

        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(basicPlansDetails);

        assertNotNull(result);
        assertEquals("Basic Plan", result.getName());
        assertEquals("Basic plan with limited features", result.getDescription());
        assertEquals("60", result.getNumberOfMinutes());
        assertEquals("1", result.getMaximumNumberOfUsers());
        assertEquals("100", result.getMusicCollection());
        assertEquals("personalized", result.getMusicSuggestion());
        assertEquals("60.0 " + "€", result.getAnnualFee());
        assertEquals("6.0 " + "€", result.getMonthlyFee());
        assertEquals("true", result.getActive());
        assertEquals("false", result.getPromoted());
    }

    private Plans createBasicPlan() {
        Name name = new Name();
        name.setName("Basic Plan");

        Description description = new Description();
        description.setDescription("Basic plan with limited features");

        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("60");

        MaximumNumberOfUsers maximumNumberOfUsers = new MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(1);

        MusicCollection musicCollection = new MusicCollection();
        musicCollection.setMusicCollection(100);

        MusicSuggestion musicSuggestion = new MusicSuggestion();
        musicSuggestion.setMusicSuggestion("personalized");

        AnnualFee annualFee = new AnnualFee();
        annualFee.setAnnualFee(60.00);

        MonthlyFee monthlyFee = new MonthlyFee();
        monthlyFee.setMonthlyFee(6.00);

        Active active = new Active();
        active.setActive(true);

        Promoted promoted = new Promoted();
        promoted.setPromoted(false);

        return new Plans(name, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, active, promoted);
    }

    @Test
    void testToPlansDetailsView_InactivePlan_ShouldMapActiveStatusCorrectly() {
       
        Plans inactivePlan = createInactivePlan();
        PlansDetails inactivePlansDetails = new PlansDetails(inactivePlan);

        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(inactivePlansDetails);

        assertNotNull(result);
        assertEquals("Inactive Plan", result.getName());
        assertEquals("false", result.getActive());
        assertEquals("false", result.getPromoted());
    }

    private Plans createInactivePlan() {
        Name name = new Name();
        name.setName("Inactive Plan");

        Description description = new Description();
        description.setDescription("This plan is inactive");

        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("30");

        MaximumNumberOfUsers maximumNumberOfUsers = new MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(1);

        MusicCollection musicCollection = new MusicCollection();
        musicCollection.setMusicCollection(50);

        MusicSuggestion musicSuggestion = new MusicSuggestion();
        musicSuggestion.setMusicSuggestion("automatic");

        AnnualFee annualFee = new AnnualFee();
        annualFee.setAnnualFee(30.00);

        MonthlyFee monthlyFee = new MonthlyFee();
        monthlyFee.setMonthlyFee(3.00);

        Active active = new Active();
        active.setActive(false);

        Promoted promoted = new Promoted();
        promoted.setPromoted(false);

        return new Plans(name, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, active, promoted);
    }

    @Test
    void testToPlansDetailsView_ZeroValues_ShouldMapCorrectly() {
        
        Plans planWithZeros = createPlanWithZeroValues();
        PlansDetails plansDetailsWithZeros = new PlansDetails(planWithZeros);

        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(plansDetailsWithZeros);

        assertNotNull(result);
        assertEquals("Free Plan", result.getName());
        assertEquals("1", result.getNumberOfMinutes());
        assertEquals("0", result.getMaximumNumberOfUsers());
        assertEquals("0", result.getMusicCollection());
        assertEquals("0.0 €", result.getAnnualFee());
        assertEquals("0.0 €", result.getMonthlyFee());
    }

    private Plans createPlanWithZeroValues() {
        Name name = new Name();
        name.setName("Free Plan");

        Description description = new Description();
        description.setDescription("Free plan with no features");

        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("1");

        MaximumNumberOfUsers maximumNumberOfUsers = new MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(0);

        MusicCollection musicCollection = new MusicCollection();
        musicCollection.setMusicCollection(0);

        MusicSuggestion musicSuggestion = new MusicSuggestion();
        musicSuggestion.setMusicSuggestion("automatic");

        AnnualFee annualFee = new AnnualFee();
        annualFee.setAnnualFee(0.0);

        MonthlyFee monthlyFee = new MonthlyFee();
        monthlyFee.setMonthlyFee(0.0);

        Active active = new Active();
        active.setActive(true);

        Promoted promoted = new Promoted();
        promoted.setPromoted(false);

        return new Plans(name, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, active, promoted);
    }

    @Test
    void testToPlansDetailsView_HighValues_ShouldMapCorrectly() {
        
        Plans planWithHighValues = createPlanWithHighValues();
        PlansDetails plansDetailsWithHighValues = new PlansDetails(planWithHighValues);

        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(plansDetailsWithHighValues);

        assertNotNull(result);
        assertEquals("Enterprise Plan", result.getName());
        assertEquals("9999", result.getNumberOfMinutes());
        assertEquals("1000", result.getMaximumNumberOfUsers());
        assertEquals("100000", result.getMusicCollection());
        assertEquals("9999.99 " + "€", result.getAnnualFee());
        assertEquals("999.99 " + "€", result.getMonthlyFee());
    }

    private Plans createPlanWithHighValues() {
        Name name = new Name();
        name.setName("Enterprise Plan");

        Description description = new Description();
        description.setDescription("Enterprise plan for large organizations");

        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("9999");

        MaximumNumberOfUsers maximumNumberOfUsers = new MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(1000);

        MusicCollection musicCollection = new MusicCollection();
        musicCollection.setMusicCollection(100000);

        MusicSuggestion musicSuggestion = new MusicSuggestion();
        musicSuggestion.setMusicSuggestion("automatic");

        AnnualFee annualFee = new AnnualFee();
        annualFee.setAnnualFee(9999.99);

        MonthlyFee monthlyFee = new MonthlyFee();
        monthlyFee.setMonthlyFee(999.99);

        Active active = new Active();
        active.setActive(true);

        Promoted promoted = new Promoted();
        promoted.setPromoted(true);

        return new Plans(name, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, active, promoted);
    }

    @Test
    void testToPlansDetailsView_DifferentMusicSuggestionValues_ShouldMapCorrectly() {
       
        testMusicSuggestionMapping("automatic");
        testMusicSuggestionMapping("personalized");
    }

    private void testMusicSuggestionMapping(String suggestionValue) {
        
        Plans plan = createTestPlan();
        plan.getMusicSuggestion().setMusicSuggestion(suggestionValue);
        PlansDetails plansDetails = new PlansDetails(plan);

        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(plansDetails);

        assertEquals(suggestionValue, result.getMusicSuggestion());
    }

    @Test
    void testToPlansDetailsView_NumericStringValues_ShouldMapCorrectly() {
       
        Plans plan = createTestPlan();
        plan.getNumberOfMinutes().setNumberOfMinutes("120");
        PlansDetails plansDetails = new PlansDetails(plan);

        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(plansDetails);

        assertEquals("120", result.getNumberOfMinutes());
    }

    @Test
    void testToPlansDetailsView_DecimalValues_ShouldMapCorrectly() {
     
        Plans plan = createTestPlan();
        plan.getMonthlyFee().setMonthlyFee(12.99);
        plan.getAnnualFee().setAnnualFee(129.99);
        PlansDetails plansDetails = new PlansDetails(plan);

        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(plansDetails);

        assertEquals("12.99 " + "€", result.getMonthlyFee());
        assertEquals("129.99 " + "€", result.getAnnualFee());
    }

    @Test
    void testToPlansDetailsView_BooleanToStringMapping_ShouldWork() {
      
        testBooleanMapping(true, true, "true", "true");
        
        testBooleanMapping(false, false, "false", "false");
        
        testBooleanMapping(true, false, "true", "false");
        testBooleanMapping(false, true, "false", "true");
    }

    private void testBooleanMapping(boolean activeValue, boolean promotedValue, 
                                   String expectedActive, String expectedPromoted) {
        
        Plans plan = createTestPlan();
        plan.getActive().setActive(activeValue);
        plan.getPromoted().setPromoted(promotedValue);
        PlansDetails plansDetails = new PlansDetails(plan);

        PlansDetailsView result = plansDetailsViewMapper.toPlansDetailsView(plansDetails);

        assertEquals(expectedActive, result.getActive());
        assertEquals(expectedPromoted, result.getPromoted());
    }
}
