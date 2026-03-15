package com.example.writein.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void serverUrl_hasExpectedValue() {
        assertEquals("http://localhost:3000", Constants.SERVER_URL);
    }

    @Test
    void apiEndpoint_hasExpectedValue() {
        assertEquals("/api/v1/", Constants.API_ENDPOINT);
    }
}
