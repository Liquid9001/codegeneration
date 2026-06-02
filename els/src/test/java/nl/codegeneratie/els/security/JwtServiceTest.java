package nl.codegeneratie.els.security;

import io.jsonwebtoken.Claims;
import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.domain.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void generateTokenUsesConfiguredClaimsAndExpiration() {
        JwtService jwtService = new JwtService(
                "testOnlyJwtSecretKeyForElsMustBeAtLeast32Bytes",
                3600000
        );
        User user = new User();
        user.setId(7L);
        user.setEmail("jwt.customer@example.com");
        user.setRole(UserRole.CUSTOMER);
        user.setApproved(true);

        String token = jwtService.generateToken(user);
        Claims claims = jwtService.extractClaims(token);

        assertEquals("jwt.customer@example.com", claims.getSubject());
        assertEquals("jwt.customer@example.com", claims.get("email", String.class));
        assertEquals(7L, claims.get("userId", Long.class));
        assertEquals("CUSTOMER", claims.get("role", String.class));
        assertEquals(true, claims.get("approved", Boolean.class));
        assertTrue(claims.getExpiration().after(new Date()));
    }
}
