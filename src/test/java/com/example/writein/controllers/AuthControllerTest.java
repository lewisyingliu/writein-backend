package com.example.writein.controllers;

import com.example.writein.models.ERole;
import com.example.writein.models.entities.Role;
import com.example.writein.models.entities.User;
import com.example.writein.payload.request.LoginRequest;
import com.example.writein.payload.request.RegisterRequest;
import com.example.writein.payload.response.JwtResponse;
import com.example.writein.payload.response.MessageResponse;
import com.example.writein.repository.RoleRepository;
import com.example.writein.repository.UserRepository;
import com.example.writein.security.jwt.JwtUtils;
import com.example.writein.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @Test
    void authenticateUser_withValidCredentials_returnsJwtResponse() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password",
                Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

        ResponseEntity<JwtResponse> response = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getAccessToken());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals(1, response.getBody().getRoles().size());
    }

    @Test
    void registerUser_withExistingUsername_returnsBadRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("new@example.com");
        request.setPassword("password");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        ResponseEntity<MessageResponse> response = authController.registerUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Username is already taken"));
    }

    @Test
    void registerUser_withExistingEmail_returnsBadRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        ResponseEntity<MessageResponse> response = authController.registerUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Email is already in use"));
    }

    @Test
    void registerUser_withNoRole_assignsDefaultUserRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setRole(null);

        Role userRole = new Role();
        userRole.setName(ERole.ROLE_USER);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(encoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        ResponseEntity<MessageResponse> response = authController.registerUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("User registered successfully"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_withAdminRole_assignsAdminRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("adminuser");
        request.setEmail("admin@example.com");
        request.setPassword("password");
        request.setRole(Set.of("admin"));

        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);

        when(userRepository.existsByUsername("adminuser")).thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(encoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));

        ResponseEntity<MessageResponse> response = authController.registerUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_withUserRole_assignsUserRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("regularuser");
        request.setEmail("regular@example.com");
        request.setPassword("password");
        request.setRole(Set.of("user"));

        Role userRole = new Role();
        userRole.setName(ERole.ROLE_USER);

        when(userRepository.existsByUsername("regularuser")).thenReturn(false);
        when(userRepository.existsByEmail("regular@example.com")).thenReturn(false);
        when(encoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        ResponseEntity<MessageResponse> response = authController.registerUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(any(User.class));
    }
}
