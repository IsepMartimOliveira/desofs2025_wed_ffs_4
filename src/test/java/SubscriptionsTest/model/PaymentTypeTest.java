package SubscriptionsTest.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;

public class PaymentTypeTest {

    @Test
    public void testDefaultConstructor_ShouldCreateEmptyPaymentType() {

        PaymentType paymentType = new PaymentType();

        assertNotNull(paymentType);
        assertNull(paymentType.getPaymentType());
    }

    @Test
    public void testParameterizedConstructor_ValidPaymentType_ShouldCreatePaymentType() {

        String paymentTypeValue = "monthly";

        PaymentType paymentType = new PaymentType(paymentTypeValue);

        assertNotNull(paymentType);
        assertEquals("monthly", paymentType.getPaymentType());
    }

    @Test
    public void testParameterizedConstructor_NullPaymentType_ShouldCreatePaymentTypeWithNull() {

        PaymentType paymentType = new PaymentType(null);

        assertNotNull(paymentType);
        assertNull(paymentType.getPaymentType());
    }

    @Test
    public void testParameterizedConstructor_EmptyPaymentType_ShouldCreatePaymentTypeWithEmpty() {

        PaymentType paymentType = new PaymentType("");

        assertNotNull(paymentType);
        assertEquals("", paymentType.getPaymentType());
    }

    @Test
    public void testParameterizedConstructor_DifferentPaymentTypes_ShouldCreateCorrectly() {

        String[] paymentTypes = {"monthly", "annually", "weekly", "daily"};

        for (String type : paymentTypes) {
            PaymentType paymentType = new PaymentType(type);
            assertNotNull(paymentType);
            assertEquals(type, paymentType.getPaymentType());
        }
    }

    @Test
    public void testLombokGeneratedMethods_ShouldWork() {

        PaymentType paymentType = new PaymentType("monthly");

        assertEquals("monthly", paymentType.getPaymentType());

        paymentType.setPaymentType("annually");
        assertEquals("annually", paymentType.getPaymentType());

        assertDoesNotThrow(() -> {
            String toString = paymentType.toString();
            assertNotNull(toString);

        });

        PaymentType anotherPaymentType = new PaymentType("annually");
        assertEquals(paymentType, anotherPaymentType);
        assertEquals(paymentType.hashCode(), anotherPaymentType.hashCode());

        PaymentType differentPaymentType = new PaymentType("monthly");
        assertNotEquals(paymentType, differentPaymentType);
        assertNotEquals(paymentType.hashCode(), differentPaymentType.hashCode());
    }

    @Test
    public void testDefaultConstructorThenSetter_ShouldWork() {

        PaymentType paymentType = new PaymentType();
        assertNull(paymentType.getPaymentType());

        paymentType.setPaymentType("monthly");
        assertEquals("monthly", paymentType.getPaymentType());
    }

    @Test
    public void testConstructorCoverage_BothConstructors() {

        PaymentType defaultPaymentType = new PaymentType();
        assertNotNull(defaultPaymentType);
        assertNull(defaultPaymentType.getPaymentType());

        PaymentType paramPaymentType = new PaymentType("test");
        assertNotNull(paramPaymentType);
        assertEquals("test", paramPaymentType.getPaymentType());

        assertNotEquals(defaultPaymentType, paramPaymentType);
    }

    @Test
    public void testPaymentTypeAsEmbeddable_ShouldWorkCorrectly() {

        PaymentType paymentType1 = new PaymentType("monthly");
        PaymentType paymentType2 = new PaymentType("monthly");

        assertEquals(paymentType1, paymentType2);
        assertEquals(paymentType1.hashCode(), paymentType2.hashCode());

        paymentType2.setPaymentType("annually");
        assertNotEquals(paymentType1, paymentType2);
    }
}
