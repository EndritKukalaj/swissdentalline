package ch.zhaw.swissdentalline.security;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
public class TestSecurityConfig {
    public static final String PATIENT = "Bearer patient";
    public static final String ZAHNARZT = "Bearer zahnarzt";
    public static final String INVALID = "Bearer invalid";

    @Bean
    public JwtDecoder jwtDecoder() {
        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) {
                var bearer = "Bearer " + token;
                if (bearer.equals(PATIENT)) {
                    return createJwtWithRole("patient", List.of("Patient"));
                } else if (bearer.equals(ZAHNARZT)) {
                    return createJwtWithRole("zahnarzt", List.of("Zahnarzt"));
                } else if (bearer.equals(INVALID)) {
                    throw new AuthenticationException("Invalid JWT") {};
                }
                throw new AuthenticationException("Unknown token") {};
            }
        };
    }

    private Jwt createJwtWithRole(String subject, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", subject);
        claims.put("user_roles", roles);
        return new Jwt(
                "valid-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims);
    }
}
