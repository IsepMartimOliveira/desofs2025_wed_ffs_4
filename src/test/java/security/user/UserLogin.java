package security.user;

import com.example.psoft_22_23_project.api.AuthApi;
import com.example.psoft_22_23_project.api.AuthRequest;
import com.example.psoft_22_23_project.configuration.ClientIPUtil;
import com.example.psoft_22_23_project.configuration.JwtService;
import com.example.psoft_22_23_project.usermanagement.api.UserView;
import com.example.psoft_22_23_project.usermanagement.api.UserViewMapper;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.services.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserLogin {
    @InjectMocks
    private AuthApi authApi;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ClientIPUtil clientIPUtil;

    @Mock
    private UserViewMapper userViewMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void login_success_returnsOkWithTokenAndUserView() {
        String username = "john@mail.com";
        String password = "password";
        String ip = "127.0.0.1";
        String token = "jwt-token";

        AuthRequest request = new AuthRequest(username, password);
        User user = new User(username, password);
        UserView userView = new UserView("1", username, null);

        when(clientIPUtil.getClientIP()).thenReturn(ip);
        when(loginAttemptService.isIpBlocked(ip)).thenReturn(false);
        when(loginAttemptService.isUserBlocked(username)).thenReturn(false);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(eq(user), eq(authentication))).thenReturn(token);
        when(userViewMapper.toUserView(user)).thenReturn(userView);

        ResponseEntity<?> response = authApi.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(token, response.getHeaders().getFirst("Authorization"));
        assertEquals(userView, response.getBody());
    }
    @Test
    void login_ipBlocked_returnsTooManyRequests() {
        String username = "john@mail.com";
        String ip = "127.0.0.1";
        LocalDateTime unlockTime = LocalDateTime.now().plusMinutes(10);

        AuthRequest request = new AuthRequest(username, "pass");

        when(clientIPUtil.getClientIP()).thenReturn(ip);
        when(loginAttemptService.isIpBlocked(ip)).thenReturn(true);
        when(loginAttemptService.getIpUnlockTime(ip)).thenReturn(unlockTime);

        ResponseEntity<?> response = authApi.login(request);

        assertEquals(429, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(((String) body.get("error")).contains("IP address"));
    }

    @Test
    void login_invalidCredentials_returnsUnauthorized() {
        String username = "john@mail.com";
        String ip = "127.0.0.1";

        AuthRequest request = new AuthRequest(username, "wrongpass");

        when(clientIPUtil.getClientIP()).thenReturn(ip);
        when(loginAttemptService.isIpBlocked(ip)).thenReturn(false);
        when(loginAttemptService.isUserBlocked(username)).thenReturn(false);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad creds"));
        when(loginAttemptService.getUserAttemptsLeft(username)).thenReturn(1);

        ResponseEntity<?> response = authApi.login(request);

        assertEquals(401, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Invalid credentials", body.get("error"));
        assertEquals(1, body.get("attemptsLeft"));
    }

}
