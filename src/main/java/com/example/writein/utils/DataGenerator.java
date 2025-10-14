package com.example.writein.utils;

import com.example.writein.models.ERole;
import com.example.writein.models.EUser;
import com.example.writein.models.entities.Role;
import com.example.writein.models.entities.User;
import com.example.writein.repository.RoleRepository;
import com.example.writein.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataGenerator implements HasLogger, CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public DataGenerator(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... strings) throws Exception {
        if (roleRepository.count() == 0L) {
            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER);
            roleRepository.save(userRole);
        }
        if (userRepository.count() != 0L) {
            getLogger().info("Using existing database");
            if (userRepository.findAdminUser().size() == 0L) {
                getLogger().info("... adding admins");
                createAdmin();
            }
        } else {
            getLogger().info("Generating data");
            getLogger().info("... generating admins");
            createAdmin();
        }
    }

    private void createAdmin() {
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        userRepository.save(createUser("admin", "Administrator", "Account", encoder.encode("admin"), adminRole, EUser.Active));
    }

    private User createUser(String userName, String firstName, String lastName, String password, Role role, EUser status) {
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = new User();
        user.setUsername(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setRoles(roles);
        user.setStatus(status);
        return user;
    }
}
