package com.example.writein.models;

import com.example.writein.models.entities.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void user_constructor_setsFields() {
        User user = new User("testuser", "test@example.com", "password123");
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void user_settersAndGetters_work() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("secret");
        user.setEmail("john@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserCode("U001");
        user.setStatus(EUser.Active);

        assertEquals("john", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertEquals("john@test.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("U001", user.getUserCode());
        assertEquals(EUser.Active, user.getStatus());
    }

    @Test
    void user_roles_work() {
        User user = new User();
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        roles.add(role);
        user.setRoles(roles);
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void user_election_association_works() {
        User user = new User();
        Election election = new Election();
        election.setTitle("Test Election");
        user.setElection(election);
        assertEquals("Test Election", user.getElection().getTitle());
    }

    @Test
    void election_settersAndGetters_work() {
        Election election = new Election();
        election.setCode("E001");
        election.setTitle("General Election");
        election.setElectionDate(LocalDate.of(2026, 11, 3));
        election.setAdvanceVoteDate(LocalDate.of(2026, 10, 20));
        election.setNominationPeriodDate(LocalDate.of(2026, 9, 1));
        election.setDefaultTag(true);
        election.setSerialNumber(1);
        election.setStatus(EElection.Published);

        assertEquals("E001", election.getCode());
        assertEquals("General Election", election.getTitle());
        assertEquals(LocalDate.of(2026, 11, 3), election.getElectionDate());
        assertEquals(LocalDate.of(2026, 10, 20), election.getAdvanceVoteDate());
        assertEquals(LocalDate.of(2026, 9, 1), election.getNominationPeriodDate());
        assertTrue(election.isDefaultTag());
        assertEquals(1, election.getSerialNumber());
        assertEquals(EElection.Published, election.getStatus());
    }

    @Test
    void election_defaultStatus_isPrePublished() {
        Election election = new Election();
        assertEquals(EElection.PrePublished, election.getStatus());
        assertFalse(election.isDefaultTag());
    }

    @Test
    void role_settersAndGetters_work() {
        Role role = new Role();
        role.setName(ERole.ROLE_ADMIN);
        assertEquals(ERole.ROLE_ADMIN, role.getName());
    }
}
