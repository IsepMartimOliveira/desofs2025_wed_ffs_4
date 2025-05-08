package PlansTest.model;


import com.example.psoft_22_23_project.plansmanagement.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlansTest {



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
        promotedStatus.setPromoted(false);

        return new Plans(planName, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, activeStatus, promotedStatus);
    }

    @Test
    public void createPlanTest()     {
        Name name = new Name();
        name.setName("Plan Name");

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

        Active active = new Active();
        active.setActive(true);

        Promoted promoted = new Promoted();
        promoted.setPromoted(false);

        Plans plan = new Plans(name, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, active, promoted);

        assertEquals("Plan Name", plan.getName().getName());
        assertEquals("Plan Description", plan.getDescription().getDescription());
        assertEquals("100", plan.getNumberOfMinutes().getNumberOfMinutes());
        assertEquals(5, plan.getMaximumNumberOfUsers().getMaximumNumberOfUsers());
        assertEquals(10, plan.getMusicCollection().getMusicCollection());
        assertEquals("personalized", plan.getMusicSuggestion().getMusicSuggestion());
        assertEquals(100.00, plan.getAnnualFee().getAnnualFee());
        assertEquals(10.00, plan.getMonthlyFee().getMonthlyFee());
        assertEquals(true, plan.getActive().getActive());
        assertEquals(false, plan.getPromoted().getPromoted());

        assertEquals(1, plan.getFeeRevisions().size());
        FeeRevision feeRevision = plan.getFeeRevisions().get(0);
        assertEquals(100.00, feeRevision.getAnnualFee());
        assertEquals(10.00, feeRevision.getMonthlyFee());
    }
    @Test
    public void testInvalidName() {
        Name name = new Name();
        assertThrows(IllegalArgumentException.class, () -> name.setName(""));
    }

    @Test
    public void testInvalidDescription() {
        Description description = new Description();
        assertThrows(IllegalArgumentException.class, () -> description.setDescription(""));
    }

    @Test
    public void testInvalidNumberOfMinutes() {
        NumberOfMinutes numberOfMinutes = new NumberOfMinutes();
        assertThrows(IllegalArgumentException.class, () -> numberOfMinutes.setNumberOfMinutes(""));
        assertThrows(IllegalArgumentException.class, () -> numberOfMinutes.setNumberOfMinutes("-10"));
        assertThrows(IllegalArgumentException.class, () -> numberOfMinutes.setNumberOfMinutes("not_unlimited"));

    }

    @Test
    public void testInvalidMaxUser() {
        MaximumNumberOfUsers max = new MaximumNumberOfUsers();
        assertThrows(IllegalArgumentException.class, () -> max.setMaximumNumberOfUsers(null));
    }

    @Test
    public void testInvalidMusicCol() {
        MusicCollection attribute = new MusicCollection();
        assertThrows(IllegalArgumentException.class, () -> attribute.setMusicCollection(null));
    }

    @Test
    public void testInvalidMusicSug() {
        MusicSuggestion attribute = new MusicSuggestion();
        assertThrows(IllegalArgumentException.class, () -> attribute.setMusicSuggestion("invalid"));
    }

    @Test
    public void testInvalidAnnualFee() {
        AnnualFee attribute = new AnnualFee();
        assertThrows(IllegalArgumentException.class, () -> attribute.setAnnualFee(-1));
    }

    @Test
    public void testInvalidMonFee() {
        MonthlyFee attribute = new MonthlyFee();
        assertThrows(IllegalArgumentException.class, () -> attribute.setMonthlyFee(-1));
    }

    @Test
    public void testUpdatePlan() {

        Plans plans = createPlan("Created Plan");

        String newDescription = "New Description";
        Integer newMaximumNumberOfUsers = 10;
        String newNumberOfMinutes = "12";
        Integer newMusicCollections = 7;
        String newMusicSuggestions = "automatic";
        Boolean newActive = true;
        Boolean newPromoted  = true;

        plans.updateData(0,newDescription,newMaximumNumberOfUsers,newNumberOfMinutes,newMusicCollections,newMusicSuggestions,newActive,newPromoted);

        assertEquals(newDescription, plans.getDescription().getDescription());
        assertEquals(newMaximumNumberOfUsers, plans.getMaximumNumberOfUsers().getMaximumNumberOfUsers());
        assertEquals(newNumberOfMinutes, plans.getNumberOfMinutes().getNumberOfMinutes());
        assertEquals(newMusicCollections, plans.getMusicCollection().getMusicCollection());
        assertEquals(newMusicSuggestions, plans.getMusicSuggestion().getMusicSuggestion());
        assertEquals(newActive, plans.getActive().getActive());
        assertEquals(newPromoted, plans.getPromoted().getPromoted());
    }

    @Test
    public void testUpdatePlanMoney() {
        Plans plans = createPlan("Created Plan");

        Double aFee = 4.0;
        Double mFee = 12.0;
        String user = "chico";

        plans.moneyData(0,aFee,mFee,user);

        assertEquals(aFee, plans.getAnnualFee().getAnnualFee());
        assertEquals(mFee, plans.getMonthlyFee().getMonthlyFee());
        assertEquals(user, plans.getFeeRevisions().get(1).getUser());
    }

}
