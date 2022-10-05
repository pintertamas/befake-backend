package com.pintertamas.userservice.controller;

import com.pintertamas.userservice.exceptions.UserExistsException;
import com.pintertamas.userservice.exceptions.UserNotFoundException;
import com.pintertamas.userservice.model.User;
import com.pintertamas.userservice.repository.UserRepository;
import com.pintertamas.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserService userService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

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
            logger.info("USER CREATED: " + newUser);
            // todo: send email with notification service (it is going to be implemented at the end of the project)
            // if (!notificationService.sendRegistrationSuccessfulMessage(newUser.getEmail(), newUser.getUsername()))
            //    throw new Exception("Could not send Email to: " + newUser.getEmail());
            return ResponseEntity.ok(user);
        } catch (UserExistsException exception) {
            logger.error("USER ALREADY EXISTS: " + exception.getExistingUser());
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            logger.error("Something went wrong during registration...");
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

}
