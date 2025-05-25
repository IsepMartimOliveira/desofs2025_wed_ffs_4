package security.user;

import com.example.psoft_22_23_project.usermanagement.api.CreateUserRequest;
import com.example.psoft_22_23_project.usermanagement.api.UserView;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.services.UserService;
import com.example.psoft_22_23_project.usermanagement.api.UserViewMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.example.psoft_22_23_project.Main.class)
@AutoConfigureMockMvc
public class UserAccountTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserViewMapper userViewMapper;

    @Test
    void createUser_whenValidRequest_thenReturnsCreatedAndUserView() throws Exception {
        // Prepare request DTO
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john@mail.com");
        request.setPassword("StrongPass123!");
        request.setEmail("john@mail.com");
        request.setPhoneNumber(123456789);
        request.setAge(30);
        request.setCity("New York");
        request.setCountry("USA");

        // Mock domain user returned by service
        User user = new User(request.getUsername(), request.getPassword());

        // Mock UserView returned by mapper
        UserView userView = new UserView("1", request.getUsername(), null);

        // Mock behavior of service and mapper
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(user);
        when(userViewMapper.toUserView(user)).thenReturn(userView);

        // Perform POST request and assert
        mockMvc.perform(post("/api/user/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value(request.getUsername()))
                .andExpect(jsonPath("$.fileName").doesNotExist());
    }

    @Test
    void createUser_whenInvalidEmail_thenReturnsBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("invalid-email");
        request.setPassword("StrongPass123!");
        request.setEmail("invalid-email");
        request.setPhoneNumber(123456789);
        request.setAge(30);

        mockMvc.perform(post("/api/user/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenPasswordTooShort_thenReturnsBadRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john@mail.com");
        request.setPassword("short");
        request.setEmail("john@mail.com");
        request.setPhoneNumber(123456789);
        request.setAge(30);

        mockMvc.perform(post("/api/user/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
