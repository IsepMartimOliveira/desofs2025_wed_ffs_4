package com.example.psoft_22_23_project.usermanagement.services;

import com.example.psoft_22_23_project.filestoragemanagement.service.FileStorageService;
import com.example.psoft_22_23_project.usermanagement.api.PersonalDataDeletionRequest;
import com.example.psoft_22_23_project.usermanagement.api.PersonalDataExportDTO;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.model.UserImage;
import com.example.psoft_22_23_project.usermanagement.repositories.UserImageRepository;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;
import com.example.psoft_22_23_project.subscriptionsmanagement.model.ActiveStatus; // Corrected import
import com.example.psoft_22_23_project.subscriptionsmanagement.model.Subscriptions;
import com.example.psoft_22_23_project.subscriptionsmanagement.repositories.SubscriptionsRepository;
import com.example.psoft_22_23_project.subscriptionsmanagement.services.SubscriptionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserImageRepository userImageRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private SubscriptionsRepository subscriptionsRepository;
    @Mock
    private SubscriptionsService subscriptionsService;

    @InjectMocks
    private UserService userService;

    private User testUser; // This will be a spy object
    private UserImage testUserImage;

    @BeforeEach
    void setUp() {
        // Create a real User object and then spy on it to mock getId()
        User actualUser = new User("testUser", "password123", "test@example.com", 123456789, 30);
        testUser = Mockito.spy(actualUser);
        Mockito.when(testUser.getId()).thenReturn(1L); // Mock getId() to return 1L

        // UserImage ID is set by JPA, not manually. No setId needed for testUserImage.
        testUserImage = new UserImage("prefix", "testImage.jpg", "http://localhost/image.jpg", "image/jpeg", 1024L);
        // If testUserImage.getId() is needed, it should be mocked if UserImage is a mock/spy.
        // For a real UserImage object, its ID would be null until persisted.

        // Mock SecurityContextHolder
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        // Use the mocked testUser.getId() for consistency
        String authenticatedUserId = String.valueOf(testUser.getId()); // Resolve ID first
        Mockito.when(authentication.getName()).thenReturn(authenticatedUserId);
        SecurityContextHolder.setContext(securityContext);

        // Ensure userRepository.findById uses the consistent ID and returns the spied testUser
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    }

    //region exportPersonalData Tests

    @Test
    void exportPersonalData_shouldReturnCorrectDTO_whenUserExists() {
        // Arrange
        testUser.setLocation("Test Location"); // Can call setters on the spied object

        // Act
        PersonalDataExportDTO result = userService.exportPersonalData();

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUserData().getUsername());
        assertEquals(testUser.getEmail(), result.getUserData().getEmail());
        assertEquals(testUser.getPhoneNumber(), result.getUserData().getPhoneNumber());
        assertEquals(testUser.getAge(), result.getUserData().getAge());
        assertEquals(testUser.getLocation(), result.getUserData().getLocation());
        assertNotNull(result.getExportMetadata());
        assertEquals("Personal data retention as per privacy policy.", result.getExportMetadata().getDataRetentionPolicy());
        assertEquals("Subscription and device data preserved for business auditing.", result.getExportMetadata().getAuditNote());

        // Verify interactions (White-box)
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void exportPersonalData_shouldIncludeImageDTO_whenUserHasImage() {
        // Arrange
        testUser.setUserImage(testUserImage);

        // Act
        PersonalDataExportDTO result = userService.exportPersonalData();

        // Assert
        assertNotNull(result.getUserData().getProfileImage());
        assertEquals(testUserImage.getFileName(), result.getUserData().getProfileImage().getFileName());
        assertEquals(testUserImage.getFileDownloadUri(), result.getUserData().getProfileImage().getDownloadUrl());
        assertEquals(testUserImage.getContentType(), result.getUserData().getProfileImage().getContentType());
        assertEquals(testUserImage.getFileSize(), result.getUserData().getProfileImage().getFileSize());

        // Verify interactions (White-box)
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void exportPersonalData_shouldNotIncludeImageDTO_whenUserHasNoImage() {
        // Arrange
        testUser.setUserImage(null);

        // Act
        PersonalDataExportDTO result = userService.exportPersonalData();

        // Assert
        assertNull(result.getUserData().getProfileImage());

        // Verify interactions (White-box)
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void exportPersonalData_shouldThrowEntityNotFoundException_whenUserNotFound() {
        // Arrange
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.exportPersonalData());

        // Verify interactions (White-box)
        verify(userRepository, times(1)).findById(1L);
    }

    //endregion

    //region deletePersonalData Tests

    private Subscriptions mockSubscription(Long id, Long version, boolean isActive) {
        Subscriptions subscription = Mockito.mock(Subscriptions.class);
        ActiveStatus activeStatusMock = Mockito.mock(ActiveStatus.class);
        Mockito.when(activeStatusMock.isActive()).thenReturn(isActive);

        Mockito.lenient().when(subscription.getId()).thenReturn(id);
        Mockito.lenient().when(subscription.getVersion()).thenReturn(version);
        Mockito.when(subscription.getActiveStatus()).thenReturn(activeStatusMock);
        return subscription;
    }

    @Test
    void deletePersonalData_shouldAnonymizeAndSaveUser_whenPasswordMatches() {
        // Arrange
        PersonalDataDeletionRequest request = new PersonalDataDeletionRequest();
        request.setConfirmationPassword("password123");
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser); // testUser is already a spy
        when(subscriptionsRepository.findByUser(testUser)).thenReturn(Optional.empty());

        // Act
        userService.deletePersonalData(request);

        // Assert (Black-box: check user state on the spied testUser)
        // Email uses the mocked ID
        assertEquals("deleted_" + testUser.getId() + "@anonymous.local", testUser.getEmail());
        assertEquals(0, testUser.getPhoneNumber());
        assertEquals(0, testUser.getAge());
        assertNull(testUser.getLocation());
        assertTrue(testUser.isPersonalDataDeleted());
        assertNotNull(testUser.getPersonalDataDeletionDate());
        assertFalse(testUser.isEnabled());

        // Verify interactions (White-box)
        verify(passwordEncoder, times(1)).matches("password123", testUser.getPassword());
        verify(userRepository, times(1)).save(testUser); // Verifies save on the spied object
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(userImageRepository, never()).delete(any(UserImage.class));
        verify(subscriptionsRepository, times(1)).findByUser(testUser);
        verify(subscriptionsService, never()).cancelSubscription(anyLong());
    }

    @Test
    void deletePersonalData_shouldDeleteImageAndCancelSubscription_whenTheyExistAndAreActive() throws Exception {
        // Arrange
        PersonalDataDeletionRequest request = new PersonalDataDeletionRequest();
        request.setConfirmationPassword("password123");
        testUser.setUserImage(testUserImage); // Set on the spied user

        Subscriptions activeSubscription = mockSubscription(1L, 1L, true);

        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(subscriptionsRepository.findByUser(testUser)).thenReturn(Optional.of(activeSubscription));
        when(fileStorageService.deleteFile(testUserImage.getFileName())).thenReturn(true); // Assuming deleteFile returns boolean
        doNothing().when(userImageRepository).delete(testUserImage);
        when(subscriptionsService.cancelSubscription(activeSubscription.getVersion())).thenReturn(activeSubscription);

        // Act
        userService.deletePersonalData(request);

        // Assert (Black-box: check user state)
        assertNull(testUser.getUserImage());

        // Verify interactions (White-box)
        verify(passwordEncoder, times(1)).matches("password123", testUser.getPassword());
        verify(fileStorageService, times(1)).deleteFile(testUserImage.getFileName());
        verify(userImageRepository, times(1)).delete(testUserImage);
        verify(subscriptionsRepository, times(1)).findByUser(testUser);
        verify(subscriptionsService, times(1)).cancelSubscription(activeSubscription.getVersion());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void deletePersonalData_shouldThrowAccessDeniedException_whenPasswordDoesNotMatch() {
        // Arrange
        PersonalDataDeletionRequest request = new PersonalDataDeletionRequest();
        request.setConfirmationPassword("wrongPassword");
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userService.deletePersonalData(request));

        // Verify interactions (White-box)
        verify(passwordEncoder, times(1)).matches("wrongPassword", testUser.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deletePersonalData_shouldHandleFileDeletionErrorGracefully() throws Exception {
        // Arrange
        PersonalDataDeletionRequest request = new PersonalDataDeletionRequest();
        request.setConfirmationPassword("password123");
        testUser.setUserImage(testUserImage);

        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        doThrow(new RuntimeException("File deletion failed")).when(fileStorageService).deleteFile(testUserImage.getFileName());
        when(subscriptionsRepository.findByUser(testUser)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userService.deletePersonalData(request));
        assertEquals("User image deletion failed for user 1, aborting user data deletion.", thrown.getMessage());

        // Assert that user data was NOT anonymized or saved
        assertFalse(testUser.isPersonalDataDeleted());
        assertNull(testUser.getPersonalDataDeletionDate());
        assertTrue(testUser.isEnabled());

        // Verify
        verify(fileStorageService, times(1)).deleteFile(testUserImage.getFileName());
        verify(userImageRepository, never()).delete(testUserImage);
        verify(userRepository, never()).save(testUser);
    }

    @Test
    void deletePersonalData_shouldNotCancelSubscription_whenSubscriptionIsNotActive() throws Exception {
        // Arrange
        PersonalDataDeletionRequest request = new PersonalDataDeletionRequest();
        request.setConfirmationPassword("password123");

        Subscriptions inactiveSubscription = mockSubscription(1L, 1L, false);

        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(subscriptionsRepository.findByUser(testUser)).thenReturn(Optional.of(inactiveSubscription));

        // Act
        userService.deletePersonalData(request);

        // Assert
        assertTrue(testUser.isPersonalDataDeleted());

        // Verify
        verify(subscriptionsService, never()).cancelSubscription(anyLong());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void deletePersonalData_shouldHandleSubscriptionCancellationErrorGracefully() throws Exception {
        // Arrange
        PersonalDataDeletionRequest request = new PersonalDataDeletionRequest();
        request.setConfirmationPassword("password123");

        Subscriptions activeSubscription = mockSubscription(1L, 1L, true);

        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(subscriptionsRepository.findByUser(testUser)).thenReturn(Optional.of(activeSubscription));
        Long subscriptionVersion = activeSubscription.getVersion(); // Ensure version is captured before mocking
        doThrow(new RuntimeException("Subscription cancellation failed")).when(subscriptionsService).cancelSubscription(subscriptionVersion);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userService.deletePersonalData(request));
        assertEquals("Subscription cancellation failed for user " + testUser.getId() + ", aborting user data deletion.", thrown.getMessage());


        // Assert that user data was NOT anonymized or saved
        assertFalse(testUser.isPersonalDataDeleted());
        assertNull(testUser.getPersonalDataDeletionDate());
        assertTrue(testUser.isEnabled());

        // Verify
        verify(subscriptionsService, times(1)).cancelSubscription(activeSubscription.getVersion());
        verify(userRepository, never()).save(any(User.class));
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(userImageRepository, never()).delete(any(UserImage.class));
    }

    @Test
    void deletePersonalData_shouldThrowEntityNotFoundException_whenUserNotFoundDuringDeletion() {
        // Arrange
        PersonalDataDeletionRequest request = new PersonalDataDeletionRequest();
        request.setConfirmationPassword("password123");
        // Override the setUp mock for this specific test case
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deletePersonalData(request));

        // Verify
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    //endregion
}
