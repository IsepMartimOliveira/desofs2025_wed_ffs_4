package SubscriptionsTest.api;


import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.server.ResponseStatusException;

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
import com.example.psoft_22_23_project.subscriptionsmanagement.api.CreateSubscriptionsRequest;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.PlansDetailsView;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.PlansDetailsViewMapper;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.SubscriptionsController;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.SubscriptionsView;
import com.example.psoft_22_23_project.subscriptionsmanagement.api.SubscriptionsViewMapper;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PaymentType;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.PlansDetails;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.subscriptionsmanagement.services.SubscriptionsService;
import com.example.psoft_22_23_project.usermanagement.model.User;

@ExtendWith(MockitoExtension.class)

public class SubscriptionsControllerTest {

    @Mock
    private SubscriptionsService subscriptionsService;

    @Mock
    private SubscriptionsViewMapper subscriptionsViewMapper;

    @Mock
    private PlansDetailsViewMapper plansDetailsViewMapper;


    @InjectMocks
    private SubscriptionsController subscriptionsController;

    private User testUser;
    private Plans testPlan;
    private Subscriptions testSubscription;
    private SubscriptionsView testSubscriptionsView;
    private PlansDetails testPlansDetails;
    private PlansDetailsView testPlansDetailsView;
    private CreateSubscriptionsRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testPlan = createTestPlan();
        testSubscription = createTestSubscription();
        testSubscriptionsView = createTestSubscriptionsView();
        testPlansDetails = new PlansDetails(testPlan);
        testPlansDetailsView = createTestPlansDetailsView();
        testCreateRequest = new CreateSubscriptionsRequest("Premium Plan", "monthly");

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private User createTestUser() {
        return new User("testuser@example.com", "password123");
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

    private Subscriptions createTestSubscription() {
        PaymentType paymentType = new PaymentType();
        paymentType.setPaymentType("monthly");
        Subscriptions subscription = new Subscriptions(testPlan, paymentType, testUser);

        return subscription;
    }

    private SubscriptionsView createTestSubscriptionsView() {
        SubscriptionsView view = new SubscriptionsView();
        view.setUsername("testuser@example.com");
        view.setPlanName("Premium Plan");
        view.setPlanDescription("Premium plan with unlimited features");
        view.setPaymentType("monthly");
        view.setStartDate("2025-01-01");
        view.setEndDate("2025-12-31");
        view.setActiveStatus("true");
        return view;
    }

    private PlansDetailsView createTestPlansDetailsView() {
        PlansDetailsView view = new PlansDetailsView();
        view.setName("Premium Plan");
        view.setDescription("Premium plan with unlimited features");
        view.setNumberOfMinutes("unlimited");
        view.setMaximumNumberOfUsers("10");
        view.setMusicCollection("1000");
        view.setMusicSuggestion("personalized");
        view.setMonthlyFee("12.00");
        view.setAnnualFee("120.00");
        view.setActive("true");
        view.setPromoted("true");
        return view;
    }

    @Test
    void testFindAll_ShouldReturnListOfSubscriptionsViews() {

        List<Subscriptions> subscriptionsList = Arrays.asList(testSubscription);
        List<SubscriptionsView> subscriptionsViewList = Arrays.asList(testSubscriptionsView);

        when(subscriptionsService.findAll()).thenReturn(subscriptionsList);
        when(subscriptionsViewMapper.toSubscriptionsView(subscriptionsList)).thenReturn(subscriptionsViewList);

        Iterable<SubscriptionsView> result = subscriptionsController.findAll();

        assertNotNull(result);
        List<SubscriptionsView> resultList = (List<SubscriptionsView>) result;
        assertEquals(1, resultList.size());
        assertEquals("testuser@example.com", resultList.get(0).getUsername());
    }

    @Test
    void testCreate_ValidRequest_ShouldReturnCreatedSubscription() {

        Subscriptions mockSubscription = mock(Subscriptions.class);
        when(mockSubscription.getVersion()).thenReturn(1L);
        when(mockSubscription.getPlan()).thenReturn(testPlan);
        when(mockSubscription.getId()).thenReturn(1L);
        when(mockSubscription.getUser()).thenReturn(testUser);
        
        when(subscriptionsService.create(testCreateRequest)).thenReturn(mockSubscription);
        when(subscriptionsViewMapper.toSubscriptionView(mockSubscription)).thenReturn(testSubscriptionsView);

        ResponseEntity<SubscriptionsView> result = subscriptionsController.create(testCreateRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(testSubscriptionsView, result.getBody());
        assertEquals("\"1\"", result.getHeaders().getETag());
        assertNotNull(result.getHeaders().getLocation());
    }

    @Test
    void testCancelSubscription_ValidIfMatch_ShouldReturnCancelledSubscription() {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("If-Match", "\"1\"");
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        Subscriptions cancelledSubscription = mock(Subscriptions.class);
        when(cancelledSubscription.getVersion()).thenReturn(2L);

        when(subscriptionsService.cancelSubscription(1L)).thenReturn(cancelledSubscription);
        when(subscriptionsViewMapper.toSubscriptionView(cancelledSubscription)).thenReturn(testSubscriptionsView);

        ResponseEntity<SubscriptionsView> result = subscriptionsController.cancelSubscription(webRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testSubscriptionsView, result.getBody());
        assertEquals("\"2\"", result.getHeaders().getETag());
    }

    @Test
    void testCancelSubscription_MissingIfMatch_ShouldThrowBadRequest() {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> subscriptionsController.cancelSubscription(webRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testCancelSubscription_EmptyIfMatch_ShouldThrowBadRequest() {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("If-Match", "");
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> subscriptionsController.cancelSubscription(webRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testPlanDetails_ShouldReturnPlansDetailsView() {

        when(subscriptionsService.planDetails()).thenReturn(testPlansDetails);
        when(plansDetailsViewMapper.toPlansDetailsView(testPlansDetails)).thenReturn(testPlansDetailsView);

        ResponseEntity<PlansDetailsView> result = subscriptionsController.planDetails();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testPlansDetailsView, result.getBody());
    }

    @Test
    void testRenewAnualSubscription_ValidIfMatch_ShouldReturnRenewedSubscription() {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("If-Match", "\"1\"");
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        Subscriptions renewedSubscription = mock(Subscriptions.class);
        when(renewedSubscription.getVersion()).thenReturn(2L);

        when(subscriptionsService.renewAnualSubscription(1L)).thenReturn(renewedSubscription);
        when(subscriptionsViewMapper.toSubscriptionView(renewedSubscription)).thenReturn(testSubscriptionsView);

        ResponseEntity<SubscriptionsView> result = subscriptionsController.renewAnualSubscription(webRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testSubscriptionsView, result.getBody());
        assertEquals("\"2\"", result.getHeaders().getETag());
    }

    @Test
    void testRenewAnualSubscription_MissingIfMatch_ShouldThrowBadRequest() {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> subscriptionsController.renewAnualSubscription(webRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testChangePlan_ValidParameters_ShouldReturnUpdatedSubscription() {

        String newPlanName = "Basic Plan";
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("If-Match", "\"1\"");
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        Subscriptions updatedSubscription = mock(Subscriptions.class);
        when(updatedSubscription.getVersion()).thenReturn(2L);

        when(subscriptionsService.changePlan(1L, newPlanName)).thenReturn(updatedSubscription);
        when(subscriptionsViewMapper.toSubscriptionView(updatedSubscription)).thenReturn(testSubscriptionsView);

        ResponseEntity<SubscriptionsView> result = subscriptionsController.changePlan(webRequest, newPlanName);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testSubscriptionsView, result.getBody());
        assertEquals("\"2\"", result.getHeaders().getETag());
    }

    @Test
    void testChangePlan_MissingIfMatch_ShouldThrowBadRequest() {

        String newPlanName = "Basic Plan";
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> subscriptionsController.changePlan(webRequest, newPlanName));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testMigrateAllToPlan_ValidParameters_ShouldExecuteSuccessfully() {

        String actualPlan = "Premium Plan";
        String newPlan = "Ultimate Plan";
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("If-Match", "\"1\"");
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        subscriptionsController.migrateAllToPlan(webRequest, actualPlan, newPlan);

    }

    @Test
    void testMigrateAllToPlan_MissingIfMatch_ShouldThrowBadRequest() {

        String actualPlan = "Premium Plan";
        String newPlan = "Ultimate Plan";
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> subscriptionsController.migrateAllToPlan(webRequest, actualPlan, newPlan));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testGetVersionFromIfMatchHeader_WithQuotes_ShouldParseCorrectly() {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("If-Match", "\"42\"");
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        when(subscriptionsService.cancelSubscription(42L)).thenReturn(testSubscription);
        when(subscriptionsViewMapper.toSubscriptionView(testSubscription)).thenReturn(testSubscriptionsView);

        ResponseEntity<SubscriptionsView> result = subscriptionsController.cancelSubscription(webRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testGetVersionFromIfMatchHeader_WithoutQuotes_ShouldParseCorrectly() {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("If-Match", "42");
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        when(subscriptionsService.cancelSubscription(42L)).thenReturn(testSubscription);
        when(subscriptionsViewMapper.toSubscriptionView(testSubscription)).thenReturn(testSubscriptionsView);

        ResponseEntity<SubscriptionsView> result = subscriptionsController.cancelSubscription(webRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testCreate_DifferentPaymentTypes_ShouldHandleCorrectly() {

        CreateSubscriptionsRequest annualRequest = new CreateSubscriptionsRequest("Premium Plan", "annually");
        
        when(subscriptionsService.create(annualRequest)).thenReturn(testSubscription);
        when(subscriptionsViewMapper.toSubscriptionView(testSubscription)).thenReturn(testSubscriptionsView);

        ResponseEntity<SubscriptionsView> result = subscriptionsController.create(annualRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(testSubscriptionsView, result.getBody());
    }

    @Test
    void testCreate_ShouldSetCorrectLocationHeader() {

        when(subscriptionsService.create(testCreateRequest)).thenReturn(testSubscription);
        when(subscriptionsViewMapper.toSubscriptionView(testSubscription)).thenReturn(testSubscriptionsView);

        ResponseEntity<SubscriptionsView> result = subscriptionsController.create(testCreateRequest);

        assertNotNull(result);
        URI location = result.getHeaders().getLocation();
        assertNotNull(location);

        assertEquals("/Premium Plan", location.getPath());
    }

    @Test
    void testFindAll_EmptyList_ShouldReturnEmptyIterable() {

        List<Subscriptions> emptyList = Arrays.asList();
        List<SubscriptionsView> emptyViewList = Arrays.asList();

        when(subscriptionsService.findAll()).thenReturn(emptyList);
        when(subscriptionsViewMapper.toSubscriptionsView(emptyList)).thenReturn(emptyViewList);

        Iterable<SubscriptionsView> result = subscriptionsController.findAll();

        assertNotNull(result);
        List<SubscriptionsView> resultList = (List<SubscriptionsView>) result;
        assertEquals(0, resultList.size());
    }

    @Test
    void testChangePlan_SpecialCharactersInPlanName_ShouldHandleCorrectly() {

        String specialPlanName = "Plan@#$%^&*()";
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("If-Match", "\"1\"");
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        when(subscriptionsService.changePlan(1L, specialPlanName)).thenReturn(testSubscription);
        when(subscriptionsViewMapper.toSubscriptionView(testSubscription)).thenReturn(testSubscriptionsView);

        ResponseEntity<SubscriptionsView> result = subscriptionsController.changePlan(webRequest, specialPlanName);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testController_VersionHandling_ShouldHandleDifferentVersionFormats() {

        testVersionFormat("\"123\"", 123L);
        testVersionFormat("123", 123L);
        testVersionFormat("\"0\"", 0L);
        testVersionFormat("0", 0L);
    }

    private void testVersionFormat(String ifMatchValue, Long expectedVersion) {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("If-Match", ifMatchValue);
        ServletWebRequest webRequest = new ServletWebRequest(mockRequest);

        when(subscriptionsService.cancelSubscription(expectedVersion)).thenReturn(testSubscription);
        when(subscriptionsViewMapper.toSubscriptionView(testSubscription)).thenReturn(testSubscriptionsView);

        ResponseEntity<SubscriptionsView> result = subscriptionsController.cancelSubscription(webRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}

    @Mock
    private WebRequest webRequest;

    @Mock
    private Authentication authentication;

    private SubscriptionsController subscriptionsController;
    private User testUser;
    private Plans testPlan;
    private Subscriptions testSubscription;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        subscriptionsController = new SubscriptionsController(subscriptionsService, subscriptionsViewMapper, plansDetailsViewMapper);
        testUser = new User("alex@mail.com", "alexpass");

        
        testPlan = createPlan("Premium");
        testSubscription = new Subscriptions(testPlan, new PaymentType("monthly"), testUser);

        when(authentication.getName()).thenReturn(testUser.getEmail());
    }

    
    private Plans createPlan(String name) {
        com.example.psoft_22_23_project.plansmanagement.model.Name planName = new com.example.psoft_22_23_project.plansmanagement.model.Name();
        planName.setName(name);

        com.example.psoft_22_23_project.plansmanagement.model.Description description = new com.example.psoft_22_23_project.plansmanagement.model.Description();
        description.setDescription("Plan Description");

        com.example.psoft_22_23_project.plansmanagement.model.NumberOfMinutes numberOfMinutes = new com.example.psoft_22_23_project.plansmanagement.model.NumberOfMinutes();
        numberOfMinutes.setNumberOfMinutes("100");

        com.example.psoft_22_23_project.plansmanagement.model.MaximumNumberOfUsers maximumNumberOfUsers = new com.example.psoft_22_23_project.plansmanagement.model.MaximumNumberOfUsers();
        maximumNumberOfUsers.setMaximumNumberOfUsers(5);

        com.example.psoft_22_23_project.plansmanagement.model.MusicCollection musicCollection = new com.example.psoft_22_23_project.plansmanagement.model.MusicCollection();
        musicCollection.setMusicCollection(10);

        com.example.psoft_22_23_project.plansmanagement.model.MusicSuggestion musicSuggestion = new com.example.psoft_22_23_project.plansmanagement.model.MusicSuggestion();
        musicSuggestion.setMusicSuggestion("personalized");

        com.example.psoft_22_23_project.plansmanagement.model.AnnualFee annualFee = new com.example.psoft_22_23_project.plansmanagement.model.AnnualFee();
        annualFee.setAnnualFee(100.00);

        com.example.psoft_22_23_project.plansmanagement.model.MonthlyFee monthlyFee = new com.example.psoft_22_23_project.plansmanagement.model.MonthlyFee();
        monthlyFee.setMonthlyFee(10.00);

        com.example.psoft_22_23_project.plansmanagement.model.Active activeStatus = new com.example.psoft_22_23_project.plansmanagement.model.Active();
        activeStatus.setActive(true);

        com.example.psoft_22_23_project.plansmanagement.model.Promoted promotedStatus = new com.example.psoft_22_23_project.plansmanagement.model.Promoted();
        promotedStatus.setPromoted(false);

        return new Plans(planName, description, numberOfMinutes, maximumNumberOfUsers,
                musicCollection, musicSuggestion, annualFee, monthlyFee, activeStatus, promotedStatus);
    }


    @Test
    public void cancelSubscription_Success() {
        
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.cancelSubscription(eq(1L))).thenReturn(testSubscription);

       
        ResponseEntity<?> response = subscriptionsController.cancelSubscription(webRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(subscriptionsService).cancelSubscription(eq(1L));
    }

    @Test
    public void cancelSubscription_MissingIfMatchHeader() {
      
        when(webRequest.getHeader("If-Match")).thenReturn(null);

    
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () ->
                subscriptionsController.cancelSubscription(webRequest)
        );
    }

    @Test
    public void cancelSubscription_EntityNotFound() {
      
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.cancelSubscription(anyLong())).thenThrow(
                new EntityNotFoundException("No subscriptions found")
        );

        
        assertThrows(EntityNotFoundException.class, () ->
                subscriptionsController.cancelSubscription(webRequest)
        );
    }

    @Test
    public void changePlan_Success() {
  
        String planName = "Premium Plus";
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.changePlan(anyLong(), eq(planName))).thenReturn(testSubscription);

    
        ResponseEntity<?> response = subscriptionsController.changePlan(webRequest, planName);

      
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(subscriptionsService).changePlan(anyLong(), eq(planName));
    }

    @Test
    public void changePlan_MissingIfMatchHeader() {
        
        String planName = "Premium Plus";
        when(webRequest.getHeader("If-Match")).thenReturn(null);

     
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () ->
                subscriptionsController.changePlan(webRequest, planName)
        );
    }

    @Test
    public void changePlan_IllegalArgumentException() {
       
        String planName = "Current Plan";
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.changePlan(anyLong(), eq(planName))).thenThrow(
                new IllegalArgumentException("You are already subscribed to this plan")
        );

        assertThrows(IllegalArgumentException.class, () ->
                subscriptionsController.changePlan(webRequest, planName)
        );
    }

    
    @Test
    public void renewSubscription_Success() {
        
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.renewAnualSubscription(anyLong())).thenReturn(testSubscription);

       
        ResponseEntity<?> response = subscriptionsController.renewAnualSubscription(webRequest);

       
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(subscriptionsService).renewAnualSubscription(anyLong());
    }

    @Test
    public void renewSubscription_MissingIfMatchHeader() {
        
        when(webRequest.getHeader("If-Match")).thenReturn(null);

        
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () ->
                subscriptionsController.renewAnualSubscription(webRequest)
        );
    }

    @Test
    public void renewSubscription_MonthlySubscription() {
       
        String version = "1";
        when(webRequest.getHeader("If-Match")).thenReturn(version);
        when(subscriptionsService.renewAnualSubscription(anyLong())).thenThrow(
                new IllegalArgumentException("You can not renew a monthly subscription")
        );

      
        assertThrows(IllegalArgumentException.class, () ->
                subscriptionsController.renewAnualSubscription(webRequest)
        );
    }




    @Test
    public void createSubscription_UserAlreadyHasActiveSubscription() {
        
        CreateSubscriptionsRequest request = new CreateSubscriptionsRequest();
        request.setName("Premium");
        request.setPaymentType("monthly");

        when(subscriptionsService.create(any(CreateSubscriptionsRequest.class))).thenThrow(
                new IllegalArgumentException("You need to let your active subscription end in order to subscribe")
        );

    
        assertThrows(IllegalArgumentException.class, () ->
                subscriptionsController.create(request)
        );
    }
}

