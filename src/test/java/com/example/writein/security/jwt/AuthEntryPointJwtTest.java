package com.example.writein.security.jwt;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AuthEntryPointJwtTest {

    @Test
    void commence_setsUnauthorizedResponse() throws IOException, ServletException {
        AuthEntryPointJwt entryPoint = new AuthEntryPointJwt();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test/user");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("Bad credentials"));

        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());
        String content = response.getContentAsString();
        assertTrue(content.contains("Unauthorized"));
        assertTrue(content.contains("Bad credentials"));
        assertTrue(content.contains("/api/test/user"));
    }
}
