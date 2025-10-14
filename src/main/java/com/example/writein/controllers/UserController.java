package com.example.writein.controllers;

import com.example.writein.dto.UserInput;
import com.example.writein.models.ERole;
import com.example.writein.models.EUser;
import com.example.writein.models.entities.Election;
import com.example.writein.models.entities.User;
import com.example.writein.payload.response.MessageResponse;
import com.example.writein.repository.ElectionRepository;
import com.example.writein.repository.RoleRepository;
import com.example.writein.repository.UserRepository;
import com.example.writein.utils.UserPDFGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.example.writein.utils.Constants.API_ENDPOINT;
import static com.example.writein.utils.Constants.SERVER_URL;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(API_ENDPOINT)
public class UserController {
    private final Random rand = new Random();
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ElectionRepository electionRepository;

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getWriteInUserByID(@PathVariable("id") Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users/elections/{id}")
    public ResponseEntity<User> createWriteInUser(@PathVariable("id") Long id, @RequestBody User user) {
        try {
            Optional<Election> electionData = electionRepository.findById(id);
            if (electionData.isPresent()) {
                user.setElection(electionData.get());
                String password = user.getUserCode();
                user.setRoles(Collections.singleton(roleRepository.findByName(ERole.ROLE_USER).get()));
                user.setPassword(encoder.encode(password));
                User _User = userRepository.save(user);
                return new ResponseEntity<>(_User, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateWriteInUser(@PathVariable("id") Long id, @RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User _user = userOptional.get();
            String password = user.getUserCode();
            _user.setUsername(user.getUsername());
            _user.setUserCode(user.getUserCode());
            _user.setPassword(encoder.encode(password));
            _user.setStatus(user.getStatus());
            return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users/deleteBatch")
    public ResponseEntity<HttpStatus> deleteInBatch(@RequestBody List<Long> ids) {
        try {
            userRepository.deleteAllByIdInBatch(ids);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/users/printBatch", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> printBatch(@RequestBody List<Long> ids) throws IOException {
        try {
            List<User> users = userRepository.findAllById(ids);
            ByteArrayInputStream bis = UserPDFGenerator.userPDFReport(users);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=users.pdf");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> findUserByUsername(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: This string is already taken!"));
        }
        return ResponseEntity.ok(new MessageResponse("This string is fine!"));
    }

    @PostMapping("/user")
    public ResponseEntity<?> getUserByUsername(@RequestBody UserInput userInput) {
        List<User> users = userRepository.findByUsernameContaining(userInput.getInitString());
        if (!users.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: This string is already taken!"));
        }
        return ResponseEntity.ok(new MessageResponse("This string is fine!"));
    }

    @PostMapping("/users/createBatch/{id}")
    public ResponseEntity<?> createBatchUsers(@PathVariable("id") Long id, @RequestBody UserInput userInput) {
        try {
            Optional<Election> electionOptional = electionRepository.findById(id);
            int startNumber = userInput.getStartNumber();
            int endNumber = userInput.getEndNumber();
            String initString = userInput.getInitString();
            if (electionOptional.isPresent() && startNumber >= 1 && endNumber >= 2 && endNumber > startNumber) {
                for (int i = startNumber; i <= endNumber; i++) {
                    String username = initString + i;
                    String code = String.valueOf(rand.nextInt(100000000));
                    User newUser = new User();
                    newUser.setElection(electionOptional.get());
                    newUser.setUsername(username);
                    newUser.setUserCode(code);
                    newUser.setPassword(encoder.encode(code));
                    newUser.setStatus(EUser.Active);
                    newUser.setRoles(Collections.singleton(roleRepository.findByName(ERole.ROLE_USER).get()));
                    userRepository.save(newUser);
                }
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
