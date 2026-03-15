package com.example.writein.models;

import com.example.writein.models.entities.Election;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDateAuditTest {

    @Test
    void createdBy_getterSetter_works() {
        Election election = new Election();
        election.setCreatedBy(1L);
        assertEquals(1L, election.getCreatedBy());
    }

    @Test
    void updatedBy_getterSetter_works() {
        Election election = new Election();
        election.setUpdatedBy(2L);
        assertEquals(2L, election.getUpdatedBy());
    }
}
