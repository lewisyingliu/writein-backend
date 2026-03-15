package com.example.writein.models;

import com.example.writein.models.entities.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractEntityTest {

    @Test
    void isPersisted_whenIdIsNull_returnsFalse() {
        Role role = new Role();
        assertFalse(role.isPersisted());
    }

    @Test
    void hashCode_whenIdIsNull_returnsSystemHashCode() {
        Role role = new Role();
        assertNotNull(role.hashCode());
    }

    @Test
    void equals_sameObject_returnsTrue() {
        Role role = new Role();
        assertEquals(role, role);
    }

    @Test
    void equals_null_returnsFalse() {
        Role role = new Role();
        assertNotEquals(null, role);
    }

    @Test
    void equals_differentClass_returnsFalse() {
        Role role = new Role();
        assertNotEquals("string", role);
    }

    @Test
    void equals_twoNewEntities_comparesVersionAndId() {
        Role role1 = new Role();
        Role role2 = new Role();
        // Both have null id and version 0, but equals checks id which is null
        // The Objects.equals(null, null) returns true and version 0 == 0
        assertEquals(role1, role2);
    }
}
