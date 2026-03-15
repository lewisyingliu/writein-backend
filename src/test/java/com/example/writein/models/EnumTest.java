package com.example.writein.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumTest {

    @Test
    void eRole_hasExpectedValues() {
        assertEquals(2, ERole.values().length);
        assertEquals(ERole.ROLE_USER, ERole.valueOf("ROLE_USER"));
        assertEquals(ERole.ROLE_ADMIN, ERole.valueOf("ROLE_ADMIN"));
    }

    @Test
    void eUser_hasExpectedValues() {
        assertEquals(3, EUser.values().length);
        assertEquals(EUser.Active, EUser.valueOf("Active"));
        assertEquals(EUser.Archived, EUser.valueOf("Archived"));
        assertEquals(EUser.Locked, EUser.valueOf("Locked"));
    }

    @Test
    void eElection_hasExpectedValues() {
        assertEquals(3, EElection.values().length);
        assertEquals(EElection.PrePublished, EElection.valueOf("PrePublished"));
        assertEquals(EElection.Published, EElection.valueOf("Published"));
        assertEquals(EElection.Locked, EElection.valueOf("Locked"));
    }
}
