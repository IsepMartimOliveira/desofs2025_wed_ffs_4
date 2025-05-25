package security.user;

import com.example.psoft_22_23_project.usermanagement.api.CreateUserRequest;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;
import com.example.psoft_22_23_project.usermanagement.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceAccountTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CreateUserRequest baseRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john@mail.com");
        request.setPassword("SecurePass123!");
        request.setEmail("john@mail.com");
        request.setPhoneNumber(123456789);
        request.setAge(30);
        request.setCity("Lisbon");
        request.setCountry("Portugal");
        return request;
    }

    @Test
    void createUser_withValidData_savesUserAndReturnsIt() {
        String username = "john@mail.com";
        String password = "password";
        CreateUserRequest request = baseRequest();
        when(userRepository.findByUsername("john@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("hashedPassword");

        User savedUser = new User(username,password);

        when(userRepository.save(any())).thenReturn(savedUser);

        User result = userService.createUser(request);

        assertEquals("john@mail.com", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_withExistingUsername_throwsException() {
        String username = "john@mail.com";
        String password = "password";
        CreateUserRequest request = baseRequest();
        User savedUser = new User(username,password);

        when(userRepository.findByUsername("john@mail.com"))
                .thenReturn(Optional.of(savedUser));

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_setsLocationFromCityAndCountry() {
        String username = "john@mail.com";
        String password = "password";
        CreateUserRequest request = baseRequest();
        request.setLocation(null);
        User savedUser = new User(username,password);

        when(userRepository.findByUsername("john@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(captor.capture())).thenReturn(savedUser);

        userService.createUser(request);
        User user = captor.getValue();

        assertEquals("Lisbon, Portugal", user.getLocation());
    }

    @Test
    void createUser_setsLocationDirectlyIfProvided() {
        String username = "john@mail.com";
        String password = "password";
        CreateUserRequest request = baseRequest();
        request.setLocation("Paris, France");
        User savedUser = new User(username,password);

        when(userRepository.findByUsername("john@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(captor.capture())).thenReturn(savedUser);

        userService.createUser(request);
        User user = captor.getValue();

        assertEquals("Paris, France", user.getLocation());
    }
}
