package com.example.writein.controllers;

import com.example.writein.models.ERole;
import com.example.writein.models.entities.User;
import com.example.writein.payload.response.MessageResponse;
import com.example.writein.repository.RoleRepository;
import com.example.writein.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.writein.utils.Constants.API_ENDPOINT;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping(API_ENDPOINT)
public class AdminUserController {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping("/adminUsers")
    public ResponseEntity<List<User>> getAllAdminUsers() {
        try {
            List<User> adminUsers = userRepository.findAdminUser();
            if (adminUsers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(adminUsers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/adminUsers/{id}")
    public ResponseEntity<User> getAdminUserById(@PathVariable("id") long id) {
        Optional<User> userData = userRepository.findById(id);
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/adminUser/{username}")
    public ResponseEntity<MessageResponse> findUserByUsername(@PathVariable String username) {
        boolean exists = userRepository.existsByUsername(username);
        if (exists) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: This string is already taken!"));
        }
        return ResponseEntity.ok(new MessageResponse("This string is fine!"));
    }

    @PostMapping("/adminUsers")
    public ResponseEntity<User> createAdminUser(@RequestBody User user) {
        try {
            user.setRoles(Collections.singleton(roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow()));
            user.setPassword(encoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/adminUsers/{id}")
    public ResponseEntity<User> updateAdminUser(@PathVariable("id") Long id, @RequestBody User user) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/adminUsers/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteAdminUser(@PathVariable("id") Long id) {
        try {
            userRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/adminUsers/deleteBatch")
    public ResponseEntity<HttpStatus> deleteInBatch(@RequestBody List<Long> ids) {
        try {
            userRepository.deleteAllByIdInBatch(ids);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
