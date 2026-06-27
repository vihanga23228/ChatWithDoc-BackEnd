package com.local.chatwithdocbackend.service;

import com.local.chatwithdocbackend.entity.Role;
import com.local.chatwithdocbackend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "3cfa76f114ea766a5e119420dc2e0e0bb397fa6c8888b598b9f1d0f5bb8ef744");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        User user = new User("1", "John Doe", "john@example.com", "password", Role.PATIENT);

        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String username = jwtService.extractUsername(token);
        assertEquals("john@example.com", username);

        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("PATIENT", role);

        assertTrue(jwtService.isTokenValid(token, "john@example.com"));
        assertFalse(jwtService.isTokenValid(token, "other@example.com"));
    }
}
