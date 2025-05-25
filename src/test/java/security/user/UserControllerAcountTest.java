package security.user;

import com.example.psoft_22_23_project.api.AuthApi;
import com.example.psoft_22_23_project.usermanagement.api.CreateUserRequest;
import com.example.psoft_22_23_project.usermanagement.api.UserController;
import com.example.psoft_22_23_project.usermanagement.api.UserView;
import com.example.psoft_22_23_project.usermanagement.api.UserViewMapper;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class UserControllerAcountTest {
    private static final String username="john@mail.com";
    private static final String password="StrongPass123!";
    @Mock
    private UserService userService;

    @Mock
    private UserViewMapper userViewMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    private CreateUserRequest createRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john@mail.com");
        request.setPassword("StrongPass123!");
        request.setEmail("john@mail.com");
        request.setPhoneNumber(123456789);
        request.setAge(25);
        return request;
    }

    @Test
    void createUser_success_returnsCreatedUserView() {

        CreateUserRequest request = createRequest();
        User user = new User(username,password);

        UserView userView = new UserView("1", request.getUsername(), null);

        when(userService.createUser(request)).thenReturn(user);
        when(userViewMapper.toUserView(user)).thenReturn(userView);

        ResponseEntity<UserView> response = userController.createUser(request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(userView, response.getBody());
    }

    @Test
    void createUser_whenUsernameExists_throwsException() {
        CreateUserRequest request = createRequest();

        when(userService.createUser(request))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userController.createUser(request)
        );

        assertEquals("Username already exists", exception.getMessage());
    }
}
