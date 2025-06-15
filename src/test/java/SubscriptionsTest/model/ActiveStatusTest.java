package SubscriptionsTest.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.psoft_22_23_project.subscriptionsmanagement.model.ActiveStatus;

public class ActiveStatusTest {

    @Test
    public void testDefaultConstructor_ShouldCreateActiveStatusWithTrueValue() {

        ActiveStatus activeStatus = new ActiveStatus();

        assertNotNull(activeStatus);
        assertTrue(activeStatus.isActive());
    }

    @Test
    public void testParameterizedConstructor_WithTrue_ShouldCreateActiveStatusWithTrueValue() {

        ActiveStatus activeStatus = new ActiveStatus(true);

        assertNotNull(activeStatus);
        assertTrue(activeStatus.isActive());
    }

    @Test
    public void testParameterizedConstructor_WithFalse_ShouldCreateActiveStatusWithFalseValue() {

        ActiveStatus activeStatus = new ActiveStatus(false);

        assertNotNull(activeStatus);
        assertFalse(activeStatus.isActive());
    }

    @Test
    public void testSetActive_ShouldUpdateActiveValue() {

        ActiveStatus activeStatus = new ActiveStatus();
        assertTrue(activeStatus.isActive());

        activeStatus.setActive(false);

        assertFalse(activeStatus.isActive());

        activeStatus.setActive(true);

        assertTrue(activeStatus.isActive());
    }

    @Test
    public void testDefaultVsParameterizedConstructor_ShouldBehaveSame() {

        ActiveStatus defaultConstructed = new ActiveStatus();
        ActiveStatus parameterConstructed = new ActiveStatus(true);

        assertEquals(defaultConstructed.isActive(), parameterConstructed.isActive());
        assertTrue(defaultConstructed.isActive());
        assertTrue(parameterConstructed.isActive());
    }

    @Test
    public void testLombokGeneratedMethods_ShouldWork() {

        ActiveStatus activeStatus1 = new ActiveStatus();
        ActiveStatus activeStatus2 = new ActiveStatus(true);
        ActiveStatus activeStatus3 = new ActiveStatus(false);

        assertDoesNotThrow(() -> {
            String toString1 = activeStatus1.toString();
            String toString2 = activeStatus2.toString();
            String toString3 = activeStatus3.toString();
            
            assertNotNull(toString1);
            assertNotNull(toString2);
            assertNotNull(toString3);
            assertTrue(toString1.contains("ActiveStatus"));
            assertTrue(toString2.contains("ActiveStatus"));
            assertTrue(toString3.contains("ActiveStatus"));
        });

        assertNotEquals(activeStatus1, activeStatus2);
        assertNotEquals(activeStatus1, activeStatus3);
        assertNotEquals(activeStatus1.hashCode(), activeStatus2.hashCode());
        
        assertEquals(activeStatus1, activeStatus1);
        assertEquals(activeStatus1.hashCode(), activeStatus1.hashCode());
        
        assertEquals(activeStatus1.isActive(), activeStatus2.isActive());
        assertNotEquals(activeStatus1.isActive(), activeStatus3.isActive());
    }

    @Test
    public void testConstructorCoverage_DefaultConstructorSpecifically() {

        ActiveStatus activeStatus = new ActiveStatus();

        assertTrue(activeStatus.isActive());
        
        activeStatus.setActive(false);
        assertFalse(activeStatus.isActive());
        
        ActiveStatus anotherActiveStatus = new ActiveStatus();
        assertTrue(anotherActiveStatus.isActive());
    }

    @Test
    public void testActiveStatusInstances_ShouldBeIndependent() {

        ActiveStatus activeStatus1 = new ActiveStatus();
        ActiveStatus activeStatus2 = new ActiveStatus();

        assertTrue(activeStatus1.isActive());
        assertTrue(activeStatus2.isActive());

        activeStatus1.setActive(false);

        assertFalse(activeStatus1.isActive());
        assertTrue(activeStatus2.isActive());
    }
}
