package com.example.writein.payload;

import com.example.writein.payload.request.LoginRequest;
import com.example.writein.payload.request.RegisterRequest;
import com.example.writein.payload.response.JwtResponse;
import com.example.writein.payload.response.MessageResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PayloadTest {

    @Test
    void loginRequest_gettersAndSetters_work() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("pass");
        assertEquals("user", request.getUsername());
        assertEquals("pass", request.getPassword());
    }

    @Test
    void registerRequest_gettersAndSetters_work() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user");
        request.setEmail("user@example.com");
        request.setPassword("password");
        request.setRole(Set.of("admin"));

        assertEquals("user", request.getUsername());
        assertEquals("user@example.com", request.getEmail());
        assertEquals("password", request.getPassword());
        assertEquals(1, request.getRole().size());
        assertTrue(request.getRole().contains("admin"));
    }

    @Test
    void jwtResponse_constructor_setsFields() {
        JwtResponse response = new JwtResponse("token123", 1L, "user", "user@example.com", List.of("ROLE_USER"));

        assertEquals("token123", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(1L, response.getId());
        assertEquals("user", response.getUsername());
        assertEquals("user@example.com", response.getEmail());
        assertEquals(1, response.getRoles().size());
        assertEquals("ROLE_USER", response.getRoles().get(0));
    }

    @Test
    void jwtResponse_setters_work() {
        JwtResponse response = new JwtResponse("token", 1L, "user", "email@test.com", List.of());
        response.setAccessToken("newToken");
        response.setTokenType("Custom");
        response.setId(2L);
        response.setUsername("newUser");
        response.setEmail("new@test.com");

        assertEquals("newToken", response.getAccessToken());
        assertEquals("Custom", response.getTokenType());
        assertEquals(2L, response.getId());
        assertEquals("newUser", response.getUsername());
        assertEquals("new@test.com", response.getEmail());
    }

    @Test
    void messageResponse_getterAndSetter_work() {
        MessageResponse response = new MessageResponse("Success");
        assertEquals("Success", response.getMessage());

        response.setMessage("Updated");
        assertEquals("Updated", response.getMessage());
    }
}
