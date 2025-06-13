package SubscriptionsTest.model;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.psoft_22_23_project.subscriptionsmanagement.model.EndDate;

public class EndDateTest {

    @Test
    public void testEndDateDefaultConstructor_ShouldCreateEmptyEndDate() {

        EndDate endDate = new EndDate();

        assertNotNull(endDate);
        assertTrue(endDate instanceof EndDate);
        assertNull(endDate.getEndDate());
    }

    @Test
    public void testEndDateParameterizedConstructor_AnnuallyPayment_ShouldSetCorrectEndDate() {

        String paymentType = "annually";
        LocalDate expectedEndDate = LocalDate.now().plusYears(1);

        EndDate endDate = new EndDate(paymentType);

        assertNotNull(endDate);
        assertNotNull(endDate.getEndDate());
        assertEquals(expectedEndDate.toString(), endDate.getEndDate());
    }

    @Test
    public void testEndDateParameterizedConstructor_MonthlyPayment_ShouldSetNotYetDefined() {

        String paymentType = "monthly";

        EndDate endDate = new EndDate(paymentType);

        assertNotNull(endDate);
        assertNotNull(endDate.getEndDate());
        assertEquals("Not yet defined", endDate.getEndDate());
    }

    @Test
    public void testEndDateParameterizedConstructor_UnknownPayment_ShouldSetNotYetDefined() {

        String paymentType = "unknown";

        EndDate endDate = new EndDate(paymentType);

        assertNotNull(endDate);
        assertNotNull(endDate.getEndDate());
        assertEquals("Not yet defined", endDate.getEndDate());
    }

    @Test
    public void testEndDateDefaultConstructor_CoverageOfConstructorLine() {

        EndDate endDate = new EndDate();

        assertNotNull(endDate);
        
        endDate.setEndDate("2024-12-31");
        assertEquals("2024-12-31", endDate.getEndDate());
        
        endDate.setEndDate(null);
        assertNull(endDate.getEndDate());
    }

    @Test
    public void testEndDateLombokGeneratedMethods_ShouldWork() {
        EndDate endDate = new EndDate();
        String testDate = "2024-12-31";

        endDate.setEndDate(testDate);
        assertEquals(testDate, endDate.getEndDate());

        String retrievedDate = endDate.getEndDate();
        assertEquals(testDate, retrievedDate);

        assertDoesNotThrow(() -> {
            String toString = endDate.toString();
            assertNotNull(toString);
            assertTrue(toString.contains("EndDate"));
        });

        EndDate anotherEndDate = new EndDate();
        anotherEndDate.setEndDate(testDate);

        assertEquals(endDate.getEndDate(), anotherEndDate.getEndDate());

    }

    @Test
    public void testEndDateBothConstructors_CompareResults() {

        EndDate defaultEndDate = new EndDate();
        EndDate annualEndDate = new EndDate("annually");
        EndDate monthlyEndDate = new EndDate("monthly");

        assertNotNull(defaultEndDate);
        assertNotNull(annualEndDate);
        assertNotNull(monthlyEndDate);

        assertNull(defaultEndDate.getEndDate());

        assertNotNull(annualEndDate.getEndDate());
        assertEquals("Not yet defined", monthlyEndDate.getEndDate());

        defaultEndDate.setEndDate("Not yet defined");
        assertEquals(defaultEndDate.getEndDate(), monthlyEndDate.getEndDate());
    }
}
