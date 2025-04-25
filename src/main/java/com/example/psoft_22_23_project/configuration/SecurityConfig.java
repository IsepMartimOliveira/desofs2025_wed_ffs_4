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
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
 *
 * @author pagsousa
 *
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserRepository userRepo;

	@Value("${jwt.public.key}")
	private RSAPublicKey rsaPublicKey;

	@Value("${jwt.private.key}")
	private RSAPrivateKey rsaPrivateKey;

	@Value("${springdoc.api-docs.path}")
	private String restApiDocPath;

	@Value("${springdoc.swagger-ui.path}")
	private String swaggerPath;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(username -> userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(format("User: %s, not found", username))));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Enable CORS and disable CSRF
		http = http.cors().and().csrf().disable();

		// Set session management to stateless
		http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();

		// Set unauthorized requests exception handler
		http = http.exceptionHandling(
				exceptions -> exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
						.accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

		// Set permissions on endpoints
		http.authorizeRequests()
				// Swagger endpoints must be publicly accessible
				.antMatchers("/").permitAll().antMatchers(format("%s/**", restApiDocPath)).permitAll()
				.antMatchers(format("%s/**", swaggerPath)).permitAll()
				.antMatchers("/api-docs/**").permitAll()
				.antMatchers("/swagger-ui/**").permitAll()
				.antMatchers("/h2/**").permitAll();


				// Our public endpoints
				http.authorizeRequests()

						//all public endpoints
						.antMatchers("/api/public/**").permitAll()

						// Plans management
						.antMatchers(HttpMethod.GET,"/api/plans").permitAll()

						// get a device image management
						.antMatchers(HttpMethod.GET, "/api/device/photo/**").permitAll()

						.antMatchers(HttpMethod.GET,"/api/subscriptions/list").hasRole(Role.User_Admin)
						.antMatchers(HttpMethod.POST,"/api/subscriptions/create/").permitAll()
						.antMatchers(HttpMethod.GET,"/api/subscriptions/").hasRole(Role.Subscriber)
						.antMatchers(HttpMethod.PATCH,"/api/subscriptions/").hasRole(Role.Subscriber)
						.antMatchers(HttpMethod.PATCH,"/api/subscriptions/renew").hasRole(Role.Subscriber)
						.antMatchers(HttpMethod.PATCH,"/api/subscriptions/change/{name}").hasRole(Role.Subscriber)
						.antMatchers(HttpMethod.PATCH,"/api/subscriptions/change/{actualPlan}/{newPlan}").hasRole(Role.Marketing_Director)
						.antMatchers("/api/public/subscriptions/**").permitAll()

						//private endpoints

						// device management
						.antMatchers("/api/device/**").hasRole(Role.Subscriber)

						//plans management
						.antMatchers(HttpMethod.POST,"/api/plans").hasRole(Role.Marketing_Director)
						.antMatchers(HttpMethod.PATCH,"/api/plans/update/**").hasRole(Role.Marketing_Director)
						.antMatchers(HttpMethod.PATCH,"/api/plans/updateMoney/**").hasRole(Role.Marketing_Director)
						.antMatchers(HttpMethod.PATCH,"/api/plans/deactivate/**").hasRole(Role.Marketing_Director)
						.antMatchers(HttpMethod.PATCH,"/api/plans/promote/**").hasRole(Role.Marketing_Director)
						.antMatchers(HttpMethod.GET,"/api/plans/history/**").hasRole(Role.Marketing_Director)
						.antMatchers(HttpMethod.DELETE,"/api/plans/**").hasRole(Role.Marketing_Director)

						//Dashboard Endpoints management
						.antMatchers(HttpMethod.GET,"/api/dashboard/**").hasRole(Role.Project_Manager)
						.antMatchers(HttpMethod.GET,"/api/dashboard/revenuePlan").hasRole(Role.Financial_director)
						.antMatchers(HttpMethod.GET,"/api/dashboard/currentRevenue").hasRole(Role.Financial_director)

						//.antMatchers("/api/admin/user/**").hasRole(Role.User_Admin) // user management no
						.antMatchers("/api/user/photo/**").hasRole(Role.Subscriber)// photo for user upload and see it
						.antMatchers(HttpMethod.POST,"/api/user/account").permitAll()// user account management
						.anyRequest().authenticated()

						.and().httpBasic(Customizer.withDefaults()).oauth2ResourceServer().jwt();
				http.headers().frameOptions().sameOrigin().and().authorizeRequests();

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
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
