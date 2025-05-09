package com.example.psoft_22_23_project.configuration;

import com.example.psoft_22_23_project.usermanagement.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import org.springframework.security.core.GrantedAuthority;

import jakarta.validation.constraints.NotNull;


@Service
@Component
public class JwtService {
    private final JwtEncoder jwtEncoder;

    @Value("${token.timeout}")
    private int tokenTimeout;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(@NotNull User user,@NotNull Authentication authentication) {
        final Instant now = Instant.now();

        final String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(" "));

        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("example.io")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(tokenTimeout))
                .subject(format("%s,%s", user.getId(), user.getUsername()))
                .claim("roles", scope)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
