package com.example.psoft_22_23_project.api;

import com.example.psoft_22_23_project.configuration.ClientIPUtil;
import com.example.psoft_22_23_project.configuration.JwtService;
import com.example.psoft_22_23_project.usermanagement.api.UserViewMapper;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.services.LoginAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@Tag(name = "Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/public")
@Slf4j
public class AuthApi {

	private final AuthenticationManager authenticationManager;
	private final UserViewMapper userViewMapper;
	private final LoginAttemptService loginAttemptService;
	private final JwtService jwtService;
	private final ClientIPUtil clientIPUtil;

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Operation(summary = "Login a user and get JWT token")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User authenticated successfully"),
			@ApiResponse(responseCode = "401", description = "Invalid credentials"),
			@ApiResponse(responseCode = "423", description = "Account is locked"),
			@ApiResponse(responseCode = "429", description = "Too many failed login attempts")
	})
	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody @Valid final AuthRequest request) {
		String clientIp = clientIPUtil.getClientIP();
		String username = request.getUsername();

		if (loginAttemptService.isIpBlocked(clientIp)) {
			LocalDateTime unlockTime = loginAttemptService.getIpUnlockTime(clientIp);
			log.warn("Login attempt from blocked IP: {}", clientIp);

			Map<String, Object> response = createErrorResponse(
					"IP address temporarily blocked",
					"Too many failed login attempts from this IP address",
					unlockTime
			);

			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
					.body(response);
		}

		if (loginAttemptService.isUserBlocked(username)) {
			LocalDateTime unlockTime = loginAttemptService.getUserUnlockTime(username);
			log.warn("Login attempt for blocked user: {}", username);

			Map<String, Object> response = createErrorResponse(
					"Account temporarily locked",
					"Too many failed login attempts for this account",
					unlockTime
			);

			return ResponseEntity.status(HttpStatus.LOCKED)
					.body(response);
		}

		try {
			final Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, request.getPassword()));

			loginAttemptService.loginSucceeded(username, clientIp);

			final User user = (User) authentication.getPrincipal();
			log.info("User {} successfully authenticated from IP {}", username, clientIp);

			final String token = jwtService.generateToken(user, authentication);

			return ResponseEntity.ok()
					.header(HttpHeaders.AUTHORIZATION, token)
					.body(userViewMapper.toUserView(user));

		} catch (final BadCredentialsException ex) {
			loginAttemptService.loginFailed(username, clientIp);

			int attemptsLeft = loginAttemptService.getUserAttemptsLeft(username);
			log.warn("Failed login attempt for user: {} from IP: {}, attempts left: {}",
					username, clientIp, attemptsLeft);

			Map<String, Object> response = new HashMap<>();
			response.put("error", "Invalid credentials");

			if (attemptsLeft <= 3 && attemptsLeft > 0) {
				response.put("attemptsLeft", attemptsLeft);
				response.put("message", "Account will be temporarily locked after " +
						attemptsLeft + " more failed " +
						(attemptsLeft == 1 ? "attempt" : "attempts"));
			}

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

		} catch (final LockedException ex) {
			log.warn("Attempt to login to locked account: {} from IP: {}", username, clientIp);
			return ResponseEntity.status(HttpStatus.LOCKED)
					.body(Map.of("error", "Account locked",
							"message", "This account has been locked. Please contact support."));

		} catch (Exception ex) {
			log.error("Unexpected error during authentication for user: {}", username, ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Authentication error",
							"message", "An unexpected error occurred during authentication"));
		}
	}

	/**
	 * Helper method to create standardized error responses with time remaining information
	 */
	private Map<String, Object> createErrorResponse(String error, String message, LocalDateTime unlockTime) {
		Map<String, Object> response = new HashMap<>();
		response.put("error", error);
		response.put("message", message);

		if (unlockTime != null) {
			Duration timeRemaining = Duration.between(LocalDateTime.now(), unlockTime);

			if (!timeRemaining.isNegative()) {
				long minutes = timeRemaining.toMinutes();
				long seconds = timeRemaining.minusMinutes(minutes).getSeconds();

				response.put("unlockTime", unlockTime.format(TIME_FORMATTER));
				response.put("timeRemaining", String.format("%d minutes, %d seconds", minutes, seconds));
			}
		}

		return response;
	}
}