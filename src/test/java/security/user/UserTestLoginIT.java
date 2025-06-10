package security.user;

import com.example.psoft_22_23_project.api.AuthRequest;
import com.example.psoft_22_23_project.configuration.ClientIPUtil;
import com.example.psoft_22_23_project.configuration.JwtService;
import com.example.psoft_22_23_project.usermanagement.api.UserView;
import com.example.psoft_22_23_project.usermanagement.api.UserViewMapper;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.services.LoginAttemptService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(classes = com.example.psoft_22_23_project.Main.class)

@AutoConfigureMockMvc
public class UserTestLoginIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserViewMapper userViewMapper;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ClientIPUtil clientIPUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_withValidCredentials_returnsTokenAndUserView() throws Exception {
        AuthRequest authRequest = new AuthRequest("john@mail.com", "password");
        String ip = "127.0.0.1";
        User user = new User("john@mail.com", "password");
        String jwtToken = "mock-jwt-token";

        when(clientIPUtil.getClientIP()).thenReturn(ip);
        when(loginAttemptService.isIpBlocked(ip)).thenReturn(false);
        when(loginAttemptService.isUserBlocked("john@mail.com")).thenReturn(false);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        UserView userView = new UserView("1", "john@mail.com", null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(eq(user), any())).thenReturn(jwtToken);
        when(userViewMapper.toUserView(user)).thenReturn(userView);

        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .secure(true)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", jwtToken))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("john@mail.com"))
                .andExpect(jsonPath("$.fileName").doesNotExist());
    }

    @Test
    void login_withInvalidCredentials_returnsUnauthorized() throws Exception {
        AuthRequest authRequest = new AuthRequest("john@mail.com", "wrongpassword");
        String ip = "127.0.0.1";

        when(clientIPUtil.getClientIP()).thenReturn(ip);
        when(loginAttemptService.isIpBlocked(ip)).thenReturn(false);
        when(loginAttemptService.isUserBlocked("john@mail.com")).thenReturn(false);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(loginAttemptService.getUserAttemptsLeft("john@mail.com")).thenReturn(2);

        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .secure(true)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"))
                .andExpect(jsonPath("$.attemptsLeft").value(2))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void login_whenUserBlocked_returnsLocked() throws Exception {
        AuthRequest authRequest = new AuthRequest("john@mail.com", "password");
        String ip = "127.0.0.1";
        LocalDateTime unlockTime = LocalDateTime.now().plusMinutes(15);

        when(clientIPUtil.getClientIP()).thenReturn(ip);
        when(loginAttemptService.isIpBlocked(ip)).thenReturn(false);
        when(loginAttemptService.isUserBlocked("john@mail.com")).thenReturn(true);
        when(loginAttemptService.getUserUnlockTime("john@mail.com")).thenReturn(unlockTime);

        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .secure(true)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.error").value("Account temporarily locked"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.unlockTime").exists())
                .andExpect(jsonPath("$.timeRemaining").exists());
    }

    @Test
    void login_whenIpBlocked_returnsTooManyRequests() throws Exception {
        AuthRequest authRequest = new AuthRequest("john@mail.com", "password");
        String ip = "127.0.0.1";
        LocalDateTime unlockTime = LocalDateTime.now().plusMinutes(10);

        when(clientIPUtil.getClientIP()).thenReturn(ip);
        when(loginAttemptService.isIpBlocked(ip)).thenReturn(true);
        when(loginAttemptService.getIpUnlockTime(ip)).thenReturn(unlockTime);

        mockMvc.perform(post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .secure(true)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").value("IP address temporarily blocked"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.unlockTime").exists())
                .andExpect(jsonPath("$.timeRemaining").exists());
    }



}
