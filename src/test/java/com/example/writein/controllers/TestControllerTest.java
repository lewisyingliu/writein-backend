package com.example.writein.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestControllerTest {

    @Test
    void allAccess_returnsPublicContent() {
        TestController controller = new TestController();
        assertEquals("Public Content.", controller.allAccess());
    }

    @Test
    void userAccess_returnsUserContent() {
        TestController controller = new TestController();
        assertEquals("User Content.", controller.userAccess());
    }

    @Test
    void adminAccess_returnsAdminBoard() {
        TestController controller = new TestController();
        assertEquals("Admin Board.", controller.adminAccess());
    }
}
