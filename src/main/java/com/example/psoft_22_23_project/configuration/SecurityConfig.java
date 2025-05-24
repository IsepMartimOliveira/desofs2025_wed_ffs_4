/*
 * Copyright (c) 2022-2022 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.example.psoft_22_23_project.configuration;

import com.example.psoft_22_23_project.usermanagement.model.Role;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static java.lang.String.format;

/**
 * Check https://www.baeldung.com/security-spring and
 * https://www.toptal.com/spring/spring-security-tutorial
 * <p>
 * Based on https://github.com/Yoh0xFF/java-spring-security-example/
 * Updated for Spring Boot 3.x
 *
 * @author pagsousa
 *
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserRepository userRepo;
	private final AuthenticationConfiguration authenticationConfiguration;

	@Value("${jwt.public.key}")
	private RSAPublicKey rsaPublicKey;

	@Value("${jwt.private.key}")
	private RSAPrivateKey rsaPrivateKey;

	@Value("${springdoc.api-docs.path}")
	private String restApiDocPath;

	@Value("${springdoc.swagger-ui.path}")
	private String swaggerPath;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// Enable CORS and disable CSRF



		//TEST DEPLOYMENT
		http = http.cors(cors -> cors.configurationSource(request -> {
					CorsConfiguration configuration = new CorsConfiguration();
					configuration.setAllowCredentials(true);
					configuration.addAllowedOrigin("*");
					configuration.addAllowedHeader("*");
					configuration.addAllowedMethod("*");
					return configuration;
				}))
				.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")); // Exempt API endpoints from CSRF protection



		// Set session management to stateless
		http = http.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Set unauthorized requests exception handler
		http = http.exceptionHandling(exceptions ->
				exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
						.accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

		// Set permissions on endpoints
		http.authorizeHttpRequests(auth -> auth
				// Swagger endpoints must be publicly accessible
				.requestMatchers("/").permitAll()
				.requestMatchers(format("%s/**", restApiDocPath)).permitAll()
				.requestMatchers(format("%s/**", swaggerPath)).permitAll()
				.requestMatchers("/api-docs/**").permitAll()
				.requestMatchers("/swagger-ui/**").permitAll()
				.requestMatchers("/h2/**").permitAll()

				// All public endpoints
				.requestMatchers("/api/public/**").permitAll()

				// Plans management
				.requestMatchers(HttpMethod.GET, "/api/plans").permitAll()

				// Get a device image management
				.requestMatchers(HttpMethod.GET, "/api/device/photo/**").permitAll()

				.requestMatchers(HttpMethod.GET, "/api/subscriptions/list").hasRole(Role.User_Admin)
				.requestMatchers(HttpMethod.POST, "/api/subscriptions/create/").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/subscriptions/").hasRole(Role.Subscriber)
				.requestMatchers(HttpMethod.PATCH, "/api/subscriptions/").hasRole(Role.Subscriber)
				.requestMatchers(HttpMethod.PATCH, "/api/subscriptions/renew").hasRole(Role.Subscriber)
				.requestMatchers(HttpMethod.PATCH, "/api/subscriptions/change/{name}").hasRole(Role.Subscriber)
				.requestMatchers(HttpMethod.PATCH, "/api/subscriptions/change/{actualPlan}/{newPlan}").hasRole(Role.Marketing_Director)
				.requestMatchers("/api/public/subscriptions/**").permitAll()

				// Private endpoints

				// Device management
				.requestMatchers("/api/device/**").hasRole(Role.Subscriber)

				// Plans management
				.requestMatchers(HttpMethod.POST, "/api/plans").hasRole(Role.Marketing_Director)
				.requestMatchers(HttpMethod.PATCH, "/api/plans/update/**").hasRole(Role.Marketing_Director)
				.requestMatchers(HttpMethod.PATCH, "/api/plans/updateMoney/**").hasRole(Role.Marketing_Director)
				.requestMatchers(HttpMethod.PATCH, "/api/plans/deactivate/**").hasRole(Role.Marketing_Director)
				.requestMatchers(HttpMethod.PATCH, "/api/plans/promote/**").hasRole(Role.Marketing_Director)
				.requestMatchers(HttpMethod.GET, "/api/plans/history/**").hasRole(Role.Marketing_Director)
				.requestMatchers(HttpMethod.DELETE, "/api/plans/**").hasRole(Role.Marketing_Director)

				// Dashboard Endpoints management
				.requestMatchers(HttpMethod.GET, "/api/dashboard/**").hasRole(Role.Project_Manager)
				.requestMatchers(HttpMethod.GET, "/api/dashboard/revenuePlan").hasRole(Role.Financial_director)
				.requestMatchers(HttpMethod.GET, "/api/dashboard/currentRevenue").hasRole(Role.Financial_director)

				// .requestMatchers("/api/admin/user/**").hasRole(Role.User_Admin) // user management no
				.requestMatchers("/api/user/photo/**").hasRole(Role.Subscriber) // photo for user upload and see it
				.requestMatchers(HttpMethod.POST, "/api/user/account").permitAll() // user account management
				.anyRequest().authenticated()
		);

		// Configure OAuth2 resource server with JWT
		http.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
		);

		// Configure frame options for H2
		http.headers(headers -> headers
				.frameOptions(frameOptions -> frameOptions.sameOrigin())
		);


		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(username -> userRepo.findByUsername(username)
						.orElseThrow(() -> new UsernameNotFoundException(format("User: %s, not found", username))))
				.passwordEncoder(passwordEncoder())
				.and()
				.build();
	}

	// Used by JwtAuthenticationProvider to generate JWT tokens
	@Bean
	public JwtEncoder jwtEncoder() {
		final JWK jwk = new RSAKey.Builder(this.rsaPublicKey).privateKey(this.rsaPrivateKey).build();
		final JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	// Used by JwtAuthenticationProvider to decode and validate JWT tokens
	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build();
	}

	// Extract authorities from the roles claim
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

		final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	// Set password encoding schema
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Used by spring security if CORS is enabled.
	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	// Expose authentication manager bean
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}