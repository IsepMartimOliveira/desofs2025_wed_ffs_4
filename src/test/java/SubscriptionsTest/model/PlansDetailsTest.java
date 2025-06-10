package SubscriptionsTest.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PlansDetails;

public class PlansDetailsTest {

    private Plans createTestPlan(String name) {
        Name planName = new Name();
        planName.setName(name);

        Description description = new Description();
        description.setDescription("Test Plan Description");

        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("120");

        MaximumNumberOfUsers maximumNumberOfUsers = new MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(10);

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
    public void testPlansDetailsConstructor_ValidPlan_ShouldCreatePlansDetails() {

        Plans testPlan = createTestPlan("Test Plan");

        PlansDetails plansDetails = new PlansDetails(testPlan);

        assertNotNull(plansDetails);
        assertNotNull(plansDetails.getPlans());
        assertEquals(testPlan, plansDetails.getPlans());
        assertEquals("Test Plan", plansDetails.getPlans().getName().getName());
        assertEquals("Test Plan Description", plansDetails.getPlans().getDescription().getDescription());
        assertEquals("120", plansDetails.getPlans().getNumberOfMinutes().getNumberOfMinutes());
        assertEquals(10, plansDetails.getPlans().getMaximumNumberOfUsers().getMaximumNumberOfUsers());
        assertEquals(15, plansDetails.getPlans().getMusicCollection().getMusicCollection());
        assertEquals("automatic", plansDetails.getPlans().getMusicSuggestion().getMusicSuggestion());
        assertEquals(120.00, plansDetails.getPlans().getAnnualFee().getAnnualFee());
        assertEquals(12.00, plansDetails.getPlans().getMonthlyFee().getMonthlyFee());
        assertTrue(plansDetails.getPlans().getActive().getActive());
        assertFalse(plansDetails.getPlans().getPromoted().getPromoted());
    }

    @Test
    public void testPlansDetailsConstructor_NullPlan_ShouldCreatePlansDetailsWithNullPlan() {

        PlansDetails plansDetails = new PlansDetails(null);

        assertNotNull(plansDetails);
        assertNull(plansDetails.getPlans());
    }

    @Test
    public void testPlansDetailsConstructor_SetPlansProperty_ShouldStoreCorrectPlan() {

        Plans testPlan = createTestPlan("Another Test Plan");

        PlansDetails plansDetails = new PlansDetails(testPlan);

        assertSame(testPlan, plansDetails.getPlans());
    }

    @Test
    public void testPlansDetailsConstructor_MultiplePlans_ShouldCreateDifferentInstances() {

        Plans plan1 = createTestPlan("Plan 1");
        Plans plan2 = createTestPlan("Plan 2");

        PlansDetails plansDetails1 = new PlansDetails(plan1);
        PlansDetails plansDetails2 = new PlansDetails(plan2);

        assertNotEquals(plansDetails1, plansDetails2);
        assertNotEquals(plansDetails1.getPlans(), plansDetails2.getPlans());
        assertEquals("Plan 1", plansDetails1.getPlans().getName().getName());
        assertEquals("Plan 2", plansDetails2.getPlans().getName().getName());
    }

    @Test
    public void testPlansDetailsLombokGeneratedMethods_ShouldWork() {

        Plans testPlan = createTestPlan("Lombok Test Plan");
        PlansDetails plansDetails = new PlansDetails(testPlan);

        Plans retrievedPlan = plansDetails.getPlans();
        assertSame(testPlan, retrievedPlan);

        Plans newPlan = createTestPlan("New Plan");
        plansDetails.setPlans(newPlan);
        assertEquals(newPlan, plansDetails.getPlans());
        assertEquals("New Plan", plansDetails.getPlans().getName().getName());

        assertDoesNotThrow(() -> {
            String toString = plansDetails.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("PlansDetails"));
        });

        PlansDetails anotherPlansDetails = new PlansDetails(newPlan);
        assertEquals(plansDetails, anotherPlansDetails);
        assertEquals(plansDetails.hashCode(), anotherPlansDetails.hashCode());
    }

    @Test
    public void testPlansDetailsConstructor_CoverageOfConstructorLine() {

        Plans testPlan = createTestPlan("Coverage Test Plan");

        PlansDetails plansDetails = new PlansDetails(testPlan);

        assertSame(testPlan, plansDetails.getPlans());
        
        PlansDetails nullPlansDetails = new PlansDetails(null);
        assertNull(nullPlansDetails.getPlans());
    }
}
