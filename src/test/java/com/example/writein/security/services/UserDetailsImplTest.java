package com.example.writein.security.services;

import com.example.writein.models.ERole;
import com.example.writein.models.entities.Role;
import com.example.writein.models.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void build_createsUserDetailsFromUser() {
        User user = new User("testuser", "test@example.com", "password");
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        assertEquals("testuser", userDetails.getUsername());
        assertEquals("test@example.com", userDetails.getEmail());
        assertEquals("password", userDetails.getPassword());
        assertNull(userDetails.getId());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void build_withAdminRole_createsCorrectAuthorities() {
        User user = new User("admin", "admin@example.com", "password");
        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);
        Role userRole = new Role();
        userRole.setName(ERole.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        roles.add(userRole);
        user.setRoles(roles);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        assertEquals(2, userDetails.getAuthorities().size());
    }

    @Test
    void accountStatus_methods_returnTrue() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user", "email@test.com", "pass", Set.of());

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void equals_sameId_returnsTrue() {
        UserDetailsImpl user1 = new UserDetailsImpl(1L, "user1", "email1@test.com", "pass1", Set.of());
        UserDetailsImpl user2 = new UserDetailsImpl(1L, "user2", "email2@test.com", "pass2", Set.of());

        assertEquals(user1, user2);
    }

    @Test
    void equals_differentId_returnsFalse() {
        UserDetailsImpl user1 = new UserDetailsImpl(1L, "user1", "email1@test.com", "pass1", Set.of());
        UserDetailsImpl user2 = new UserDetailsImpl(2L, "user2", "email2@test.com", "pass2", Set.of());

        assertNotEquals(user1, user2);
    }

    @Test
    void equals_sameObject_returnsTrue() {
        UserDetailsImpl user1 = new UserDetailsImpl(1L, "user1", "email1@test.com", "pass1", Set.of());
        assertEquals(user1, user1);
    }

    @Test
    void equals_null_returnsFalse() {
        UserDetailsImpl user1 = new UserDetailsImpl(1L, "user1", "email1@test.com", "pass1", Set.of());
        assertNotEquals(null, user1);
    }

    @Test
    void equals_differentClass_returnsFalse() {
        UserDetailsImpl user1 = new UserDetailsImpl(1L, "user1", "email1@test.com", "pass1", Set.of());
        assertNotEquals("string", user1);
    }

    @Test
    void getId_returnsId() {
        UserDetailsImpl user = new UserDetailsImpl(42L, "user", "email@test.com", "pass", Set.of());
        assertEquals(42L, user.getId());
    }

    @Test
    void getEmail_returnsEmail() {
        UserDetailsImpl user = new UserDetailsImpl(1L, "user", "email@test.com", "pass", Set.of());
        assertEquals("email@test.com", user.getEmail());
    }
}
