package SubscriptionsTest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.psoft_22_23_project.subscriptionsmanagement.api.SubscriptionsView;

public class SubscriptionsViewTest {

    private SubscriptionsView subscriptionsView;

    @BeforeEach
    void setUp() {
        subscriptionsView = new SubscriptionsView();
    }

    @Test
    void testSubscriptionsView_SetAndGetUsername_ShouldWork() {

        String expectedUsername = "testuser@example.com";

        subscriptionsView.setUsername(expectedUsername);

        assertEquals(expectedUsername, subscriptionsView.getUsername());
    }

    @Test
    void testSubscriptionsView_SetAndGetPlanName_ShouldWork() {

        String expectedPlanName = "Premium Plan";

        subscriptionsView.setPlanName(expectedPlanName);

        assertEquals(expectedPlanName, subscriptionsView.getPlanName());
    }

    @Test
    void testSubscriptionsView_SetAndGetPlanDescription_ShouldWork() {

        String expectedDescription = "Premium plan with unlimited access";

        subscriptionsView.setPlanDescription(expectedDescription);

        assertEquals(expectedDescription, subscriptionsView.getPlanDescription());
    }

    @Test
    void testSubscriptionsView_SetAndGetPaymentType_ShouldWork() {

        String expectedPaymentType = "monthly";

        subscriptionsView.setPaymentType(expectedPaymentType);

        assertEquals(expectedPaymentType, subscriptionsView.getPaymentType());
    }

    @Test
    void testSubscriptionsView_SetAndGetStartDate_ShouldWork() {

        String expectedStartDate = "2025-01-01";

        subscriptionsView.setStartDate(expectedStartDate);

        assertEquals(expectedStartDate, subscriptionsView.getStartDate());
    }

    @Test
    void testSubscriptionsView_SetAndGetEndDate_ShouldWork() {

        String expectedEndDate = "2025-12-31";

        subscriptionsView.setEndDate(expectedEndDate);

        assertEquals(expectedEndDate, subscriptionsView.getEndDate());
    }

    @Test
    void testSubscriptionsView_SetAndGetActiveStatus_ShouldWork() {

        String expectedActiveStatus = "true";

        subscriptionsView.setActiveStatus(expectedActiveStatus);

        assertEquals(expectedActiveStatus, subscriptionsView.getActiveStatus());
    }

    @Test
    void testSubscriptionsView_AllFieldsNull_ShouldReturnNull() {

        SubscriptionsView view = new SubscriptionsView();

        assertNull(view.getUsername());
        assertNull(view.getPlanName());
        assertNull(view.getPlanDescription());
        assertNull(view.getPaymentType());
        assertNull(view.getStartDate());
        assertNull(view.getEndDate());
        assertNull(view.getActiveStatus());
    }

    @Test
    void testSubscriptionsView_SetAllFields_ShouldWorkCorrectly() {

        String username = "user@test.com";
        String planName = "Basic Plan";
        String planDescription = "Basic plan with limited features";
        String paymentType = "annually";
        String startDate = "2025-06-01";
        String endDate = "2026-05-31";
        String activeStatus = "true";

        subscriptionsView.setUsername(username);
        subscriptionsView.setPlanName(planName);
        subscriptionsView.setPlanDescription(planDescription);
        subscriptionsView.setPaymentType(paymentType);
        subscriptionsView.setStartDate(startDate);
        subscriptionsView.setEndDate(endDate);
        subscriptionsView.setActiveStatus(activeStatus);

        assertEquals(username, subscriptionsView.getUsername());
        assertEquals(planName, subscriptionsView.getPlanName());
        assertEquals(planDescription, subscriptionsView.getPlanDescription());
        assertEquals(paymentType, subscriptionsView.getPaymentType());
        assertEquals(startDate, subscriptionsView.getStartDate());
        assertEquals(endDate, subscriptionsView.getEndDate());
        assertEquals(activeStatus, subscriptionsView.getActiveStatus());
    }

    @Test
    void testSubscriptionsView_OverwriteFields_ShouldWork() {

        subscriptionsView.setUsername("olduser@test.com");
        subscriptionsView.setPlanName("Old Plan");

        subscriptionsView.setUsername("newuser@test.com");
        subscriptionsView.setPlanName("New Plan");

        assertEquals("newuser@test.com", subscriptionsView.getUsername());
        assertEquals("New Plan", subscriptionsView.getPlanName());
    }

    @Test
    void testSubscriptionsView_SetFieldsToNull_ShouldWork() {

        subscriptionsView.setUsername("user@test.com");
        subscriptionsView.setPlanName("Test Plan");
        subscriptionsView.setPaymentType("monthly");

        subscriptionsView.setUsername(null);
        subscriptionsView.setPlanName(null);
        subscriptionsView.setPaymentType(null);

        assertNull(subscriptionsView.getUsername());
        assertNull(subscriptionsView.getPlanName());
        assertNull(subscriptionsView.getPaymentType());
    }

    @Test
    void testSubscriptionsView_SetFieldsToEmptyString_ShouldWork() {

        subscriptionsView.setUsername("");
        subscriptionsView.setPlanName("");
        subscriptionsView.setPlanDescription("");
        subscriptionsView.setPaymentType("");
        subscriptionsView.setStartDate("");
        subscriptionsView.setEndDate("");
        subscriptionsView.setActiveStatus("");

        assertEquals("", subscriptionsView.getUsername());
        assertEquals("", subscriptionsView.getPlanName());
        assertEquals("", subscriptionsView.getPlanDescription());
        assertEquals("", subscriptionsView.getPaymentType());
        assertEquals("", subscriptionsView.getStartDate());
        assertEquals("", subscriptionsView.getEndDate());
        assertEquals("", subscriptionsView.getActiveStatus());
    }

    @Test
    void testSubscriptionsView_ToStringEqualsHashCode_ShouldWork() {

        SubscriptionsView view1 = new SubscriptionsView();
        view1.setUsername("test@example.com");
        view1.setPlanName("Premium");
        view1.setPaymentType("monthly");

        SubscriptionsView view2 = new SubscriptionsView();
        view2.setUsername("test@example.com");
        view2.setPlanName("Premium");
        view2.setPaymentType("monthly");

        assertNotNull(view1.toString());
        assertEquals(view1, view2);
        assertEquals(view1.hashCode(), view2.hashCode());
    }

    @Test
    void testSubscriptionsView_DifferentObjectsNotEqual_ShouldWork() {

        SubscriptionsView view1 = new SubscriptionsView();
        view1.setUsername("user1@example.com");
        view1.setPlanName("Plan1");

        SubscriptionsView view2 = new SubscriptionsView();
        view2.setUsername("user2@example.com");
        view2.setPlanName("Plan2");

        assertNotNull(view1);
        assertNotNull(view2);
    }

    @Test
    void testSubscriptionsView_PaymentTypeCases_ShouldWork() {

        subscriptionsView.setPaymentType("monthly");
        assertEquals("monthly", subscriptionsView.getPaymentType());

        subscriptionsView.setPaymentType("annually");
        assertEquals("annually", subscriptionsView.getPaymentType());

        subscriptionsView.setPaymentType("MONTHLY");
        assertEquals("MONTHLY", subscriptionsView.getPaymentType());
    }

    @Test
    void testSubscriptionsView_ActiveStatusCases_ShouldWork() {

        subscriptionsView.setActiveStatus("true");
        assertEquals("true", subscriptionsView.getActiveStatus());

        subscriptionsView.setActiveStatus("false");
        assertEquals("false", subscriptionsView.getActiveStatus());

        subscriptionsView.setActiveStatus("active");
        assertEquals("active", subscriptionsView.getActiveStatus());

        subscriptionsView.setActiveStatus("inactive");
        assertEquals("inactive", subscriptionsView.getActiveStatus());
    }
}
