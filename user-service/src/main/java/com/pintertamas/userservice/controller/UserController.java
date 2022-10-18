package com.pintertamas.userservice.controller;

import com.pintertamas.userservice.exceptions.UserExistsException;
import com.pintertamas.userservice.exceptions.UserNotFoundException;
import com.pintertamas.userservice.model.User;
import com.pintertamas.userservice.repository.UserRepository;
import com.pintertamas.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<?> getUserData(@RequestParam Long userId) {
        try {
            User user = userService.getUserData(userId);
            if (user == null) throw new UserNotFoundException(userId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUserList() {
        try {
            List<User> users = userService.getListOfUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User newUser) {
        try {
            User user = userService.register(newUser);
            log.info("USER CREATED: " + newUser);
            // todo: send email with notification service (it is going to be implemented at the end of the project)
            // if (!notificationService.sendRegistrationSuccessfulMessage(newUser.getEmail(), newUser.getUsername()))
            //    throw new Exception("Could not send Email to: " + newUser.getEmail());
            return ResponseEntity.ok(user);
        } catch (UserExistsException exception) {
            log.error("USER ALREADY EXISTS: " + exception.getExistingUser());
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            log.error("Something went wrong during registration...");
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/user-by-username/{username}")
    ResponseEntity<User> findUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findUserByUsername(username);
            if (user == null) throw new UserNotFoundException();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user-by-userId/{userId}")
    ResponseEntity<User> findUserByUserId(@PathVariable Long userId) {
        try {
            User user = userService.findUserById(userId);
            if (user == null) throw new UserNotFoundException();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
