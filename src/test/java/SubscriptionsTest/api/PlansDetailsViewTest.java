package SubscriptionsTest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.psoft_22_23_project.subscriptionsmanagement.api.PlansDetailsView;

public class PlansDetailsViewTest {

    private PlansDetailsView plansDetailsView;

    @BeforeEach
    void setUp() {
        plansDetailsView = new PlansDetailsView();
    }

    @Test
    void testPlansDetailsView_SetAndGetName_ShouldWork() {

        String expectedName = "Premium Plan";

        plansDetailsView.setName(expectedName);

        assertEquals(expectedName, plansDetailsView.getName());
    }

    @Test
    void testPlansDetailsView_SetAndGetDescription_ShouldWork() {

        String expectedDescription = "Premium plan with unlimited features";

        plansDetailsView.setDescription(expectedDescription);

        assertEquals(expectedDescription, plansDetailsView.getDescription());
    }

    @Test
    void testPlansDetailsView_SetAndGetNumberOfMinutes_ShouldWork() {

        String expectedMinutes = "unlimited";

        plansDetailsView.setNumberOfMinutes(expectedMinutes);

        assertEquals(expectedMinutes, plansDetailsView.getNumberOfMinutes());
    }

    @Test
    void testPlansDetailsView_SetAndGetMaximumNumberOfUsers_ShouldWork() {

        String expectedMaxUsers = "10";

        plansDetailsView.setMaximumNumberOfUsers(expectedMaxUsers);

        assertEquals(expectedMaxUsers, plansDetailsView.getMaximumNumberOfUsers());
    }

    @Test
    void testPlansDetailsView_SetAndGetMusicCollection_ShouldWork() {

        String expectedMusicCollection = "1000";

        plansDetailsView.setMusicCollection(expectedMusicCollection);

        assertEquals(expectedMusicCollection, plansDetailsView.getMusicCollection());
    }

    @Test
    void testPlansDetailsView_SetAndGetMusicSuggestion_ShouldWork() {

        String expectedMusicSuggestion = "personalized";

        plansDetailsView.setMusicSuggestion(expectedMusicSuggestion);

        assertEquals(expectedMusicSuggestion, plansDetailsView.getMusicSuggestion());
    }

    @Test
    void testPlansDetailsView_SetAndGetMonthlyFee_ShouldWork() {

        String expectedMonthlyFee = "12.99";

        plansDetailsView.setMonthlyFee(expectedMonthlyFee);

        assertEquals(expectedMonthlyFee + " €", plansDetailsView.getMonthlyFee());
    }

    @Test
    void testPlansDetailsView_SetAndGetAnnualFee_ShouldWork() {

        String expectedAnnualFee = "129.99";

        plansDetailsView.setAnnualFee(expectedAnnualFee);

        assertEquals(expectedAnnualFee + " €", plansDetailsView.getAnnualFee());
    }

    @Test
    void testPlansDetailsView_SetAndGetActive_ShouldWork() {

        String expectedActive = "true";

        plansDetailsView.setActive(expectedActive);

        assertEquals(expectedActive, plansDetailsView.getActive());
    }

    @Test
    void testPlansDetailsView_SetAndGetPromoted_ShouldWork() {

        String expectedPromoted = "false";

        plansDetailsView.setPromoted(expectedPromoted);

        assertEquals(expectedPromoted, plansDetailsView.getPromoted());
    }

    @Test
    void testPlansDetailsView_GetMonthlyFeeWithEuroSymbol_ShouldAddEuroSymbol() {

        plansDetailsView.setMonthlyFee("12.99");

        String result = plansDetailsView.getMonthlyFee();

        assertEquals("12.99 €", result);
    }

    @Test
    void testPlansDetailsView_GetAnnualFeeWithEuroSymbol_ShouldAddEuroSymbol() {

        plansDetailsView.setAnnualFee("129.99");

        String result = plansDetailsView.getAnnualFee();

        assertEquals("129.99 €", result);
    }

    @Test
    void testPlansDetailsView_GetMonthlyFeeWithNullValue_ShouldHandleNull() {

        plansDetailsView.setMonthlyFee(null);

        String result = plansDetailsView.getMonthlyFee();

        assertNull(result);
    }

    @Test
    void testPlansDetailsView_GetAnnualFeeWithNullValue_ShouldHandleNull() {

        plansDetailsView.setAnnualFee(null);

        String result = plansDetailsView.getAnnualFee();

        assertNull(result);
    }

    @Test
    void testPlansDetailsView_GetMonthlyFeeWithEmptyString_ShouldHandleEmpty() {

        plansDetailsView.setMonthlyFee("");

        String result = plansDetailsView.getMonthlyFee();

        assertEquals(" €", result);
    }

    @Test
    void testPlansDetailsView_GetAnnualFeeWithEmptyString_ShouldHandleEmpty() {

        plansDetailsView.setAnnualFee("");

        String result = plansDetailsView.getAnnualFee();

        assertEquals(" €", result);
    }

    @Test
    void testPlansDetailsView_AllFieldsNull_ShouldReturnNull() {

        PlansDetailsView view = new PlansDetailsView();

        assertNull(view.getName());
        assertNull(view.getDescription());
        assertNull(view.getNumberOfMinutes());
        assertNull(view.getMaximumNumberOfUsers());
        assertNull(view.getMusicCollection());
        assertNull(view.getMusicSuggestion());

        assertNull(view.getActive());
        assertNull(view.getPromoted());
    }

    @Test
    void testPlansDetailsView_SetAllFields_ShouldWorkCorrectly() {

        String name = "Ultimate Plan";
        String description = "Ultimate plan with all features";
        String numberOfMinutes = "unlimited";
        String maximumNumberOfUsers = "50";
        String musicCollection = "5000";
        String musicSuggestion = "AI-powered";
        String monthlyFee = "29.99";
        String annualFee = "299.99";
        String active = "true";
        String promoted = "true";

        plansDetailsView.setName(name);
        plansDetailsView.setDescription(description);
        plansDetailsView.setNumberOfMinutes(numberOfMinutes);
        plansDetailsView.setMaximumNumberOfUsers(maximumNumberOfUsers);
        plansDetailsView.setMusicCollection(musicCollection);
        plansDetailsView.setMusicSuggestion(musicSuggestion);
        plansDetailsView.setMonthlyFee(monthlyFee);
        plansDetailsView.setAnnualFee(annualFee);
        plansDetailsView.setActive(active);
        plansDetailsView.setPromoted(promoted);

        assertEquals(name, plansDetailsView.getName());
        assertEquals(description, plansDetailsView.getDescription());
        assertEquals(numberOfMinutes, plansDetailsView.getNumberOfMinutes());
        assertEquals(maximumNumberOfUsers, plansDetailsView.getMaximumNumberOfUsers());
        assertEquals(musicCollection, plansDetailsView.getMusicCollection());
        assertEquals(musicSuggestion, plansDetailsView.getMusicSuggestion());
        assertEquals("29.99 €", plansDetailsView.getMonthlyFee());
        assertEquals("299.99 €", plansDetailsView.getAnnualFee());
        assertEquals(active, plansDetailsView.getActive());
        assertEquals(promoted, plansDetailsView.getPromoted());
    }

    @Test
    void testPlansDetailsView_OverwriteFields_ShouldWork() {

        plansDetailsView.setName("Old Plan");
        plansDetailsView.setDescription("Old description");
        plansDetailsView.setMonthlyFee("10.00");

        plansDetailsView.setName("New Plan");
        plansDetailsView.setDescription("New description");
        plansDetailsView.setMonthlyFee("15.00");

        assertEquals("New Plan", plansDetailsView.getName());
        assertEquals("New description", plansDetailsView.getDescription());
        assertEquals("15.00 €", plansDetailsView.getMonthlyFee());
    }

    @Test
    void testPlansDetailsView_SetFieldsToNull_ShouldWork() {

        plansDetailsView.setName("Test Plan");
        plansDetailsView.setDescription("Test description");
        plansDetailsView.setActive("true");

        plansDetailsView.setName(null);
        plansDetailsView.setDescription(null);
        plansDetailsView.setActive(null);

        assertNull(plansDetailsView.getName());
        assertNull(plansDetailsView.getDescription());
        assertNull(plansDetailsView.getActive());
    }

    @Test
    void testPlansDetailsView_SetFieldsToEmptyString_ShouldWork() {

        plansDetailsView.setName("");
        plansDetailsView.setDescription("");
        plansDetailsView.setNumberOfMinutes("");
        plansDetailsView.setMaximumNumberOfUsers("");
        plansDetailsView.setMusicCollection("");
        plansDetailsView.setMusicSuggestion("");
        plansDetailsView.setMonthlyFee("");
        plansDetailsView.setAnnualFee("");
        plansDetailsView.setActive("");
        plansDetailsView.setPromoted("");

        assertEquals("", plansDetailsView.getName());
        assertEquals("", plansDetailsView.getDescription());
        assertEquals("", plansDetailsView.getNumberOfMinutes());
        assertEquals("", plansDetailsView.getMaximumNumberOfUsers());
        assertEquals("", plansDetailsView.getMusicCollection());
        assertEquals("", plansDetailsView.getMusicSuggestion());
        assertEquals(" €", plansDetailsView.getMonthlyFee());
        assertEquals(" €", plansDetailsView.getAnnualFee());
        assertEquals("", plansDetailsView.getActive());
        assertEquals("", plansDetailsView.getPromoted());
    }

    @Test
    void testPlansDetailsView_ToStringEqualsHashCode_ShouldWork() {

        PlansDetailsView view1 = new PlansDetailsView();
        view1.setName("Premium");
        view1.setMonthlyFee("12.99");
        view1.setActive("true");

        PlansDetailsView view2 = new PlansDetailsView();
        view2.setName("Premium");
        view2.setMonthlyFee("12.99");
        view2.setActive("true");

        assertNotNull(view1.toString());
        assertEquals(view1, view2);
        assertEquals(view1.hashCode(), view2.hashCode());
    }

    @Test
    void testPlansDetailsView_NumberFormats_ShouldWork() {

        plansDetailsView.setNumberOfMinutes("120");
        plansDetailsView.setMaximumNumberOfUsers("5");
        plansDetailsView.setMusicCollection("1000");

        assertEquals("120", plansDetailsView.getNumberOfMinutes());
        assertEquals("5", plansDetailsView.getMaximumNumberOfUsers());
        assertEquals("1000", plansDetailsView.getMusicCollection());
    }

    @Test
    void testPlansDetailsView_BooleanStringValues_ShouldWork() {

        plansDetailsView.setActive("true");
        plansDetailsView.setPromoted("false");

        assertEquals("true", plansDetailsView.getActive());
        assertEquals("false", plansDetailsView.getPromoted());

        plansDetailsView.setActive("false");
        plansDetailsView.setPromoted("true");

        assertEquals("false", plansDetailsView.getActive());
        assertEquals("true", plansDetailsView.getPromoted());
    }
}
