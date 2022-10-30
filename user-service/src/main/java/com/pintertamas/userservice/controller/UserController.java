package com.pintertamas.userservice.controller;

import com.amazonaws.services.memorydb.model.UserAlreadyExistsException;
import com.pintertamas.userservice.dto.UserDTO;
import com.pintertamas.userservice.exceptions.UserExistsException;
import com.pintertamas.userservice.exceptions.UserNotFoundException;
import com.pintertamas.userservice.exceptions.WeakPasswordException;
import com.pintertamas.userservice.model.User;
import com.pintertamas.userservice.service.JwtUtil;
import com.pintertamas.userservice.service.KafkaService;
import com.pintertamas.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final KafkaService kafkaService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, KafkaService kafkaService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.kafkaService = kafkaService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/kafka-test")
    public ResponseEntity<String> kafkaTest() {
        kafkaService.sendEmailMessage("pintertamas99@gmail.com", "Tomi");
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<User> getUserData(@RequestParam Long userId) {
        try {
            User user = userService.getUserData(userId);
            if (user == null) throw new UserNotFoundException(userId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getUserList() {
        try {
            List<User> users = userService.getListOfUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserDTO newUserDTO) {
        try {
            User newUser = newUserDTO.userFromDTO();
            User user = userService.register(newUser);
            log.info("USER CREATED: " + newUser);
            kafkaService.sendEmailMessage(newUser.getEmail(), newUser.getUsername());
            log.info("EMAIL SENT: " + newUser.getEmail());
            return ResponseEntity.ok(user);
        } catch (UserExistsException exception) {
            log.error("USER ALREADY EXISTS: " + exception.getExistingUser());
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (WeakPasswordException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception exception) {
            log.error("Something went wrong during registration...");
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping
    public ResponseEntity<User> editUser(@Valid @RequestBody UserDTO editedUserDTO, @RequestHeader HttpHeaders headers) {
        try {
            User user = jwtUtil.getUserFromToken(headers);
            User editedUser = editedUserDTO.userFromDTOWithId(user.getId());
            user = userService.updateProfile(editedUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserAlreadyExistsException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            log.error("Could not find user, you might need to log in again");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/edit-password")
    public ResponseEntity<String> editPassword(@RequestBody String password, @RequestHeader HttpHeaders headers) {
        try {
            User user = jwtUtil.getUserFromToken(headers);
            log.info(user.toString());
            userService.editPassword(user.getId(), password);
            return new ResponseEntity<>(user.getPassword(), HttpStatus.OK);
        } catch (WeakPasswordException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/upload-profile-picture")
    public ResponseEntity<User> uploadProfilePicture(
            @RequestParam(value = "picture") MultipartFile profilePicture,
            @RequestHeader HttpHeaders headers
    ) {
        try {
            User user = jwtUtil.getUserFromToken(headers);
            userService.updateProfilePicture(user.getId(), profilePicture);
            return ResponseEntity.ok(user);
        } catch (Exception exception) {
            log.error("Something went wrong during registration...");
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/profile-picture/{userId}")
    ResponseEntity<String> findUserByUsername(@PathVariable Long userId) {
        try {
            User user = userService.findUserById(userId);
            if (user == null) throw new UserNotFoundException();
            String profilePictureUrl = userService.getImageUrl(user.getProfilePicture());
            return new ResponseEntity<>(profilePictureUrl, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user-by-username/{username}")
    ResponseEntity<User> findUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findUserByUsername(username);
            if (user == null) throw new UserNotFoundException();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user-by-userId/{userId}")
    ResponseEntity<User> findUserByUserId(@PathVariable Long userId) {
        try {
            User user = userService.findUserById(userId);
            if (user == null) throw new UserNotFoundException();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
