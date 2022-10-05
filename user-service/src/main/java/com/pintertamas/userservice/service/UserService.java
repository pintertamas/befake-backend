package com.pintertamas.userservice.service;

import com.pintertamas.userservice.exceptions.UserExistsException;
import com.pintertamas.userservice.exceptions.UserNotFoundException;
import com.pintertamas.userservice.model.User;
import com.pintertamas.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        return save(newUser);
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

    public User save(User user) {
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
