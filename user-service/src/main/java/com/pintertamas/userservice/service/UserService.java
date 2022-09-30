package com.pintertamas.userservice.service;

import com.pintertamas.userservice.exceptions.UserExistsException;
import com.pintertamas.userservice.exceptions.UserNotFoundException;
import com.pintertamas.userservice.model.User;
import com.pintertamas.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    public User register(User newUser) throws UserExistsException {
        if (userRepository.findUserByUsername(newUser.getUsername()) != null || userRepository.findUserByEmail(newUser.getEmail()) != null) {
            throw new UserExistsException(newUser);
        }
        newUser.setPassword(bcryptEncoder.encode(newUser.getPassword()));
        newUser.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        userRepository.save(newUser);
        return newUser;
    }

    public User getUserData(Long userId) throws UserNotFoundException {
        try {
            return userRepository.findUserById(userId);
        } catch (Exception e) {
            throw new UserNotFoundException(userId);
        }
    }

    public List<User> getListOfUsers() throws UserNotFoundException {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new UserNotFoundException();
        }
    }
}
