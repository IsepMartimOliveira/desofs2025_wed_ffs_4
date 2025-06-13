package SubscriptionsTest.api;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.psoft_22_23_project.subscriptionsmanagement.api.CreateSubscriptionsRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class CreateSubscriptionsRequestTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCreateSubscriptionsRequest_ValidInputs_ShouldPassValidation() {
      
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest("Test Plan", "monthly");

        Set<ConstraintViolation<CreateSubscriptionsRequest>> violations = validator.validate(request);

        assertEquals(0, violations.size());
        assertEquals("Test Plan", request.getName());
        assertEquals("monthly", request.getPaymentType());
    }

    @Test
    void testCreateSubscriptionsRequest_ValidAnnualPayment_ShouldPassValidation() {
        
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest("Premium Plan", "annually");

        Set<ConstraintViolation<CreateSubscriptionsRequest>> violations = validator.validate(request);

        assertEquals(0, violations.size());
        assertEquals("Premium Plan", request.getName());
        assertEquals("annually", request.getPaymentType());
    }

    @Test
    void testCreateSubscriptionsRequest_InvalidPaymentType_ShouldFailValidation() {
       
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest("Test Plan", "weekly");

        Set<ConstraintViolation<CreateSubscriptionsRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<CreateSubscriptionsRequest> violation = violations.iterator().next();
        assertEquals("paymentType", violation.getPropertyPath().toString());
    }

    @Test
    void testCreateSubscriptionsRequest_EmptyPaymentType_ShouldFailValidation() {
       
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest("Test Plan", "");

        Set<ConstraintViolation<CreateSubscriptionsRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<CreateSubscriptionsRequest> violation = violations.iterator().next();
        assertEquals("paymentType", violation.getPropertyPath().toString());
    }

    @Test
    void testCreateSubscriptionsRequest_NullPaymentType_ShouldFailValidation() {
       
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest("Test Plan", null);

        Set<ConstraintViolation<CreateSubscriptionsRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<CreateSubscriptionsRequest> violation = violations.iterator().next();
        assertEquals("paymentType", violation.getPropertyPath().toString());
    }

    @Test
    void testCreateSubscriptionsRequest_NoArgsConstructor_ShouldWork() {
        
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest();

        request.setName("Test Plan");
        request.setPaymentType("monthly");

        assertEquals("Test Plan", request.getName());
        assertEquals("monthly", request.getPaymentType());
    }

    @Test
    void testCreateSubscriptionsRequest_AllArgsConstructor_ShouldWork() {
        
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest("Test Plan", "annually");

        assertEquals("Test Plan", request.getName());
        assertEquals("annually", request.getPaymentType());
    }

    @Test
    void testCreateSubscriptionsRequest_SettersAndGetters_ShouldWork() {
        
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest();

        request.setName("Basic Plan");
        request.setPaymentType("monthly");

        assertEquals("Basic Plan", request.getName());
        assertEquals("monthly", request.getPaymentType());
    }

    @Test
    void testCreateSubscriptionsRequest_NullName_ShouldNotFailValidation() {
      
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest(null, "monthly");

        Set<ConstraintViolation<CreateSubscriptionsRequest>> violations = validator.validate(request);

        assertEquals(0, violations.size());
        assertNull(request.getName());
        assertEquals("monthly", request.getPaymentType());
    }

    @Test
    void testCreateSubscriptionsRequest_EmptyName_ShouldNotFailValidation() {
     
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest("", "annually");

        Set<ConstraintViolation<CreateSubscriptionsRequest>> violations = validator.validate(request);

        assertEquals(0, violations.size());
        assertEquals("", request.getName());
        assertEquals("annually", request.getPaymentType());
    }

    @Test
    void testCreateSubscriptionsRequest_BothFieldsNull_ShouldFailOnlyPaymentTypeValidation() {
        
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest(null, null);

        Set<ConstraintViolation<CreateSubscriptionsRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<CreateSubscriptionsRequest> violation = violations.iterator().next();
        assertEquals("paymentType", violation.getPropertyPath().toString());
    }

    @Test
    void testCreateSubscriptionsRequest_CaseSensitivePaymentType_ShouldFailValidation() {
       
        CreateSubscriptionsRequest request1 = new CreateSubscriptionsRequest("Test Plan", "Monthly");
        CreateSubscriptionsRequest request2 = new CreateSubscriptionsRequest("Test Plan", "ANNUALLY");

        Set<ConstraintViolation<CreateSubscriptionsRequest>> violations1 = validator.validate(request1);
        Set<ConstraintViolation<CreateSubscriptionsRequest>> violations2 = validator.validate(request2);

        assertEquals(1, violations1.size());
        assertEquals(1, violations2.size());
    }

    @Test
    void testCreateSubscriptionsRequest_ToStringEqualsHashCode_ShouldWork() {
     
        CreateSubscriptionsRequest request1 = new CreateSubscriptionsRequest("Test Plan", "monthly");
        CreateSubscriptionsRequest request2 = new CreateSubscriptionsRequest("Test Plan", "monthly");
 
        assertNotNull(request1.toString());
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    
    }
}
