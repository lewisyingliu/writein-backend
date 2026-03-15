package com.example.writein.models;

import com.example.writein.models.entities.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateAuditTest {

    @Test
    void createdAt_getterSetter_works() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void updatedAt_getterSetter_works() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();
        user.setUpdatedAt(now);
        assertEquals(now, user.getUpdatedAt());
    }
}
